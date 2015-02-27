package org.wso2.carbon.api.analytics.alerts.core;

import org.wso2.carbon.api.analytics.alerts.core.exception.AlertConfigurationException;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationClientFacade;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationHelper;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationValueHolder;
import org.wso2.carbon.event.builder.stub.types.EventBuilderConfigurationDto;
import org.wso2.carbon.event.formatter.stub.types.EventFormatterConfigurationDto;
import org.wso2.carbon.event.processor.stub.types.ExecutionPlanConfigurationDto;

import java.rmi.RemoteException;
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

        EventBuilderConfigurationDto builderDto = AlertConfigurationHelper.getEventBuilderDto(alertConfiguration);
        ExecutionPlanConfigurationDto execPlanDto = AlertConfigurationHelper.getExecutionPlanDto(alertConfiguration);
        EventFormatterConfigurationDto formatterDto = AlertConfigurationHelper.getEventFormatterDto(alertConfiguration);

        try {
            AlertConfigurationClientFacade.getInstance().getEventBuilderAdminServiceClient().deployEventBuilderConfiguration(builderDto);
            AlertConfigurationClientFacade.getInstance().getEventProcessorAdminServiceClient().addExecutionPlan(execPlanDto);
            AlertConfigurationClientFacade.getInstance().getEventFormatterAdminServiceClient().addEventFormatterConfiguration(formatterDto);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // todo where/how to save?
    }

    public void undeployAlertConfiguration(String configurationId, int tenantId) throws AlertConfigurationException {

        // todo remove logic.

    }


    public List<AlertConfiguration> getAllAlertConfigurations(int tenantId) {
        return this.tenantSpecificConfigurations.get(tenantId);
    }




}
