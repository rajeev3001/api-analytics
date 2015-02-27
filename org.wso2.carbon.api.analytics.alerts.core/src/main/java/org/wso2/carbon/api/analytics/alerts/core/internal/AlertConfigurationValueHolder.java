package org.wso2.carbon.api.analytics.alerts.core.internal;

import java.util.Map;

public class AlertConfigurationValueHolder {

private  String inputEventAdaptorName;
private  String inputEventAdaptorType;

private  String outputEventAdaptorName;
private  String outputEventAdaptorType;
private  Map<String, String> outputAdaptorProperties;

    private static AlertConfigurationValueHolder instance;

    public static AlertConfigurationValueHolder getInstance() {
        if (instance == null) {
            instance = new AlertConfigurationValueHolder();
        }
        return instance;
    }

    private AlertConfigurationValueHolder() {
        // todo read configs from file.
    }


    public  Map<String, String> getOutputAdaptorProperties() {
        return outputAdaptorProperties;
    }

    public  String getInputEventAdaptorName() {
        return inputEventAdaptorName;
    }


    public  String getInputEventAdaptorType() {
        return inputEventAdaptorType;
    }

    public  String getOutputEventAdaptorName() {
        return outputEventAdaptorName;
    }

    public  String getOutputEventAdaptorType() {
        return outputEventAdaptorType;
    }

}
