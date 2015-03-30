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


package org.wso2.carbon.api.analytics.alerts.admin;

import org.wso2.carbon.api.analytics.alerts.core.DerivedAttribute;

public class AlertConfigurationDto {

    private AlertConfigurationConditionDto[] conditions;
    private DerivedAttributeDto[] derivedAttributes;

    private String attributeDefinitions;
    private String configurationId;
    private String inputStreamId;
    private String outputMapping;

    public String getConfigurationId() {
        return configurationId;
    }

    public void setOutputMapping(String outputMapping) {
        this.outputMapping = outputMapping;
    }

    public String getOutputMapping() {
        return outputMapping;
    }


    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public AlertConfigurationConditionDto[] getConditions() {
        return conditions;
    }

    public void setConditions(AlertConfigurationConditionDto[] conditions) {
        this.conditions = conditions;
    }

    public String getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public void setAttributeDefinitions(String attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    public DerivedAttributeDto[] getDerivedAttributes() {
        return derivedAttributes;
    }

    public void setDerivedAttributes(DerivedAttributeDto[] derivedAttributes) {
        this.derivedAttributes = derivedAttributes;
    }

    public String getInputStreamId() {
        return inputStreamId;
    }

    public void setInputStreamId(String inputStreamId) {
        this.inputStreamId = inputStreamId;
    }
}
