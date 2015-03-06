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
