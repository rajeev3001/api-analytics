package org.wso2.carbon.api.analytics.alerts.core.internal;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.api.analytics.alerts.core.internal.clients.*;

public class AlertConfigurationClientFacade {

    private String backendUrl;
    private String username;
    private String password;

    private static AlertConfigurationClientFacade instance;

    private EventBuilderAdminServiceClient builderClient;
    private EventFormatterAdminServiceClient formatterClient;
    private EventProcessorAdminServiceClient processorClient;
    private InputEventAdaptorManagerAdminServiceClient inputAdaptorClient;
    private OutputEventAdaptorManagerAdminServiceClient outputAdaptorClient;
    private EventStreamManagerAdminServiceClient streamManagerClient;

    public static AlertConfigurationClientFacade getInstance() {
        if (instance == null) {
            instance = new AlertConfigurationClientFacade();
        }
        return instance;
    }

    public EventBuilderAdminServiceClient getEventBuilderAdminServiceClient() throws AxisFault {
        if (this.builderClient == null) {
            builderClient = new EventBuilderAdminServiceClient(backendUrl, username, password);
        }
        return builderClient;
    }

    public EventFormatterAdminServiceClient getEventFormatterAdminServiceClient() throws AxisFault {
        if (this.formatterClient == null) {
            formatterClient = new EventFormatterAdminServiceClient(backendUrl, username, password);
        }
        return formatterClient;
    }

    public EventProcessorAdminServiceClient getEventProcessorAdminServiceClient() throws AxisFault {
        if (this.processorClient == null) {
            processorClient = new EventProcessorAdminServiceClient(backendUrl, username, password);
        }
        return processorClient;
    }

    public InputEventAdaptorManagerAdminServiceClient getInputEventAdaptorAdminServiceClient() throws AxisFault {
        if (this.inputAdaptorClient == null) {
            inputAdaptorClient = new InputEventAdaptorManagerAdminServiceClient(backendUrl, username, password);
        }
        return inputAdaptorClient;
    }

    public OutputEventAdaptorManagerAdminServiceClient getOutputEventAdaptorAdminServiceClient() throws AxisFault {
        if (this.outputAdaptorClient == null) {
            outputAdaptorClient = new OutputEventAdaptorManagerAdminServiceClient(backendUrl, username, password);
        }
        return outputAdaptorClient;
    }

    public  EventStreamManagerAdminServiceClient getEventStreamManagerAdminServiceClient() throws AxisFault {
        if (this.streamManagerClient == null) {
            streamManagerClient = new EventStreamManagerAdminServiceClient(backendUrl, username, password);
        }
        return streamManagerClient;
    }

    private AlertConfigurationClientFacade() {

        // todo configure these params from apimgt.xml

        backendUrl = "https://10.100.0.121:9445/services/";
        username = "admin";
        password = "admin";
    }


}
