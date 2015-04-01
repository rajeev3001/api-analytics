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
import org.wso2.carbon.api.analytics.alerts.core.internal.*;
import org.wso2.carbon.api.analytics.alerts.core.internal.cepconfig.AdaptorBasedConfigurationClient;
import org.wso2.carbon.api.analytics.alerts.core.internal.cepconfig.CEPConfigurationClient;
import org.wso2.carbon.databridge.commons.StreamDefinition;

import java.util.ArrayList;
import java.util.List;

public class AlertConfigurationService {

    private static final Log log = LogFactory.getLog(AlertConfigurationService.class);

    private CEPConfigurationClient cepClient;

    public AlertConfigurationService() {
        // todo move this out & create this from config.
        cepClient = new AdaptorBasedConfigurationClient();
    }

    public void addAlertConfiguration(AlertConfiguration alertConfiguration, int tenantId) throws AlertConfigurationException {

        AlertConfigurationHelper.validateAlertConfiguration(alertConfiguration);
        if (alertConfiguration.getStreamDefinition() == null) {
            StreamDefinition streamDefinition = StreamMgtHelper.getStreamDefinition(alertConfiguration.getInputStreamId());
            alertConfiguration.setStreamDefinition(streamDefinition);
        }
        cepClient.addAlertConfiguration(alertConfiguration, tenantId);
    }

    public void removeAlertConfiguration(String configurationId, int tenantId) throws AlertConfigurationException {
        cepClient.removeAlertConfiguration(configurationId, tenantId);
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
