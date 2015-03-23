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

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.api.analytics.alerts.admin.internal.ds.AlertConfigurationAdminValueHolder;
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

    private static final Log log = LogFactory.getLog(AlertConfigurationAdminService.class);


    public void addAlertConfiguration(AlertConfigurationDto configDto) throws AxisFault {
        AlertConfiguration alertConfig = new AlertConfiguration();
        List<AlertConfigurationCondition> conditions = new ArrayList<AlertConfigurationCondition>();
        alertConfig.setConfigurationId(configDto.getConfigurationId());

        if (configDto.getConditions() != null) {
            for (AlertConfigurationConditionDto conditionDto : configDto.getConditions()) {
                AlertConfigurationCondition condition = new AlertConfigurationCondition();
                condition.setAttribute(conditionDto.getAttributeName());
                condition.setTargetValue(conditionDto.getAttributeValue());
                if (conditionDto.getOperation().equalsIgnoreCase(AlertConfigurationCondition.Operation.LESS_THAN.toString())) {
                    condition.setOperation(AlertConfigurationCondition.Operation.LESS_THAN);
                } else if (conditionDto.getOperation().equalsIgnoreCase(AlertConfigurationCondition.Operation.GREATER_THAN.toString())) {
                    condition.setOperation(AlertConfigurationCondition.Operation.GREATER_THAN);
                } else if (conditionDto.getOperation().equalsIgnoreCase(AlertConfigurationCondition.Operation.EQUALS.toString())) {
                    condition.setOperation(AlertConfigurationCondition.Operation.EQUALS);
                }
                conditions.add(condition);
            }
        } else {
            throw new AxisFault("Configurations with no conditions are not allowed.");
        }
        alertConfig.setConditions(conditions);
        alertConfig.setOutputMapping(configDto.getOutputMapping());

        alertConfig.setInputStreamId(configDto.getInputStreamId());

        if (configDto.getAttributeDefinitions() != null && configDto.getAttributeDefinitions().length() > 0) {
            StreamDefinition streamDefinition = new StreamDefinition(configDto.getInputStreamId());
            String[] attributes = configDto.getAttributeDefinitions().split(",");

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
        }
        try {
            AlertConfigurationAdminValueHolder.getAlertConfigurationService().addAlertConfiguration(alertConfig, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
        } catch (AlertConfigurationException e) {
            log.error("Error adding alert configuration: " + alertConfig.getConfigurationId(), e);
            throw new AxisFault("Error while deploying the alert configuration.", e);
        }

    }

    public void removeAlertConfiguration(String configurationId) throws AxisFault {
        try {
            AlertConfigurationAdminValueHolder.getAlertConfigurationService().removeAlertConfiguration(configurationId, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
        } catch (AlertConfigurationException e) {
            log.error("Error removing alert configuration: " + configurationId, e);
            throw new AxisFault("Error while undeploying the alert configuration.", e);
        }
    }

    public AlertConfigurationDto[] getAllAlertConfigurations() throws AxisFault {
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        List<AlertConfiguration> allConfigs = AlertConfigurationAdminValueHolder.getAlertConfigurationService().getAllAlertConfigurations(tenantId);
        AlertConfigurationDto[] allDtos = new AlertConfigurationDto[allConfigs.size()];
        if (allConfigs.size() > 0) {

            for (int i = 0; i < allConfigs.size(); i++) {
                AlertConfiguration config = allConfigs.get(i);
                allDtos[i] = convertToDto(config);
            }
        }
        return allDtos;
    }

    public AlertConfigurationDto getAlertConfiguration(String configurationId) throws AxisFault {
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            AlertConfiguration config = AlertConfigurationAdminValueHolder.getAlertConfigurationService().getAlertConfiguration(configurationId, tenantId);
            return convertToDto(config);
        } catch (AlertConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    private AlertConfigurationDto convertToDto(AlertConfiguration config) {

        AlertConfigurationDto configDto = new AlertConfigurationDto();

        configDto.setConfigurationId(config.getConfigurationId());
        configDto.setInputStreamId(config.getStreamDefinition().getStreamId());

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

        configDto.setAttributeDefinitions(streamDef.toString());
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
        configDto.setConditions(conditionDtos);
        configDto.setOutputMapping(config.getOutputMapping());

        return configDto;
    }


}
