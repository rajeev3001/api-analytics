package org.wso2.carbon.api.analytics.alerts.core;

import org.wso2.carbon.databridge.commons.StreamDefinition;

import java.util.List;
import java.util.Map;

public class AlertConfiguration {


    /*
    config ui:

    param: <dropdown>
    is: <operator>
    value: <value>
    message: <text>
    endpoint: <uri>

     */


    // this can be the chart id
    private String configurationId;

    private List<AlertConfigurationCondition> conditions;
    private StreamDefinition streamDefinition;

    // todo infer these from input and conditions
    private Map<String, String> outputAttributes;
    private String outputStream;

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

    public void setOutputAttributes(Map<String, String> outputAttributes) {
        this.outputAttributes = outputAttributes;
    }

    // adaptor type, endpoint, credentials
    // conditions
    // msg format
    // stream attributes.



}
