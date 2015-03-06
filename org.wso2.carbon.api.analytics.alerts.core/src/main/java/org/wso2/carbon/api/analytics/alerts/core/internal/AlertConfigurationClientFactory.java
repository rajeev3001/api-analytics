/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.api.analytics.alerts.core.internal;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.api.analytics.alerts.core.internal.clients.*;

public class AlertConfigurationClientFactory {

    private String backendUrl;
    private String username;
    private String password;

    private static AlertConfigurationClientFactory instance;

    private EventBuilderAdminServiceClient builderClient;
    private EventFormatterAdminServiceClient formatterClient;
    private EventProcessorAdminServiceClient processorClient;
    private InputEventAdaptorManagerAdminServiceClient inputAdaptorClient;
    private OutputEventAdaptorManagerAdminServiceClient outputAdaptorClient;
    private EventStreamManagerAdminServiceClient streamManagerClient;

    public static AlertConfigurationClientFactory getInstance() {
        if (instance == null) {
            instance = new AlertConfigurationClientFactory();
        }
        return instance;
    }

    public EventBuilderAdminServiceClient getEventBuilderAdminServiceClient() throws AxisFault {
        if (this.builderClient == null) {
            builderClient = new EventBuilderAdminServiceClient(backendUrl, username, password);
        }
        return builderClient;
    }


    public EventFormatterAdminServiceClient getEventFormatterAdminServiceClient() throws AxisFault {
        if (this.formatterClient == null) {
            formatterClient = new EventFormatterAdminServiceClient(backendUrl, username, password);
        }
        return formatterClient;
    }


    public EventProcessorAdminServiceClient getEventProcessorAdminServiceClient() throws AxisFault {
        if (this.processorClient == null) {
            processorClient = new EventProcessorAdminServiceClient(backendUrl, username, password);
        }
        return processorClient;
    }


    public InputEventAdaptorManagerAdminServiceClient getInputEventAdaptorAdminServiceClient() throws AxisFault {
        if (this.inputAdaptorClient == null) {
            inputAdaptorClient = new InputEventAdaptorManagerAdminServiceClient(backendUrl, username, password);
        }
        return inputAdaptorClient;
    }

    public OutputEventAdaptorManagerAdminServiceClient getOutputEventAdaptorAdminServiceClient() throws AxisFault {
        if (this.outputAdaptorClient == null) {
            outputAdaptorClient = new OutputEventAdaptorManagerAdminServiceClient(backendUrl, username, password);
        }
        return outputAdaptorClient;
    }

    public EventStreamManagerAdminServiceClient getEventStreamManagerAdminServiceClient() throws AxisFault {
        if (this.streamManagerClient == null) {
            streamManagerClient = new EventStreamManagerAdminServiceClient(backendUrl, username, password);
        }
        return streamManagerClient;
    }

    private AlertConfigurationClientFactory() {

        // todo configure these params from apimgt.xml

        backendUrl = "https://10.100.0.121:9445/services/";
        username = "admin";
        password = "admin";
    }


}
