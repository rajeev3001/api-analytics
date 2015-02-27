package org.wso2.carbon.api.analytics.alerts.admin.internal.ds;

import org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationService;

public class AlertConfigurationAdminValueHolder {

    private static AlertConfigurationService alertConfigService;

    public static void registerAlertConfigurationService(AlertConfigurationService alertConfigService) {
        AlertConfigurationAdminValueHolder.alertConfigService = alertConfigService;
    }

    public static AlertConfigurationService getAlertConfigurationService() {
        return alertConfigService;
    }
}
