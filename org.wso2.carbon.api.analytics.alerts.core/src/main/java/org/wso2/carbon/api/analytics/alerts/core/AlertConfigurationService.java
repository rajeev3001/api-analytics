package org.wso2.carbon.api.analytics.alerts.core;

import org.wso2.carbon.api.analytics.alerts.core.exception.AlertConfigurationException;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class AlertConfigurationService {


    private ConcurrentHashMap<Integer, List<AlertConfiguration>> tenantSpecificConfigurations;


    public AlertConfigurationService() {
        this.tenantSpecificConfigurations = new ConcurrentHashMap<Integer, List<AlertConfiguration>>();
    }

    public void deployAlertConfiguration(AlertConfiguration alertConfiguration, int tenantId) throws AlertConfigurationException {

        List<AlertConfiguration> configurations = tenantSpecificConfigurations.get(tenantId);
        if (configurations == null) {
            configurations = new Vector<AlertConfiguration>();
            tenantSpecificConfigurations.put(tenantId, configurations);
        }



        // todo where/how to save?

    }

    public void undeployAlertConfiguration(String configurationId, int tenantId) throws AlertConfigurationException {

        // todo remove logic.

    }


    public List<AlertConfiguration> getAllAlertConfigurations(int tenantId) {
        return null;
    }




}
