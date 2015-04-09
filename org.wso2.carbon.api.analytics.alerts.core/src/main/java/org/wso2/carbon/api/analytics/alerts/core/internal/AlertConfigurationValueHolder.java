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

import java.util.HashMap;
import java.util.Map;

public class AlertConfigurationValueHolder {

    private String inputEventAdaptorName;
    private String inputEventAdaptorType;

    private String inputMappingType;

    private String outputEventAdaptorName;
    private String outputEventAdaptorType;
    private Map<String, String> outputAdaptorProperties;

    private static AlertConfigurationValueHolder instance;

    public static AlertConfigurationValueHolder getInstance() {
        if (instance == null) {
            instance = new AlertConfigurationValueHolder();
        }
        return instance;
    }

    private AlertConfigurationValueHolder() {
        // todo read configs from file.
        inputEventAdaptorName = "DefaultWSO2EventInputAdaptor";
        inputEventAdaptorType = "wso2event";

        outputEventAdaptorName = "emailOut";
        outputEventAdaptorType = "email";

        inputMappingType = "wso2event";

        outputAdaptorProperties = new HashMap<String, String>();
        outputAdaptorProperties.put("email.address", "demo.qsp@gmail.com");
        outputAdaptorProperties.put("email.subject", "api analytics alerts");
    }


    public Map<String, String> getOutputAdaptorProperties() {
        return outputAdaptorProperties;
    }

    public String getInputEventAdaptorName() {
        return inputEventAdaptorName;
    }


    public String getInputEventAdaptorType() {
        return inputEventAdaptorType;
    }

//    public String getOutputEventAdaptorName() {
//        return outputEventAdaptorName;
//    }
//
//    public String getOutputEventAdaptorType() {
//        return outputEventAdaptorType;
//    }

    public String getInputMappingType() {
        return inputMappingType;
    }
}
