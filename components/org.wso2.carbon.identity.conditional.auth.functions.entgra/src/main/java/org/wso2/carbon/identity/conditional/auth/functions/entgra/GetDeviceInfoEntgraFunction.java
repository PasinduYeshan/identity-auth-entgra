package org.wso2.carbon.identity.conditional.auth.functions.entgra;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.conditional.auth.functions.entgra.exception.EntgraConnectorException;

import java.util.Map;

/**
 * Function to get device information from Entgra IoT server
 */
@FunctionalInterface
public interface GetDeviceInfoEntgraFunction {

    /**
     * Get device information from Entgra IoT server
     * @param context       Context from authentication flow
     * @param osPlatform    Device's operating system
     * @param deviceID      Device identifier
     * @param eventHandlers Event handlers
     * @throws EntgraConnectorException When unable to retrieve tenant configuration
     */
    void getDeviceInfoEntgra(JsAuthenticationContext context, String osPlatform, String deviceID, String integrityToken, Map<String, Object> eventHandlers)
            throws EntgraConnectorException;
}
