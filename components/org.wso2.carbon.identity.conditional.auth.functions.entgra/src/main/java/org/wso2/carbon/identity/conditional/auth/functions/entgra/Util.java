package org.wso2.carbon.identity.conditional.auth.functions.entgra;

import org.json.simple.JSONObject;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;

public abstract class Util {

    /**
     * Function that is used get the user from the context.
     *
     * @param context Context from authentication flow.
     * @return User object respect to the authentication.
     */
    public static JsAuthenticatedUser getUser(JsAuthenticationContext context) {

        return (JsAuthenticatedUser) context.getMember("currentKnownSubject");
    }

    /**
     * Return Error Json Object
     * @param errorCode
     * @param errorMessage
     * @return errorMap JSONObject
     */
    public static JSONObject getErrorJsonObject(Constants.AuthResponseErrorCode errorCode, String errorMessage) {

        JSONObject errorMap = new JSONObject();
        errorMap.put("errorCode", errorCode);
        errorMap.put("errorMessage", errorMessage);
        return errorMap;
    }

}
