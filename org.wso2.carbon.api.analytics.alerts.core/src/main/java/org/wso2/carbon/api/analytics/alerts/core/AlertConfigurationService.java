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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.api.analytics.alerts.core.exception.AlertConfigurationException;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationClientFactory;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationHelper;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationStore;
import org.wso2.carbon.api.analytics.alerts.core.internal.StreamMgtHelper;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.event.builder.stub.types.EventBuilderConfigurationDto;
import org.wso2.carbon.event.formatter.stub.types.EventFormatterConfigurationDto;
import org.wso2.carbon.event.processor.stub.types.ExecutionPlanConfigurationDto;
import org.wso2.carbon.registry.api.RegistryException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlertConfigurationService {

    private static final Log log = LogFactory.getLog(AlertConfigurationService.class);

    public AlertConfigurationService() {
    }

    public boolean addAlertConfiguration(AlertConfiguration alertConfiguration, int tenantId) throws AlertConfigurationException {

        if (alertConfiguration.getStreamDefinition() == null) {
            StreamDefinition streamDefinition = StreamMgtHelper.getStreamDefinition(alertConfiguration.getInputStreamId());
            alertConfiguration.setStreamDefinition(streamDefinition);
        }

        // todo check if alert config already exists ....

        EventBuilderConfigurationDto builderDto = AlertConfigurationHelper.getEventBuilderDto(alertConfiguration);
        ExecutionPlanConfigurationDto execPlanDto = AlertConfigurationHelper.getExecutionPlanDto(alertConfiguration);
        EventFormatterConfigurationDto formatterDto = AlertConfigurationHelper.getEventFormatterDto(alertConfiguration);

        try {
            // event stream is added by the datapublisher, until it is added, these will be inactive.
            // todo add wso2 event inputs and email outputs - currently their existance is assumed..

            AlertConfigurationClientFactory.getInstance().getEventBuilderAdminServiceClient().deployEventBuilderConfiguration(builderDto);
            StreamMgtHelper.addRequiredStream(alertConfiguration.getStreamDefinition(), execPlanDto.getQueryExpressions());

            AlertConfigurationClientFactory.getInstance().getEventProcessorAdminServiceClient().addExecutionPlan(execPlanDto);
            AlertConfigurationClientFactory.getInstance().getEventFormatterAdminServiceClient().addEventFormatterConfiguration(formatterDto);

            AlertConfigurationStore.getInstance().saveAlertConfiguration(alertConfiguration, tenantId);

        } catch (RegistryException e) {
            log.error("Error while saving alert configuration", e);
            return false;
        } catch (RemoteException e) {
            log.error("Error while configuring cep.", e);
            return false;
        } catch (Exception e) {
            log.error("Error while configuring alerts.", e);
            return false;
        }
        return true;
    }

    public void removeAlertConfiguration(String configurationId, int tenantId) throws AlertConfigurationException {
        // todo do this for inactive ones too??

        try {
            AlertConfigurationClientFactory.getInstance().getEventFormatterAdminServiceClient().removeEventFormatterConfiguration(configurationId);

            AlertConfigurationClientFactory.getInstance().getEventProcessorAdminServiceClient().removeExecutionPlan(configurationId);
            AlertConfigurationClientFactory.getInstance().getEventBuilderAdminServiceClient().removeEventBuilderConfiguration(configurationId);

            // cleaning up output stream.
            AlertConfiguration config = AlertConfigurationStore.getInstance().getAlertConfiguration(configurationId, tenantId);
            String outputStream = AlertConfigurationHelper.getOutputStreamName(config);
            AlertConfigurationClientFactory.getInstance().getEventStreamManagerAdminServiceClient().removeEventStream(outputStream, config.getStreamDefinition().getVersion());

            AlertConfigurationStore.getInstance().deleteAlertConfiguration(configurationId, tenantId);
        } catch (RegistryException e) {
            log.error("Error while removing the configurations.", e);
        } catch (RemoteException e) {
            log.error("Error while removing the configurations.", e);
        }
    }


    public List<AlertConfiguration> getAllAlertConfigurations(int tenantId) {
        List<AlertConfiguration> clonedList;

        try {
            List<AlertConfiguration> configList = AlertConfigurationStore.getInstance().getAllAlertConfigurations(tenantId);
            if (configList != null && configList.size() > 0) {
                clonedList = new ArrayList<AlertConfiguration>(configList);
            } else {
                clonedList = new ArrayList<AlertConfiguration>(1);
            }

        } catch (Exception e) {
            log.error("Error while fetching alert configurations", e);
            clonedList = new ArrayList<AlertConfiguration>(1);
        }
        return clonedList;
    }


    public AlertConfiguration getAlertConfiguration(String configurationId, int tenantId) throws AlertConfigurationException {
        try {
            return AlertConfigurationStore.getInstance().getAlertConfiguration(configurationId, tenantId);
        } catch (Exception e) {
            throw new AlertConfigurationException("No such configuration exists: " + configurationId);
        }
    }
}
