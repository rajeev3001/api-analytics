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

import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.junit.Test;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfiguration;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationCondition;
import org.wso2.carbon.api.analytics.alerts.core.DerivedAttribute;
import org.wso2.carbon.api.analytics.alerts.core.internal.AlertConfigurationHelper;
import org.wso2.carbon.api.analytics.alerts.core.internal.clients.EventBuilderAdminServiceClient;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.event.builder.stub.EventBuilderAdminServiceStub;
import org.wso2.carbon.event.builder.stub.types.EventBuilderConfigurationDto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TestHelperMethods {


    @Test
    public void testStreamDef() throws MalformedStreamDefinitionException {
        StreamDefinition streamDefinition = new StreamDefinition("org.wso2.test.stream", "1.0.0");
        AlertConfiguration config = new AlertConfiguration();
        config.setStreamDefinition(streamDefinition);
        System.out.println(AlertConfigurationHelper.getOutputStreamName(config));
    }


    @Test
    public void testConfigs() throws RemoteException, MalformedStreamDefinitionException {
//        EventBuilderAdminServiceClient client = new EventBuilderAdminServiceClient("https://10.100.0.121:9445/services/EventBuilderAdminService");
//        client.setBasicAuthHeaders("admin", "admin");

        String trustStore = "/Users/rajeevs/api-analytics/wso2am-1.8.0/repository/resources/security/" + "wso2carbon.jks";
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");


        AlertConfiguration config = new AlertConfiguration();
        config.setConfigurationId("config11");
        config.setOutputStream("testOut");
        StreamDefinition sd = new StreamDefinition("response", "1.0.0");
        sd.addPayloadData("name", AttributeType.STRING);
        sd.addPayloadData("age", AttributeType.INT);
        config.setStreamDefinition(sd);


        AlertConfigurationCondition condition = new AlertConfigurationCondition();
        condition.setAttribute("age");
        condition.setTargetValue(11);
        condition.setOperation(AlertConfigurationCondition.Operation.GREATER_THAN);

        List<AlertConfigurationCondition> conditions = new ArrayList<AlertConfigurationCondition>();
        conditions.add(condition);

        config.setConditions(conditions);

        DerivedAttribute da = new DerivedAttribute();
        da.setSelectExpressions("age");
        da.setAggregationLength(11);
        da.setAggregationType("length");
        da.setGroupByAttributes("name");

        List<DerivedAttribute> daList = new ArrayList<DerivedAttribute>(1);
        daList.add(da);

//        config.setDerivedAttributes(daList);

String query = AlertConfigurationHelper.generateSiddhiQueries(config);
//        Object x =  stub.getAllActiveEventBuilderConfigurations();

//        System.out.println(x);

        System.out.println(query);
        System.out.println("done");
    }

}
