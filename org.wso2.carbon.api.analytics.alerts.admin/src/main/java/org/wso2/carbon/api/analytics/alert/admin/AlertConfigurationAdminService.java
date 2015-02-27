package org.wso2.carbon.api.analytics.alert.admin;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.api.analytics.alert.admin.internal.ds.AlertConfigurationAdminValueHolder;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfiguration;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationCondition;
import org.wso2.carbon.api.analytics.alerts.core.exception.AlertConfigurationException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.databridge.commons.Attribute;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;

import java.util.ArrayList;
import java.util.List;

public class AlertConfigurationAdminService extends AbstractAdmin {


    public void addAlertConfiguration(AlertConfigurationDto configDto) throws AxisFault {
        AlertConfiguration alertConfig = new AlertConfiguration();
        List<AlertConfigurationCondition> conditions = new ArrayList<AlertConfigurationCondition>();
        alertConfig.setConfigurationId(configDto.getConfigurationId());

        if (configDto.getConditions() != null) {
            for (AlertConfigurationConditionDto conditionDto : configDto.getConditions()) {
                AlertConfigurationCondition condition = new AlertConfigurationCondition();
                condition.setAttribute(conditionDto.getAttributeName());
                condition.setTargetValue(conditionDto.getAttributeValue());
                if (conditionDto.getOperation().equals(AlertConfigurationCondition.Operation.LESS_THAN.symbol())) {
                    condition.setOperation(AlertConfigurationCondition.Operation.LESS_THAN);
                } else if (conditionDto.getOperation().equals(AlertConfigurationCondition.Operation.GREATER_THAN.symbol())) {
                    condition.setOperation(AlertConfigurationCondition.Operation.GREATER_THAN);
                } else if (conditionDto.getOperation().equals(AlertConfigurationCondition.Operation.EQUALS.symbol())) {
                    condition.setOperation(AlertConfigurationCondition.Operation.EQUALS);
                }
                conditions.add(condition);
            }
        } else {
            // todo throw exception?
        }
        alertConfig.setConditions(conditions);
alertConfig.setOutputMapping(configDto.getOutputMapping());
        StreamDefinition streamDefinition = new StreamDefinition(configDto.getInputStreamId());
        String[] attributes = configDto.getStreamDefinition().split(",");

        for (String attribute : attributes) {
            String[] nameType = attribute.trim().split(" ");
            if (nameType[1].equals("int")) {
                streamDefinition.addPayloadData(nameType[0], AttributeType.INT);
            } else if (nameType[1].equals("float")) {
                streamDefinition.addPayloadData(nameType[0], AttributeType.FLOAT);
            } else if (nameType[1].equals("long")) {
                streamDefinition.addPayloadData(nameType[0], AttributeType.LONG);
            } else if (nameType[1].equals("double")) {
                streamDefinition.addPayloadData(nameType[0], AttributeType.DOUBLE);
            } else if (nameType[1].equals("boolean")) {
                streamDefinition.addPayloadData(nameType[0], AttributeType.BOOL);
            } else if (nameType[1].equals("string")) {
                streamDefinition.addPayloadData(nameType[0], AttributeType.STRING);
            }
        }

        alertConfig.setStreamDefinition(streamDefinition);
        try {
            AlertConfigurationAdminValueHolder.getAlertConfigurationService().deployAlertConfiguration(alertConfig, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
        } catch (AlertConfigurationException e) {
            // todo log
            throw new AxisFault("Error while deploying the alert configuration.", e);
        }

    }

    public void removeAlertConfiguration(String configurationId) throws AxisFault {
        try {
            AlertConfigurationAdminValueHolder.getAlertConfigurationService().undeployAlertConfiguration(configurationId, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
        } catch (AlertConfigurationException e) {
            // todo log
            throw new AxisFault("Error while undeploying the alert configuration.", e);
        }
    }

    public AlertConfigurationDto[] getAllAlertConfigurations() throws AxisFault {
        List<AlertConfiguration> allConfigs = AlertConfigurationAdminValueHolder.getAlertConfigurationService().getAllAlertConfigurations(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
        AlertConfigurationDto[] allDtos = new AlertConfigurationDto[allConfigs.size()];
        if (allConfigs.size() > 0) {

            for (int i = 0; i < allConfigs.size(); i++) {
                AlertConfiguration config = allConfigs.get(i);

                allDtos[i] = new AlertConfigurationDto();

                allDtos[i].setConfigurationId(config.getConfigurationId());
                allDtos[i].setInputStreamId(config.getStreamDefinition().getStreamId());

                StringBuilder streamDef = new StringBuilder("");
                boolean appendComma = false;
                for (Attribute at : config.getStreamDefinition().getPayloadData()) {
                    if (appendComma) {
                        streamDef.append(",");
                    }
                    streamDef.append(at.getName());
                    streamDef.append(" ");
                    streamDef.append(at.getType().toString().toLowerCase());
                    appendComma = true;
                }

                allDtos[i].setStreamDefinition(streamDef.toString());
                AlertConfigurationConditionDto[] conditionDtos = new AlertConfigurationConditionDto[config.getConditions().size()];

                int j = 0;
                for (AlertConfigurationCondition condition : config.getConditions()) {
                    AlertConfigurationConditionDto conditionDto = new AlertConfigurationConditionDto();
                    conditionDto.setAttributeName(condition.getAttribute());
                    conditionDto.setAttributeValue(condition.getTargetValue().toString());
                    conditionDto.setOperation(condition.getOperation().symbol());
                    conditionDtos[j] = conditionDto;
                    j++;
                }
                allDtos[i].setConditions(conditionDtos);
                allDtos[i].setOutputMapping(config.getOutputMapping());
                i++;
            }
        }
        return allDtos;
    }

}
