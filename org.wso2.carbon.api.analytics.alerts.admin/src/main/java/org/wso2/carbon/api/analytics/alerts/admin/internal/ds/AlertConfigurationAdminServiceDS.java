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

package org.wso2.carbon.api.analytics.alerts.admin.internal.ds;


import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationService;

/**
 * @scr.component name="alertConfigurationAdmin.component" immediate="true"
 * @scr.reference name="alertConfigurationAdmin.service"
 * interface="org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationService" cardinality="1..1"
 * policy="dynamic" bind="setAlertConfigurationService" unbind="unsetEventProcessorService"
 */
public class AlertConfigurationAdminServiceDS {

    protected void activate(ComponentContext context) {

    }

    public void setAlertConfigurationService(AlertConfigurationService alertConfigService) {
        AlertConfigurationAdminValueHolder.registerAlertConfigurationService(alertConfigService);
    }

    public void unsetEventProcessorService(AlertConfigurationService alertConfigService) {
        AlertConfigurationAdminValueHolder.registerAlertConfigurationService(null);

    }


}
