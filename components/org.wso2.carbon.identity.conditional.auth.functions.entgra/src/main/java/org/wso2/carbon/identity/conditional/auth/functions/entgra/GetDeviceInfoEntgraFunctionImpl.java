package org.wso2.carbon.identity.conditional.auth.functions.entgra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.wso2.carbon.identity.application.authentication.framework.AsyncProcess;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsGraphBuilder;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.conditional.auth.functions.common.utils.CommonUtils;
import org.wso2.carbon.identity.conditional.auth.functions.common.utils.ConfigProvider;
import org.wso2.carbon.identity.conditional.auth.functions.entgra.exception.EntgraConnectorException;
import org.wso2.carbon.identity.event.IdentityEventException;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

import static org.apache.http.HttpHeaders.*;
import static org.wso2.carbon.identity.conditional.auth.functions.common.utils.Constants.OUTCOME_SUCCESS;
import static org.wso2.carbon.identity.conditional.auth.functions.common.utils.Constants.OUTCOME_FAIL;
import static org.wso2.carbon.identity.conditional.auth.functions.common.utils.Constants.OUTCOME_TIMEOUT;

import com.google.api.services.playintegrity.v1.PlayIntegrity;
import com.google.api.services.playintegrity.v1.PlayIntegrityRequestInitializer;
import com.google.api.services.playintegrity.v1.model.DecodeIntegrityTokenRequest;
import com.google.api.services.playintegrity.v1.model.DecodeIntegrityTokenResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.oauth2.GoogleCredentials;



/**
 * Implementation of the {@link GetDeviceInfoEntgraFunction}
 */
public class GetDeviceInfoEntgraFunctionImpl implements GetDeviceInfoEntgraFunction {

    private static final Log LOG = LogFactory.getLog(GetDeviceInfoEntgraFunctionImpl.class);
    private CloseableHttpClient client;

    public GetDeviceInfoEntgraFunctionImpl() {

        super();
        // Configure Http Client
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(ConfigProvider.getInstance().getConnectionTimeout())
                .setConnectionRequestTimeout(ConfigProvider.getInstance().getConnectionRequestTimeout())
                .setSocketTimeout(ConfigProvider.getInstance().getReadTimeout())
                .setRedirectsEnabled(false)
                .setRelativeRedirectsAllowed(false)
                .build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

    }

    @Override
    public void getDeviceInfoEntgra(JsAuthenticationContext context, String platformOS, String deviceID, String integrityToken, Map<String, Object> eventHandlers) throws EntgraConnectorException {

        try {
            JsAuthenticatedUser user = Util.getUser(context);
            String tenantDomain = user.getWrapped().getTenantDomain();
            String username = user.getWrapped().getUserName();

            // Getting connector configurations
            String clientKey = CommonUtils.getConnectorConfig(Constants.CLIENT_KEY, tenantDomain);
            String clientSecret = CommonUtils.getConnectorConfig(Constants.CLIENT_SECRET, tenantDomain);
            String tokenURL = CommonUtils.getConnectorConfig(Constants.TOKEN_URL, tenantDomain);
            String deviceInfoBaseURL = CommonUtils.getConnectorConfig(Constants.DEVICE_INFO_URL, tenantDomain);
//            String integrityCheckEnabled = CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_CHECK_ENABLE, tenantDomain) != "" ? CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_CHECK_ENABLE, tenantDomain) : "true";
            Boolean androidIntegrityCheckEnabled = true;


            AsyncProcess asyncProcess = new AsyncProcess((authenticationContext, asyncReturn) -> {

                String outcome;
                JSONObject response = null;
                String nonceDeviceID = "";
                String deviceInfoURL = deviceInfoBaseURL + "/" + platformOS + "/" + deviceID;;

                // Check application integrity
                if ("android".equals(platformOS)) {

                    if (androidIntegrityCheckEnabled) {

                        Map<String, Object> result = IntegrityCheck.checkAndroidApplicationIntegrity(tenantDomain, integrityToken, LOG);
                        Boolean isIntegrityCheckPassed = (Boolean) result.get("isIntegrityCheckPassed");
                        if (!isIntegrityCheckPassed) {

                            outcome = OUTCOME_FAIL;
                            asyncReturn.accept(authenticationContext, response != null ? response : Collections.emptyMap(), outcome);
                            return;
                        } else {

                            nonceDeviceID = (String) result.get("deviceID");
                            // Check if the device id sent along with the auth request is equals to nonce's device id.
                            if (!nonceDeviceID.equals(deviceID)) {

                                outcome = OUTCOME_FAIL;
                                asyncReturn.accept(authenticationContext, response != null ? response : Collections.emptyMap(), outcome);
                            }

                        }

                    }
                } else if ("ios".equals(platformOS)) {

                    // Implementation of iOS integrity check
                }


                try {
                    HttpPost tokenRequest = getTokenRequest(tokenURL, clientKey, clientSecret);

                    // For catching and logging error
                    String errorURL = tokenURL;
                    try (CloseableHttpResponse tResponse = client.execute(tokenRequest)) {
                        int tokenResponseCode = tResponse.getStatusLine().getStatusCode();

                        if (tokenResponseCode >= 200 && tokenResponseCode < 300) {
                            String tJsonString = EntityUtils.toString(tResponse.getEntity());
                            JSONParser parser = new JSONParser();
                            JSONObject jsonTokenResponse = (JSONObject) parser.parse(tJsonString);
                            String accessToken = (String) jsonTokenResponse.get(Constants.ACCESS_TOKEN);

                            HttpGet deviceInfoRequest = getDeviceInfoRequest(deviceInfoURL, accessToken);

                            tResponse.close(); // Closing the CloseableHttpResponse before starting new one
                            try (CloseableHttpResponse dResponse = client.execute(deviceInfoRequest)) {
                                int dResponseCode = dResponse.getStatusLine().getStatusCode();

                                if (dResponseCode >= 200 && dResponseCode < 300) {
                                    String dJsonString = EntityUtils.toString(dResponse.getEntity());
                                    JSONObject jsonDeviceInfoResponse = (JSONObject) parser.parse(dJsonString);
                                    String enrolledUser = (String) ((JSONObject) jsonDeviceInfoResponse.get("enrolmentInfo")).get("owner");
                                    String enrollmentStatus = (String) ((JSONObject) jsonDeviceInfoResponse.get("enrolmentInfo")).get("status");

                                    // Check if the device is enrolled to current user
                                    if ("REMOVED".equals(enrollmentStatus)) {
                                        outcome = OUTCOME_FAIL;
                                        response = Util.getErrorJsonObject(Constants.AuthResponseErrorCode.DEVICE_NOT_ENROLLED, "Device is not recognized. Please register your device.");
                                    } else if (username.equalsIgnoreCase(enrolledUser)) {
                                        outcome = OUTCOME_SUCCESS;
                                        response = (JSONObject) ((JSONObject) jsonDeviceInfoResponse.get("deviceInfo")).get("deviceDetailsMap");
                                    } else {
                                        outcome = OUTCOME_FAIL;
                                        response = Util.getErrorJsonObject(Constants.AuthResponseErrorCode.DEVICE_NOT_ENROLLED_UNDER_CURRENT_USER, "Access is denied. Please contact your administrator.");
                                    }
                                } else {

                                    LOG.error("Error while fetching device information from Entgra Server. Response code: " + dResponseCode);
                                    outcome = OUTCOME_FAIL;
                                }
                            } catch (Exception e) {
                                errorURL = deviceInfoURL;
                                throw e;
                            }

                        } else if (tokenResponseCode == 404) {
                            LOG.error("Error while requesting access token from Entgra Server. Response code: " + tokenResponseCode);
                            outcome = OUTCOME_FAIL;
                            response = Util.getErrorJsonObject(Constants.AuthResponseErrorCode.DEVICE_NOT_ENROLLED, "Device is not recognized. Please register your device.");

                        } else {
                            LOG.error("Error while requesting access token from Entgra Server. Response code: " + tokenResponseCode);
                            outcome = OUTCOME_FAIL;
                        }
                    } catch (IllegalArgumentException e) {
                        LOG.error("Invalid Url: " + errorURL, e);
                        outcome = OUTCOME_FAIL;
                    } catch (ConnectTimeoutException e) {
                        LOG.error("Error while waiting to connect to " + errorURL, e);
                        outcome = OUTCOME_TIMEOUT;
                    } catch (SocketTimeoutException e) {
                        LOG.error("Error while waiting for data from " + errorURL, e);
                        outcome = OUTCOME_TIMEOUT;
                    } catch (IOException e) {
                        LOG.error("Error while calling endpoint. ", e);
                        outcome = OUTCOME_FAIL;
                    } catch (ParseException e) {
                        LOG.error("Error while parsing response. ", e);
                        outcome = OUTCOME_FAIL;
                    }
                } catch (Exception e) {
                    outcome = OUTCOME_FAIL;
                    LOG.error("Error while generating request.");
                }

                // If outcome fails and response is null, set error object as response
                if (outcome.equals(OUTCOME_FAIL) && response == null) {
                    response = Util.getErrorJsonObject(Constants.AuthResponseErrorCode.ACCESS_DENIED, "Access is denied. Please contact your administrator.");
                }

                asyncReturn.accept(authenticationContext, response != null ? response : Collections.emptyMap(), outcome);
            });
            JsGraphBuilder.addLongWaitProcess(asyncProcess, eventHandlers);

        } catch (IdentityEventException e) {
            throw new EntgraConnectorException("Can not retrieve configurations from tenant.", e);
        }

    }

    /**
     * Return http request for authorization token
     *
     * @param tokenURL     Token endpoint of Entgra IoT server
     * @param clientKey    Client key given by SP of Entgra IoT server
     * @param clientSecret Client secret given by SP of Enthra IoT server
     * @return HttpPost request
     */
    private HttpPost getTokenRequest(String tokenURL, String clientKey, String clientSecret) {

        HttpPost request = new HttpPost(tokenURL);

        // Creating basic authorization header value
        String basicAuthString = clientKey + ":" + clientSecret;
        String tokenRequestAuthorizationHeader = "Basic " + Base64.getEncoder().encodeToString(basicAuthString.getBytes(StandardCharsets.UTF_8));

        // Setting request headers for token request
        request.setHeader(CONTENT_TYPE, Constants.TYPE_APPLICATION_FORM_URLENCODED);
        request.setHeader(AUTHORIZATION, tokenRequestAuthorizationHeader);

        // Setting request body for the token request
        List<NameValuePair> tokenRequestPayload = new ArrayList<>();
        tokenRequestPayload.add(new BasicNameValuePair("grant_type", "client_credentials"));
        tokenRequestPayload.add(new BasicNameValuePair("scope", "default perm:devices:details perm:devices:view"));

        request.setEntity(new UrlEncodedFormEntity(tokenRequestPayload, StandardCharsets.UTF_8));
        return request;
    }

    /**
     * Return http request for device information fetching
     *
     * @param deviceInfoURL Device information fetching endpoint of Entgra IoT Server
     * @param accessToken   Access token received by the Entgra IoT server
     * @return HttpGet request
     */
    private HttpGet getDeviceInfoRequest(String deviceInfoURL, String accessToken) {

        HttpGet request = new HttpGet(deviceInfoURL);

        request.setHeader(ACCEPT, Constants.TYPE_APPLICATION_JSON);
        request.setHeader(AUTHORIZATION, "Bearer " + accessToken);

        return request;
    }



}
