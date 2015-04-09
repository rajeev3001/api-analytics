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

package org.wso2.carbon.api.analytics.alerts.core.internal.clients;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.event.processor.stub.EventProcessorAdminServiceStub;
import org.wso2.carbon.event.processor.stub.types.ExecutionPlanConfigurationDto;
import org.wso2.carbon.event.processor.stub.types.ExecutionPlanConfigurationFileDto;
import org.wso2.carbon.event.processor.stub.types.StreamDefinitionDto;

import java.rmi.RemoteException;

public class EventProcessorAdminServiceClient {
    private static final Log log = LogFactory.getLog(EventProcessorAdminServiceClient.class);
    private final String serviceName = "EventProcessorAdminService";
    private EventProcessorAdminServiceStub eventProcessorAdminServiceStub;
    private String endPoint;


    public EventProcessorAdminServiceClient(String backEndUrl, String username, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);
        eventProcessorAdminServiceStub = new EventProcessorAdminServiceStub(ctx, endPoint);
        AuthenticationHelper.setBasicAuthHeaders(username, password, eventProcessorAdminServiceStub);

    }

    public ExecutionPlanConfigurationFileDto[] getExecutionPlanConfigurations()
            throws RemoteException {
        try {
            return eventProcessorAdminServiceStub.getAllInactiveExecutionPlanConigurations();
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public boolean containsExecutionPlan(String execPlanName) throws RemoteException {
        ExecutionPlanConfigurationDto[] dtos = eventProcessorAdminServiceStub.getAllActiveExecutionPlanConfigurations();
        for (ExecutionPlanConfigurationDto dto : dtos) {
            if (execPlanName.equals(dto.getName())) {
                return true;
            }
        }
        return false;
    }

    public void addExecutionPlan(ExecutionPlanConfigurationDto executionPlanConfigurationDto)
            throws RemoteException {
        try {
            eventProcessorAdminServiceStub.deployExecutionPlanConfiguration(executionPlanConfigurationDto);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeActiveExecutionPlan(String planName)
            throws RemoteException {
        try {
            eventProcessorAdminServiceStub.undeployActiveExecutionPlanConfiguration(planName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeInactiveExecutionPlan(String filePath)
            throws RemoteException {
        try {
            eventProcessorAdminServiceStub.undeployInactiveExecutionPlanConfiguration(filePath);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeExecutionPlan(String planName) throws RemoteException {
        try {
            removeActiveExecutionPlan(planName);
        } catch (RemoteException e) {
            log.error(e.getMessage());
            removeInactiveExecutionPlan(planName + ".xml");
        }
    }

    public ExecutionPlanConfigurationDto getExecutionPlan(String executionPlanName)
            throws RemoteException {
        try {
            return eventProcessorAdminServiceStub.getActiveExecutionPlanConfiguration(executionPlanName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public StreamDefinitionDto[] getSiddhiStreams(String[] inputStreamDefinitions, String queryExpressions) throws RemoteException {
        try {
            return eventProcessorAdminServiceStub.getSiddhiStreams(inputStreamDefinitions, queryExpressions);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }
}
