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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.event.formatter.stub.EventFormatterAdminServiceStub;
import org.wso2.carbon.event.formatter.stub.types.*;

import java.rmi.RemoteException;

public class EventFormatterAdminServiceClient {
    private static final Log log = LogFactory.getLog(EventFormatterAdminServiceClient.class);
    private final String serviceName = "EventFormatterAdminService";
    private EventFormatterAdminServiceStub eventFormatterAdminServiceStub;
    private String endPoint;

    public EventFormatterAdminServiceClient(String backEndUrl, String sessionCookie) throws
            AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventFormatterAdminServiceStub = new EventFormatterAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, eventFormatterAdminServiceStub);

    }

    public EventFormatterAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventFormatterAdminServiceStub = new EventFormatterAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, eventFormatterAdminServiceStub);
    }

    public ServiceClient _getServiceClient() {
        return eventFormatterAdminServiceStub._getServiceClient();
    }

    public int getActiveEventFormatterCount()
            throws RemoteException {
        try {
            EventFormatterConfigurationInfoDto[] configs = eventFormatterAdminServiceStub.getAllActiveEventFormatterConfiguration();
            if (configs == null) {
                return 0;
            } else {
                return configs.length;
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }

    public int getEventFormatterCount()
            throws RemoteException {
        try {
            EventFormatterConfigurationFileDto[] configs = eventFormatterAdminServiceStub.getAllInactiveEventFormatterConfiguration();
            if (configs == null) {
                return getActiveEventFormatterCount();
            } else {
                return configs.length + getActiveEventFormatterCount();
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }


    public void addWso2EventFormatterConfiguration(String eventFormatterName, String streamId, String transportAdaptorName, String transportAdaptorType,
                                                   EventOutputPropertyConfigurationDto[] metaData, EventOutputPropertyConfigurationDto[] correlationData,
                                                   EventOutputPropertyConfigurationDto[] payloadData, PropertyDto[] eventFormatterPropertyDtos, boolean mappingEnabled)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.deployWSO2EventFormatterConfiguration(eventFormatterName, streamId, transportAdaptorName, transportAdaptorType, metaData, correlationData, payloadData, eventFormatterPropertyDtos, mappingEnabled);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void addXMLEventFormatterConfiguration(String eventFormatterName,
                                                  String streamNameWithVersion,
                                                  String transportAdaptorName,
                                                  String transportAdaptorType,
                                                  String textData,
                                                  PropertyDto[] outputPropertyConfiguration,
                                                  String dataFrom, boolean mappingEnabled)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.deployXmlEventFormatterConfiguration(eventFormatterName, streamNameWithVersion, transportAdaptorName, transportAdaptorType, textData, outputPropertyConfiguration, dataFrom, mappingEnabled);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void addEventFormatterConfiguration(String eventFormatterConfigXml) throws RemoteException {
        try {
            eventFormatterAdminServiceStub.deployEventFormatterConfiguration(eventFormatterConfigXml);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeActiveEventFormatterConfiguration(String eventFormatterName)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.undeployActiveEventFormatterConfiguration(eventFormatterName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeInactiveEventFormatterConfiguration(String filePath)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.undeployInactiveEventFormatterConfiguration(filePath);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public EventFormatterConfigurationDto getEventFormatterConfiguration(String eventFormatterName)
            throws RemoteException {
        try {
            return eventFormatterAdminServiceStub.getActiveEventFormatterConfiguration(eventFormatterName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void addEventFormatterConfiguration(EventFormatterConfigurationDto dto) throws RemoteException {

        /*
        java.lang.String eventFormatterName257, java.lang.String streamNameWithVersion258,
         java.lang.String eventAdaptorName259, java.lang.String eventAdaptorType260,
          java.lang.String textData261,
        org.wso2.carbon.event.formatter.stub.types.PropertyDto[] outputPropertyConfiguration262,
         java.lang.String dataFrom263, boolean mappingEnabled264
         */

        // dataFrom == inline

        EventFormatterPropertyDto[] propDtos = dto.getToPropertyConfigurationDto().getOutputEventAdaptorMessageConfiguration();
        PropertyDto[] stubDtos = new PropertyDto[propDtos.length];
        for (int i = 0; i < propDtos.length; i++) {
            stubDtos[i] = new PropertyDto();
            stubDtos[i].setKey(propDtos[i].getKey());
            stubDtos[i].setValue(propDtos[i].getValue());
        }

        eventFormatterAdminServiceStub.deployTextEventFormatterConfiguration(dto.getEventFormatterName(), dto.getFromStreamNameWithVersion(),
                dto.getToPropertyConfigurationDto().getEventAdaptorName(), dto.getToPropertyConfigurationDto().getEventAdaptorType(),
                dto.getTextOutputMappingDto().getMappingText(), stubDtos, "true", true);
    }
}
