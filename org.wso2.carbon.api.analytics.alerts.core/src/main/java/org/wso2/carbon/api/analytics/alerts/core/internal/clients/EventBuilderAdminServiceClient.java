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
import org.wso2.carbon.event.builder.stub.EventBuilderAdminServiceStub;
import org.wso2.carbon.event.builder.stub.types.*;
import org.wso2.carbon.event.builder.stub.types.PropertyDto;
import org.wso2.carbon.event.formatter.stub.types.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class EventBuilderAdminServiceClient {

    private static final Log log = LogFactory.getLog(EventBuilderAdminServiceClient.class);
    private final String serviceName = "EventBuilderAdminService";
    private EventBuilderAdminServiceStub eventBuilderAdminServiceStub;
    private String endPoint;


    public EventBuilderAdminServiceClient(String backEndUrl, String username, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

        eventBuilderAdminServiceStub = new EventBuilderAdminServiceStub(ctx, endPoint);
        AuthenticationHelper.setBasicAuthHeaders(username, password, eventBuilderAdminServiceStub);

    }

    public boolean containsBuilderConfiguration(String builderName) throws RemoteException {
        EventBuilderConfigurationInfoDto[] dtos = eventBuilderAdminServiceStub.getAllActiveEventBuilderConfigurations();
        for (EventBuilderConfigurationInfoDto dto : dtos) {
            if (builderName.equals(dto.getEventBuilderName())) {
                return true;
            }
        }
        return false;
    }


    public void deployEventBuilderConfiguration(EventBuilderConfigurationDto dto) throws RemoteException {

        try {
            org.wso2.carbon.event.builder.stub.types.PropertyDto[] propDtos = new org.wso2.carbon.event.builder.stub.types.PropertyDto[dto.getEventBuilderMessageProperties().length];
            int i = 0;
            for (EventBuilderMessagePropertyDto msgDto : dto.getEventBuilderMessageProperties()) {
                propDtos[i] = new org.wso2.carbon.event.builder.stub.types.PropertyDto();
                propDtos[i].setKey(msgDto.getKey());
                propDtos[i].setValue(msgDto.getValue());
                i++;
            }
            eventBuilderAdminServiceStub.deployWso2EventBuilderConfiguration(dto.getEventBuilderConfigName(), dto.getToStreamName() + ":" + dto.getToStreamVersion(),
                    dto.getInputEventAdaptorName(), dto.getInputEventAdaptorType(), null, null, null, propDtos, dto.getCustomMappingEnabled());
        } catch (Exception e) {
            log.error("exc in builder config", e);
        }
    }


    public EventBuilderConfigurationInfoDto[] getActiveEventBuilders()
            throws RemoteException {
        try {
            return eventBuilderAdminServiceStub.getAllActiveEventBuilderConfigurations();
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }

    }

    public void removeActiveEventBuilderConfiguration(String eventBuilderName)
            throws RemoteException {
        try {
            eventBuilderAdminServiceStub.undeployActiveEventBuilderConfiguration(eventBuilderName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeInactiveEventBuilderConfiguration(String filePath)
            throws RemoteException {
        try {
            eventBuilderAdminServiceStub.undeployInactiveEventBuilderConfiguration(filePath);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeEventBuilderConfiguration(String builderName) throws RemoteException {
        try {
            removeActiveEventBuilderConfiguration(builderName);
        } catch (RemoteException e) {
            log.error(e.getMessage());
            removeInactiveEventBuilderConfiguration(builderName + ".xml");
        }
    }


    public EventBuilderConfigurationDto getEventBuilderConfiguration(String eventBuilderConfiguration)
            throws RemoteException {
        try {
            return eventBuilderAdminServiceStub.getActiveEventBuilderConfiguration(eventBuilderConfiguration);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }
}
