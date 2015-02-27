package org.wso2.carbon.api.analytics.alert.admin;

import org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationCondition;

public class AlertConfigurationDto {

    private AlertConfigurationConditionDto[] conditions;

    private String streamDefinition;
    private String configurationId;
    private String inputStreamId;

    public String getConfigurationId() {
        return configurationId;
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

    public String getStreamDefinition() {
        return streamDefinition;
    }

    public void setStreamDefinition(String streamDefinition) {
        this.streamDefinition = streamDefinition;
    }

    public String getInputStreamId() {
        return inputStreamId;
    }

    public void setInputStreamId(String inputStreamId) {
        this.inputStreamId = inputStreamId;
    }
}
