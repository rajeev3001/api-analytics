package org.wso2.carbon.api.analytics.alert.admin.internal.ds;


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
