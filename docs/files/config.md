## Configuring Entgra with WSO2 Identity Server

- [Configuring the Entgra IoT Server](#configuring-the-entgra-iot-server)
- [Configuring the WSO2 Identity Server](#configuring-the-wso2-identity-server)
- [Configure Just-in-Time (JIT) user provisioning](#configure-just-in-Time-jit-user-provisioning)
    - [Configuring the WSO2 Identity Server for JIT Provisioning](#configuring-the-wso2-identity-server-for-jit-provisioning)
    - [Configuring the Entgra IoT Server for JIT Provisioning](#configuring-the-entgra-iot-server-for-jit-provisioning)

### Configuring the Entgra IoT Server  

1. Register the application in Entgra IoT server by using following CURL command. For the Authorization header encode 
`username:password` to Base64 format and use the encoded value as `'Authorization: Basic {encodedValue}'`. 
Provide any name for the `applicationName` and provide the application owner’s username for the `username`. Copy the 
values of `client_id` and `client_secret`.

    ```shell
    curl --location --request POST 'https://${mgtURL}/api-application-registration/register' \
        --header 'Accept: application/json' \
        --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
        --header 'Content-Type: application/json' \
        --data-raw '{"applicationName":"sp_sdk_s1","tags":["android","device_management"],"username":"admin",
                    "allowedToAllDomains":false,"mappingAnExistingOAuthApp":false}'
    ```

   Response:

   `{"client_secret":"gBb6LATYVyxplGhvB6tcckBOvo8a","client_id":"O6lYcMOwg1wl9OfhCrUDB_QTkKwa"}` </br></br>
   
2. Login to Entgra IoT management console from `https://{mgtURL}/carbon` and navigate to the **Service Providers** tab 
listed under the **Main** section. You will see the service provider you created above. </br></br>

3. Expand the **OAuth/OpenID Connect Configuration** section inside **Inbound Authentication Configuration** section 
and check if the `OAuth Client Key` is same as the `client_id` you got from the above response. </br></br>

4. Click on **Edit** button, and you will be redirected to Application Settings page.

   Make sure **Code** option in **Allowed Grant Types** and **Default** in **Token Issuer** are ticked. Provide a 
valid URL format ending with `/sdk/secure` for **Callback Url** section. </br></br>

   ![Screen Shot 2022-05-23 at 11.23.15 AM.png](https://user-images.githubusercontent.com/61885844/171582844-4ff662ea-039b-4d7f-b8d2-efa69ad7f7a2.png)

   ![Screen Shot 2022-05-23 at 11.22.28 AM.png](https://user-images.githubusercontent.com/61885844/171583041-cd088c67-fbb9-4d2a-a31d-b14e81855237.png)


### Configuring the WSO2 Identity server

1. If you haven’t downloaded WSO2 Identity server yet, please visit [https://wso2.com/identity-server/](https://wso2.
   com/identity-server/) and download the latest version of the Identity Server. </br></br>

2. Go to https://github.com/PasinduYeshan/identity-auth-entgra and follow exact steps to add Entgra connector and conditional 
   authentication function to WSO2 Identity Server. </br></br> 

3. Start the WSO2 Identity server.  Login to WSO2 IS management console from
http://localhost:9443/carbon and navigate to **Service Providers** tab listed under the Identity section. </br></br>

4. Click Add to add a new service provider. </br></br>

5. Provide a name for the service provider (ex:- ISEntgra) and click **Register**. Now you will be redirected to the 
   **Edit Service Provider** page. </br></br>

6. Expand the **Inbound Authentication Configuration** section and click **Configure** under the **OAuth/OpenID 
   Connect Configuration** section. </br></br>

7. Under **Allowed Grant Types** uncheck everything except `Code` and `Refresh Token`. </br></br>

8. Enter Callback URL(s) as for the following values.

   Callback Url: `wso2entgra://oauth2`

   > Alternatively if you’re running in an emulator, you can use `[http://10.0.2.2:8081](http://10.0.2.2:8081)` as the callback url.

9. Once the configurations are added, you will be redirected to the **Service Provider Details**
   page. Here, expand the **Inbound Authentication Configuration** section and click on the **OAuth/OpenID 
   Connect Configuration**. Copy the value of `OAuth Client Key` shown here. </br></br>

    ![Screen Shot 2022-05-30 at 1.03.15 PM.png](https://user-images.githubusercontent.com/61885844/171583227-e957efec-df32-4728-af18-004929481cfa.png) </br></br>

10. Expand the **Local & Outbound Authentication Configuration** section and select **Advanced Configuration,** Now 
    you will be redirected to **Advanced Authentication Configuration** page. </br></br>

11. Expand **Script Based Adaptive Authentication** section and paste following code and edit according to your 
    needs.

    ```jsx
    var onLoginRequest = function(context) {
        deviceID = context.request.params.deviceID[0];
        platformOS = context.request.params.platformOS[0];
                executeStep(1, {
                    onSuccess : function (context) {
                        getDeviceInfoEntgra(context, platformOS, deviceID, {
                           onSuccess : function (context, deviceInfo) {
                                if (deviceInfo) {
                                    if(deviceInfo.IS_DEV_MODE == "true") {
                                        executeStep(2);
                                    }
                                }
                               },
                            onFail : function (context, error) {
                                var errorMap = {
                                    "errorCode": error.errorCode,
                                    "errorMessage" : error.errorMessage
                                };
                                fail(errorMap);
                            }
                        });
                    }
                }); 
    };
    ```

   > In above code, inside the `getDeviceInfoEntgra` function’s `onSuccess` event handler you can access device 
   > information  as follows.
   > 1. Development mode enabled :- deviceInfo.IS_DEV_MODE
   > 2. Device is rooted :- deviceInfo.ROOTED
   > 3. ADB enabled :- deviceInfo.IS_ADB
   
   To see the Entgra adaptive authentication script documentation, Click [here](files/adaptive_script.md).

12. Expand the **Authentication Step Configuration** and click on **Add Authentication Step**  button make sure to 
    mark **Use subject identifier from this step** and **Use attributes from this step**. Then under **Local Authenticators** select an option and click on **Add authenticator** button. According to the above code there should be two authentication steps, therefore add another authentication steps and do not mark **Use subject identifier from this step** and **Use attributes from this step** options in this step.

   ![Screen Shot 2022-05-30 at 1.33.19 PM.png](https://user-images.githubusercontent.com/61885844/171583623-45b674e7-5fc9-4156-9f4e-2cf2640a243e.png)

13. Make sure to click the `Update` button to save the changes. Now you will be redirected **Service Providers** page 
   and make sure to click the `Update` button on the bottom to save all the changes. </br></br>

14. Navigate to **Identity Providers** tab listed under the Identity section. Click on **Resident** button and you 
    will be redirected to **Resident Realm Configuration.** </br></br>

15. Expand **Other Settings** and add the following configurations under **Entgra Configurations**.


    | Enable Entgra | True |
    | --- | --- |
    | Token URL | https://{hostname}/oauth2/token |
    | Device Information URL | https://{hostname}/api/device-mgt/v1.0/devices/1.0.0 |
    | Client Key | Client ID of the Service Provider created in the Entgra IoT server |
    | Client Secret | Client Secret of the Service Provider created in the Entgra IoT server |

16. Click on `Update` button to save the changes.

### Configure Just-in-Time (JIT) user provisioning

#### Configuring the WSO2 Identity Server for JIT Provisioning

1. Login to WSO2 IS management console from [http://localhost:9443/carbon](http://localhost:9443/carbon)/ and 
   navigate to **Service Providers** tab listed under the Identity section. </br></br>

2. Click Add to add a new service provider. </br></br>

3. Provide a name for the service provider (ex:- `EntgraIoTServer`) and click **Register**. Now you will be 
   redirected to the **Edit Service Provider** page. </br></br>

4. Expand the **Inbound Authentication Configuration** section and click **Configure** under the **OAuth/OpenID 
   Connect Configuration** section. </br></br>

5. Enter Callback URL(s) as for the following values. </br></br>

   Callback Url: `https://{mgtURL}/commonauth` </br></br>

6. Once the configurations are added, you will be redirected to the **Service Provider Details** page. Here, expand 
   the **Inbound Authentication Configuration** section and click on the **OAuth/OpenID Connect Configuration**. Copy the values of `OAuth Client Key` and `OAuth Client Secret` shown here. </br></br>

7. Expand **Claim Configuration** section and select `[http://wso2.org/claims/username](http://wso2.
   org/claims/username)` under **Subject Claim URI.** </br></br>

8. Click on **Add Claim URI** button and add select `[http://wso2.org/claims/groups](http://wso2.org/claims/groups)` 
   option under **Local Claim** and tick on **Mandatory Claim** as follows. </br></br>

   ![Screen Shot 2022-05-30 at 3.59.56 PM.png](https://user-images.githubusercontent.com/61885844/171583745-d68fb03f-7945-47a2-a20b-83f2300844cf.png)
   </br></br>

9. Make sure to click the `Update` button to save the changes.  </br></br>

10. Navigate to **OIDC Scope** listed under **Manage** section. Click on **list** to see all the OIDC scopes.  </br></br>

11. Click on **add claim** button of `openid` scope, then you will be redirected to **Edit associated OIDC claims 
    for the scope openid** page.  Check if `groups` are listed under the claims. If not click on **Add OIDC Claim 
    button** and select `groups` from the list then click on **Add**  button and finally click on **Finish** button 
    to save the changes.  </br></br> 

    ![Screen Shot 2022-05-30 at 8.27.49 PM.png](https://user-images.githubusercontent.com/61885844/171583897-fea1dec5-2798-4ed8-8cf6-85604985ef2f.png)
   </br></br>

12. Login to WSO2 IS management console from [https://localhost:9443/connsole/](https://localhost:9443/console/) and 
    navigate to **Groups** section listed under **Manage** section.  </br></br>

13. Click on  `+ New Group` button and add two new groups. (ex :- entgra_user, entgra_admin_group).  </br></br>

14. Create a new user and assign the user to above created groups.

#### Configuring the Entgra IoT Server for JIT Provisioning

1. Login to WSO2 IS management console from `https://{mgtURL}/carbon` and navigate to **Identity Providers** tab 
   listed under the **Main** section.  </br></br>

2. Click Add to add a new identity provider.  </br></br>

3. Provide a name for the identity provider (ex:- wso2is) and expand the **Basic Claim Configuration** under **Claim 
   Configuration.** Click on **Add Claim Mapping**  and provide `groups` as **Identity Provider Claim URI** and `
   [http://wso2.org/claims/role](http://wso2.org/claims/role)` as **Local Claim URI**. Select `groups` under **Role 
   Claim URI**.  </br></br> 

   ![Screen Shot 2022-05-30 at 6.50.52 PM.png](https://user-images.githubusercontent.com/61885844/171584028-050bb647-d31b-478b-bef9-2b7d07b352b0.png)

4. Expand **Role Configuration** and click on **Add Role Mapping** and map your identity server’s group name to 
   local role as follows.  </br></br>

   ![Screen Shot 2022-05-30 at 6.53.51 PM.png](https://user-images.githubusercontent.com/61885844/171584053-b4de4c30-e280-44b4-86a3-31b6ddda0041.png)

5. Expand **Federated Authenticators** section and add the following configurations under the **OAuth2/OpenID 
   Connect Configuration** section. 


    | Enable OAuth2/OpenIDConnect | Enable |
    | --- | --- |
    | Client Id | Client ID of the Service Provider created in the Identity Server for JIT provisioning. |
    | Client Secret | Client Secret of the Service Provider created in the Identity Server for JIT provisioning. |
    | Authorization Endpoint URL | https://localhost:9443/oauth2/authorize |
    | Token Endpoint URL | https://localhost:9443/oauth2/token |
    | Callback Url | https://{mgtURL}/commonauth |
    | Userinfo Endpoint URL | https://localhost:9443/oauth2/userinfo |
    | Logout Endpoint URL | https://localhost:9443/oidc/logout |
    | OpenID Connect User ID Location | User ID found in ‘sub’ attribute |
    | Additional Query Parameters | scope=openid |

6. Expand the **Just-in-Time Provisioning** section and enable **Provision silently** as follows.  </br></br>

   ![Screen Shot 2022-05-30 at 7.08.30 PM.png](https://user-images.githubusercontent.com/61885844/171584080-7243d9fe-32c4-4a4b-83a3-fa6aea93c428.png)
   </br></br>

7. Click on **Register** button to save the changes. 

