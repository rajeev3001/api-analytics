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

package org.wso2.carbon.api.analytics.alerts.core;

import org.wso2.carbon.databridge.commons.StreamDefinition;

import java.util.List;
import java.util.Map;

public class AlertConfiguration {


    // this can be the chart id
    private String configurationId;

    private List<DerivedAttribute> derivedAttributes;
    private List<AlertConfigurationCondition> conditions;
    private StreamDefinition streamDefinition;
    private String inputStreamId;

    // todo infer these from input and conditions
    private Map<String, String> outputAttributes;
    private String outputStream;
    private String outputMapping;

    private String endpoint;
    private String outputAdaptorType;

    public List<AlertConfigurationCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<AlertConfigurationCondition> conditions) {
        this.conditions = conditions;
    }

    public StreamDefinition getStreamDefinition() {
        return streamDefinition;
    }

    public void setStreamDefinition(StreamDefinition streamDefinition) {
        this.streamDefinition = streamDefinition;
    }

    public String getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(String outputStream) {
        this.outputStream = outputStream;
    }

    public Map<String, String> getOutputAttributes() {
        return outputAttributes;
    }

    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public String getOutputMapping() {
        return outputMapping;
    }

    public void setOutputMapping(String outputMapping) {
        this.outputMapping = outputMapping;
    }

    public void setOutputAttributes(Map<String, String> outputAttributes) {
        this.outputAttributes = outputAttributes;
    }

    public String getInputStreamId() {
        return inputStreamId;
    }

    public void setInputStreamId(String inputStreamId) {
        this.inputStreamId = inputStreamId;
    }

    public List<DerivedAttribute> getDerivedAttributes() {
        return derivedAttributes;
    }

    public void setDerivedAttributes(List<DerivedAttribute> derivedAttributes) {
        this.derivedAttributes = derivedAttributes;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getOutputAdaptorType() {
        return outputAdaptorType;
    }

    public void setOutputAdaptorType(String outputAdaptorType) {
        this.outputAdaptorType = outputAdaptorType;
    }

}
