package org.wso2.carbon.identity.conditional.auth.functions.entgra;

import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.playintegrity.v1.PlayIntegrity;
import com.google.api.services.playintegrity.v1.PlayIntegrityRequestInitializer;
import com.google.api.services.playintegrity.v1.model.DecodeIntegrityTokenRequest;
import com.google.api.services.playintegrity.v1.model.DecodeIntegrityTokenResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.apache.commons.logging.Log;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.wso2.carbon.identity.conditional.auth.functions.common.utils.CommonUtils;
import org.wso2.carbon.identity.conditional.auth.functions.entgra.exception.EntgraConnectorException;
import org.wso2.carbon.identity.event.IdentityEventException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wso2.carbon.identity.conditional.auth.functions.entgra.exception.EntgraConnectorException;

public abstract class IntegrityCheck {

    /**
     * Check Android application's integrity by the integrity token and return a Map
     * @param tenantDomain      Current logged in user's tenant domain
     * @param integrityToken    Integrity token received from the mobile application
     * @param LOG               Logger
     * @return output           Map<String, Object>
     *     isIntegrityCheckPassed   Boolean
     *     deviceID                 String
     */
    public static Map<String, Object> checkAndroidApplicationIntegrity(String tenantDomain, String integrityToken, Log LOG) {

        Boolean isIntegrityCheckPassed = true;
        Map<String, Object> output = new HashMap<String, Object>();

        try {
            String packageName = CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_PACKAGE_NAME, tenantDomain);
            String projectID = CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_PROJECT_ID, tenantDomain);
            String privateKeyID = CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_PRIVATE_KEY_ID, tenantDomain);
            String privateKey = CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_PRIVATE_KEY, tenantDomain);
            String clientEmail = CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_CLIENT_EMAIL, tenantDomain);
            String clientID = CommonUtils.getConnectorConfig(Constants.ANDROID_INTEGRITY_CLIENT_ID, tenantDomain);

            try {
                // Create service account json
                JSONObject serviceAccountJson = new JSONObject();
                serviceAccountJson.put("type", "service_account");
                serviceAccountJson.put("project_id", projectID);
                serviceAccountJson.put("private_key_id", privateKeyID);
                serviceAccountJson.put("private_key", privateKey);
                serviceAccountJson.put("client_email", clientEmail);
                serviceAccountJson.put("client_id", clientID);
                serviceAccountJson.put("auth_uri", Constants.ANDROID_INTEGRITY_AUTH_URI);
                serviceAccountJson.put("token_uri", Constants.ANDROID_INTEGRITY_TOKEN_URI);
                serviceAccountJson.put("auth_provider_x509_cert_url", Constants.ANDROID_INTEGRITY_AUTH_PROVIDER_X509_CERT_URL);
                serviceAccountJson.put("client_x509_cert_url", Constants.ANDROID_INTEGRITY_CLIENT_X509_CERT_URL);

//                serviceAccountJson.put("type","service_account");
//                serviceAccountJson.put("project_id","isentgra");
//                serviceAccountJson.put("private_key_id","12aff63e2662e1f314cca3ab32c4724c5c643dbf");
//                serviceAccountJson.put("private_key","-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDAMn0vP91WnaOh\nI4rqXgspigrYxjJnDFa/3lhaDCtwB4oAz4x6RJOO/8cxEGBM6DMc5hFH0GIM8ia1\nLET3sEiW+p9yX0s224LdX4pnPoB6sHcLQAuvBkattelFKPXcN6drt9ly2XaonTVy\nldqGV1bmZyLS+x75YfZ+q8dynmzmKYBwmEVUrZlIUcO9BXdk7d1YI8T8qPQTvBmP\n5TgSuCCt+4aqCvByfx1ylwKt1eb+bHqlJ+wXM6gZW6S1HoWwsN2gfGPnlQsODLQV\nz737vpLKXBhDXgXA/ImvQcnovS7ovfZQlpfOI2pOHWl0j2quo3HI0KKn6K0V06eN\nlAIhJ1n1AgMBAAECggEAC5S/QFKb1rVG/9WxcC/cglNEFg3TSaxPqd4O+tYS4/xA\nT44yC8gIECzPZuByvQ+czNf8IJSo49jPhkk1mqsvpJFTfEPLt3GAsDAqmdAFU99A\nPRhN2+0H4z274aP3NJ7R4sbAPpBl+wEGEHKBKWkn3lhGm+nv0t0ZSka1jO2I6FFr\nDbXD+yskvwVWhlqoUpzZYtx89LHQ0Y7zHdBfqIycwFImVWqC2SbKNXTt/AFsnRjc\n0oyczaedrG6qwMrTk7R5R45/nRP+P2Sq0+Y7FBVSOCuhEG8nd81lSYRgeTudUL1B\n23V9xHs2Nnry0GF+4LNDiGPlCksrg/23YZolnzUF4QKBgQDxF9pgPdcU1QhF54Yg\nTNDX9avGXcksOygf1pojVAZVV8dNxb/0P+pP2IUwusOgJJMF/poDcCVaLytQgPqT\nIm3cpvFNf0POQHD+2PU/v/Qwey/OoPMGaPowoZoV2dwW8ZQj9CxR/+/iB5GKllsu\nIbQnbe4jwnir1lPtUmZl/pevSQKBgQDMFK+F/jIfcJ+s6wINLKb407qVVJkFiWAB\ndBdy0C4csshfjnj+rs/3ZiJzsidOJH+tFntH3Yw9tH2GerLuTrp66VnfhuBJ34GA\n82a23yCmBWkDz+l6+IdNIYOH3ylGW7yuE5ei9oZV7OF8w5rnrg1KP293K2/CCB89\nzvLMNp2ZTQKBgBjpkhMn0LXCXZx7lAx6Y4otRJa20jbT1g2UK1FDOXRQIbQTnwYq\nJJgjk3+5jMVbgiW4bUm1qOZswVkMOthBUoDeb0jGwcbqOcLbwFvaBe2MjSAY2YPs\nDQ5BZJ6laGymgdxFOt8uRydgCNX9O2bE7My2O0lOqxF2kC2RuisH57yhAoGAX9xX\nllkQsjP0zQSWshmHVNofioVyW2dr8ULWwNeI0XIpLLl/nCIBDfSruiy0yb/bOTKW\nNeubNGDTlZeM9OOGNLXnwVdZg1m5OxkZ5kxoyZNBAMloopqa82AKqIfqm2H7/si1\nMZcbAsGCKVi4KptXIskpUpDUrg+inSXYZpRA8wECgYEA7ur5IQGSKX/4zz9/5bQT\nzfAltaczhCpV+0Np9eXY05urHDkWHANpCIbNj+5fs2Tfd4y5CIC7WPVxIsW30Wsv\nzc3qlxDXVIuTqHiiKsDbk2WXTtpy7ymfu7vhocBkuCFJe/dyHtUV1RsXvxqYThZ6\n2yXt9gVdiK0AmekAjCLL1r0=\n-----END PRIVATE KEY-----\n");
//                serviceAccountJson.put("client_email","isentgra@isentgra.iam.gserviceaccount.com");
//                serviceAccountJson.put("client_id","106698510585745305770");
//                serviceAccountJson.put("auth_uri","https://accounts.google.com/o/oauth2/auth");
//                serviceAccountJson.put("token_uri","https://oauth2.googleapis.com/token");
//                serviceAccountJson.put("auth_provider_x509_cert_url","https://www.googleapis.com/oauth2/v1/certs");
//                serviceAccountJson.put("client_x509_cert_url","https://www.googleapis.com/robot/v1/metadata/x509/isentgra%40isentgra.iam.gserviceaccount.com");

                DecodeIntegrityTokenRequest requestObj = new DecodeIntegrityTokenRequest();
                requestObj.setIntegrityToken(integrityToken);

                InputStream is = new ByteArrayInputStream(serviceAccountJson.toString().getBytes());
                GoogleCredentials credentials = GoogleCredentials.fromStream(is);
                HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

                HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                JsonFactory JSON_FACTORY = new GsonFactory();
                GoogleClientRequestInitializer initializer = new PlayIntegrityRequestInitializer();

                PlayIntegrity.Builder  playIntegrity = new PlayIntegrity.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer).setApplicationName("com.isentgraa").setGoogleClientRequestInitializer(initializer);
                PlayIntegrity play = playIntegrity.build();
                DecodeIntegrityTokenResponse integrityTokenResponse = play.v1().decodeIntegrityToken(packageName, requestObj).execute();

                String licensingVerdict = integrityTokenResponse.getTokenPayloadExternal().getAccountDetails().getAppLicensingVerdict();
                String nonce = integrityTokenResponse.getTokenPayloadExternal().getRequestDetails().getNonce();
                String appRecognitionVerdict = integrityTokenResponse.getTokenPayloadExternal().getAppIntegrity().getAppRecognitionVerdict();
                String integrityTokenPackageName = integrityTokenResponse.getTokenPayloadExternal().getAppIntegrity().getPackageName();
                List deviceRecognitionVerdict = integrityTokenResponse.getTokenPayloadExternal().getDeviceIntegrity().getDeviceRecognitionVerdict();

                if (!integrityTokenPackageName.equalsIgnoreCase(packageName)) {
                    isIntegrityCheckPassed = false;
                } else if (Constants.AppRecognitionVerdict.UNRECOGNIZED_VERSION.equals(appRecognitionVerdict)) {
                    isIntegrityCheckPassed = false;
                } else if (Constants.AppRecognitionVerdict.UNEVALUATED.equals(appRecognitionVerdict)) {
                    isIntegrityCheckPassed = false;
                } else if (Constants.LicensingVerdict.UNLICENSED.equals(licensingVerdict)) {
                    isIntegrityCheckPassed = false;
                } else {
                    // Decode nonce and get device identifier and unique value
                    Base64.Decoder decoder = Base64.getUrlDecoder();
                    String decodedNonce = new String(decoder.decode(nonce));
                    JSONParser parser = new JSONParser();
                    JSONObject jsonNonce = (JSONObject) parser.parse(decodedNonce);

                    String deviceID = (String) jsonNonce.get("deviceID");
                    String uniqueValue = (String) jsonNonce.get("uniqueValue");
                    output.put("deviceID", deviceID);
                }
            } catch (Error | IOException e) {
                LOG.error(e);
                isIntegrityCheckPassed = false;
                // LICENSE error
            } catch (Exception e) {
                LOG.error(e);
                isIntegrityCheckPassed = false;
                // LICENSE error
            }

        } catch (IdentityEventException e) {
            LOG.error("Can not retrieve configurations from tenant.", e);
        } finally {
            output.put("isIntegrityCheckPassed", isIntegrityCheckPassed);
            return output;
        }

    }

    /**
     * Check iOS application's integrity by the integrity token and return a Map
     * @param tenantDomain      Current logged in user's tenant domain
     * @param attestation       Attestation received from the mobile application
     * @param LOG               Logger
     * @return output           Map<String, Object>
     *     isIntegrityCheckPassed   Boolean
     *     deviceID                 String
     */
    public static Map<String, Object> checkIOSApplicationIntegrity(String tenantDomain, String attestation, Log LOG) {

        Boolean isIntegrityCheckPassed = true;
        Map<String, Object> output = new HashMap<String, Object>();
        try {
            // Implement iOS application integrity check

        } catch (Exception e) {
            LOG.error(e);
        } finally {
            output.put("isIntegrityCheckPassed", isIntegrityCheckPassed);
            return output;
        }

    }
}
