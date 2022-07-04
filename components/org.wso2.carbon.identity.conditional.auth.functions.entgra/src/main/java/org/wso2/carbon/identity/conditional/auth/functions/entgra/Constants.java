package org.wso2.carbon.identity.conditional.auth.functions.entgra;

public abstract class Constants {
    // Connector configurations constants
    public static final String TOKEN_URL = "adaptive_authentication.entgra.token_url";
    public static final String DEVICE_INFO_URL = "adaptive_authentication.entgra.device_info_url";
    public static final String CLIENT_KEY = "adaptive_authentication.entgra.client_key";
    public static final String CLIENT_SECRET = "adaptive_authentication.entgra.client_secret";

    // HTTP related constants
    public static final String TYPE_APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String TYPE_APPLICATION_JSON = "application/json";
    public static final String ACCESS_TOKEN = "access_token";

    // Authentication Response Error Code
    public static enum AuthResponseErrorCode {
        ACCESS_DENIED,
        DEVICE_NOT_ENROLLED,
        DEVICE_NOT_ENROLLED_UNDER_CURRENT_USER,
        NETWORK_ERROR,
        INTERNAL_SERVER_ERROR
    }

    /*
     * Google Play Integrity API Configurations
     */
    // Connector configs
    public static final String ANDROID_INTEGRITY_CHECK_ENABLE = "adaptive_authentication.entgra.android_integrity_enable";
    public static final String ANDROID_INTEGRITY_PACKAGE_NAME = "adaptive_authentication.entgra.android_integrity_package_name";
    public static final String ANDROID_INTEGRITY_PROJECT_ID = "adaptive_authentication.entgra.android_integrity_project_id";
    public static final String ANDROID_INTEGRITY_PRIVATE_KEY_ID = "adaptive_authentication.entgra.android_integrity_project_id";
    public static final String ANDROID_INTEGRITY_PRIVATE_KEY = "adaptive_authentication.entgra.android_integrity_project_key";
    public static final String ANDROID_INTEGRITY_CLIENT_EMAIL = "adaptive_authentication.entgra.android_integrity_client_email";
    public static final String ANDROID_INTEGRITY_CLIENT_ID = "adaptive_authentication.entgra.android_integrity_client_id";

    // Constants
    public static final String ANDROID_INTEGRITY_AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
    public static final String ANDROID_INTEGRITY_TOKEN_URI = "https://oauth2.googleapis.com/token";
    public static final String ANDROID_INTEGRITY_AUTH_PROVIDER_X509_CERT_URL = "https://www.googleapis.com/oauth2/v1/certs";
    public static final String ANDROID_INTEGRITY_CLIENT_X509_CERT_URL = "https://www.googleapis.com/robot/v1/metadata/x509/isentgra%40isentgra.iam.gserviceaccount.com";

    public static enum AppRecognitionVerdict {
        PLAY_RECOGNIZED,
        UNRECOGNIZED_VERSION,
        UNEVALUATED
    }

    public static enum DeviceRecognitionVerdict {
        MEETS_BASIC_INTEGRITY,
        MEETS_STRONG_INTEGRITY,
        MEETS_VIRTUAL_INTEGRITY
    }
    public static enum LicensingVerdict {
        LICENSED,
        UNLICENSED,
        UNEVALUATED
    }

    /*
     * iOS's integrity API check Configurations
     */
    public static final String IOS_INTEGRITY_CHECK_ENABLE = "adaptive_authentication.entgra.enable_ios_integrity_check";


}
