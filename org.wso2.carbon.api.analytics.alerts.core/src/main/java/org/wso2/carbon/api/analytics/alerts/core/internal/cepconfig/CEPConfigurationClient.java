package org.wso2.carbon.api.analytics.alerts.core.internal.cepconfig;

import org.wso2.carbon.api.analytics.alerts.core.AlertConfiguration;
import org.wso2.carbon.api.analytics.alerts.core.exception.AlertConfigurationException;

public interface CEPConfigurationClient {

    public void addAlertConfiguration(AlertConfiguration alertConfiguration, int tenantId) throws AlertConfigurationException;

    public void removeAlertConfiguration(String configurationId, int tenantId) throws AlertConfigurationException;
}
