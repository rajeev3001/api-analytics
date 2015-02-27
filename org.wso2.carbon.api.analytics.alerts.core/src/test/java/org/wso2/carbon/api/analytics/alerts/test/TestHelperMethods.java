package org.wso2.carbon.api.analytics.alerts.test;

import org.junit.Test;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfiguration;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationHelper;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;

public class TestHelperMethods {


    @Test
    public void testStreamDef() throws MalformedStreamDefinitionException {
        StreamDefinition streamDefinition = new StreamDefinition("org.wso2.test.stream", "1.0.0");
        AlertConfiguration config = new AlertConfiguration();
        config.setStreamDefinition(streamDefinition);
        System.out.println(AlertConfigurationHelper.getOutputStreamName(config));
    }

}
