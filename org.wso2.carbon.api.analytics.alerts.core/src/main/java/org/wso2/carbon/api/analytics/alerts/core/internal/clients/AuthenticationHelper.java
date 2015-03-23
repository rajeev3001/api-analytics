/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.api.analytics.alerts.core.internal.clients;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthenticationHelper {
    private static final Log log = LogFactory.getLog(AuthenticationHelper.class);

    public static void authenticateStub(String sessionCookie, Stub stub) {
        long soTimeout = 5 * 60 * 1000;

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setTimeOutInMilliSeconds(soTimeout);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
        if (log.isDebugEnabled()) {
            log.debug("AuthenticateStub : Stub created with session " + sessionCookie);
        }
    }

    public static Stub authenticateStub(Stub stub, String sessionCookie, String backendURL) {
        long soTimeout = 5 * 60 * 1000;
        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setTimeOutInMilliSeconds(soTimeout);
        System.out.println("XXXXXXXXXXXXXXXXXXX" +
                backendURL + client.getServiceContext().getAxisService().getName().replaceAll("[^a-zA-Z]", ""));
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
        option.setTo(new EndpointReference(backendURL + client.getServiceContext().getAxisService().getName().replaceAll("[^a-zA-Z]", "")));
        if (log.isDebugEnabled()) {
            log.debug("AuthenticateStub : Stub created with session " + sessionCookie);
        }

        return stub;
    }

    public static void setBasicAuthHeaders(String userName, String password, Stub stub) {

        HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
        auth.setUsername(userName);
        auth.setPassword(password);
        auth.setPreemptiveAuthentication(true);
        auth.setAllowedRetry(true);

        stub._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
        stub._getServiceClient().getOptions().setManageSession(true);
        stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(10000);
    }
}