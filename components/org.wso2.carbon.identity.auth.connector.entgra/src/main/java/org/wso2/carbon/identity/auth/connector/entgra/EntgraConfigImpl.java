package org.wso2.carbon.identity.auth.connector.entgra;

import org.wso2.carbon.identity.governance.IdentityGovernanceException;
import org.wso2.carbon.identity.governance.common.IdentityConnectorConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EntgraConfigImpl implements IdentityConnectorConfig {

    public static final String ENABLE = "adaptive_authentication.entgra.enable";
    public static final String TOKEN_URL = "adaptive_authentication.entgra.token_url";
    public static final String DEVICE_INFO_URL = "adaptive_authentication.entgra.device_info_url";
    public static final String CLIENT_KEY = "adaptive_authentication.entgra.client_key";
    public static final String CLIENT_SECRET = "adaptive_authentication.entgra.client_secret";
    public static final String ANDROID_INTEGRITY_CHECK_ENABLE = "adaptive_authentication.entgra.android_integrity_enable";
    public static final String ANDROID_INTEGRITY_PACKAGE_NAME = "adaptive_authentication.entgra.android_integrity_package_name";
    public static final String ANDROID_INTEGRITY_PROJECT_ID = "adaptive_authentication.entgra.android_integrity_project_id";
    public static final String ANDROID_INTEGRITY_PRIVATE_KEY_ID = "adaptive_authentication.entgra.android_integrity_project_id";
    public static final String ANDROID_INTEGRITY_PRIVATE_KEY = "adaptive_authentication.entgra.android_integrity_project_key";
    public static final String ANDROID_INTEGRITY_CLIENT_EMAIL = "adaptive_authentication.entgra.android_integrity_client_email";
    public static final String ANDROID_INTEGRITY_CLIENT_ID = "adaptive_authentication.entgra.android_integrity_client_id";
    public static final String IOS_INTEGRITY_CHECK_ENABLE = "adaptive_authentication.entgra.enable_ios_integrity_check";

    public static final String DEFAULT_ENABLE = "true";
    public static final String DEFAULT_TOKEN_URL = "https://500.mgt.entgra.net/oauth/token";
    public static final String DEFAULT_DEVICE_INFO_URL = "https://500.gw.entgra.net/api/device-mgt/v1.0/devices/1.0.0";
    public static final String DEFAULT_CLIENT_KEY = "change-me";
    public static final String DEFAULT_CLIENT_SECRET = "change-me";
    public static final String DEFAULT_ANDROID_INTEGRITY_CHECK_ENABLE = "false";
    public static final String DEFAULT_ANDROID_INTEGRITY_PACKAGE_NAME = "change-me";
    public static final String DEFAULT_ANDROID_INTEGRITY_PROJECT_ID = "change-me";
    public static final String DEFAULT_ANDROID_INTEGRITY_PRIVATE_KEY_ID = "change-me";
    public static final String DEFAULT_ANDROID_INTEGRITY_PRIVATE_KEY = "change-me";
    public static final String DEFAULT_ANDROID_INTEGRITY_CLIENT_EMAIL = "change-me";
    public static final String DEFAULT_ANDROID_INTEGRITY_CLIENT_ID = "change-me";
    public static final String DEFAULT_IOS_INTEGRITY_CHECK_ENABLE = "false";

    @Override
    public String getName() {

        return "entgra-config";
    }

    @Override
    public String getFriendlyName() {

        return "Entgra Configuration";
    }

    @Override
    public String getCategory() {

        return "Other Settings";
    }

    @Override
    public String getSubCategory() {

        return "DEFAULT";
    }

    @Override
    public int getOrder() {

        return 11;
    }

    @Override
    public Map<String, String> getPropertyNameMapping() {

        Map<String, String> mapping = new HashMap<>();

        mapping.put(ENABLE, "Enable Entgra");
        mapping.put(TOKEN_URL, "Token URL");
        mapping.put(DEVICE_INFO_URL, "Device Information URL");
        mapping.put(CLIENT_KEY, "Client Key");
        mapping.put(ANDROID_INTEGRITY_CHECK_ENABLE, "Enable Android Integrity Check");
        mapping.put(ANDROID_INTEGRITY_PACKAGE_NAME, "Android Package Name");
        mapping.put(ANDROID_INTEGRITY_PROJECT_ID, "Google Project ID");
        mapping.put(ANDROID_INTEGRITY_PRIVATE_KEY_ID, "Google Private Key ID");
        mapping.put(ANDROID_INTEGRITY_PRIVATE_KEY, "Google Private Key");
        mapping.put(ANDROID_INTEGRITY_CLIENT_EMAIL, "Google Client Email");
        mapping.put(ANDROID_INTEGRITY_CLIENT_ID, "Google Client ID");
        mapping.put(IOS_INTEGRITY_CHECK_ENABLE, "Enable iOS Integrity Check");

        return mapping;
    }

    @Override
    public Map<String, String> getPropertyDescriptionMapping() {

        Map<String, String> mapping = new HashMap<>();

        mapping.put(ENABLE, "Enable Entgra Authentication");
        mapping.put(TOKEN_URL, "Entgra Token URL");
        mapping.put(DEVICE_INFO_URL, "Entgra Device Information URL");
        mapping.put(CLIENT_KEY, "Entgra Client Key");
        mapping.put(CLIENT_SECRET, "Entgra Client Secret");
        mapping.put(ANDROID_INTEGRITY_CHECK_ENABLE, "Enable Android Integrity Check");
        mapping.put(ANDROID_INTEGRITY_PACKAGE_NAME, "Android Package Name");
        mapping.put(ANDROID_INTEGRITY_PROJECT_ID, "Google Project ID");
        mapping.put(ANDROID_INTEGRITY_PRIVATE_KEY_ID, "Google Private Key ID");
        mapping.put(ANDROID_INTEGRITY_PRIVATE_KEY, "Google Private Key");
        mapping.put(ANDROID_INTEGRITY_CLIENT_EMAIL, "Google Client Email");
        mapping.put(ANDROID_INTEGRITY_CLIENT_ID, "Google Client ID");
        mapping.put(IOS_INTEGRITY_CHECK_ENABLE, "Enable iOS Integrity Check");

        return mapping;
    }

    @Override
    public String[] getPropertyNames() {

        List<String> properties = new ArrayList<>();
        properties.add(ENABLE);
        properties.add(TOKEN_URL);
        properties.add(DEVICE_INFO_URL);
        properties.add(CLIENT_KEY);
        properties.add(CLIENT_SECRET);
        properties.add(ANDROID_INTEGRITY_CHECK_ENABLE);
        properties.add(ANDROID_INTEGRITY_PACKAGE_NAME);
        properties.add(ANDROID_INTEGRITY_PROJECT_ID);
        properties.add(ANDROID_INTEGRITY_PRIVATE_KEY_ID);
        properties.add(ANDROID_INTEGRITY_PRIVATE_KEY);
        properties.add(ANDROID_INTEGRITY_CLIENT_EMAIL);
        properties.add(ANDROID_INTEGRITY_CLIENT_ID);
        properties.add(IOS_INTEGRITY_CHECK_ENABLE);

        return properties.toArray(new String[0]);
    }

    @Override
    public Properties getDefaultPropertyValues(String s) throws IdentityGovernanceException {

        Map<String, String> defaultProperties = new HashMap<>();
        defaultProperties.put(ENABLE, DEFAULT_ENABLE);
        defaultProperties.put(TOKEN_URL, DEFAULT_TOKEN_URL);
        defaultProperties.put(DEVICE_INFO_URL, DEFAULT_DEVICE_INFO_URL);
        defaultProperties.put(CLIENT_KEY, DEFAULT_CLIENT_KEY);
        defaultProperties.put(CLIENT_SECRET, DEFAULT_CLIENT_SECRET);
        defaultProperties.put(ANDROID_INTEGRITY_CHECK_ENABLE, DEFAULT_ANDROID_INTEGRITY_CHECK_ENABLE);
        defaultProperties.put(ANDROID_INTEGRITY_PACKAGE_NAME, DEFAULT_ANDROID_INTEGRITY_PACKAGE_NAME);
        defaultProperties.put(ANDROID_INTEGRITY_PROJECT_ID, DEFAULT_ANDROID_INTEGRITY_PROJECT_ID);
        defaultProperties.put(ANDROID_INTEGRITY_PRIVATE_KEY_ID, DEFAULT_ANDROID_INTEGRITY_PRIVATE_KEY_ID);
        defaultProperties.put(ANDROID_INTEGRITY_PRIVATE_KEY, DEFAULT_ANDROID_INTEGRITY_PRIVATE_KEY);
        defaultProperties.put(ANDROID_INTEGRITY_CLIENT_EMAIL, DEFAULT_ANDROID_INTEGRITY_CLIENT_EMAIL);
        defaultProperties.put(ANDROID_INTEGRITY_CLIENT_ID, DEFAULT_ANDROID_INTEGRITY_CLIENT_ID);
        defaultProperties.put(IOS_INTEGRITY_CHECK_ENABLE, DEFAULT_IOS_INTEGRITY_CHECK_ENABLE);

        Properties properties = new Properties();
        properties.putAll(defaultProperties);
        return properties;
    }

    @Override
    public Map<String, String> getDefaultPropertyValues(String[] strings, String s) throws IdentityGovernanceException {

        return null;
    }


}
