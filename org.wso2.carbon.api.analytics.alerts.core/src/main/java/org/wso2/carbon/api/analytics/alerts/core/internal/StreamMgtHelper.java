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

package org.wso2.carbon.api.analytics.alerts.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Attribute;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.event.processor.stub.types.StreamDefinitionDto;
import org.wso2.carbon.event.stream.manager.stub.types.EventStreamAttributeDto;
import org.wso2.carbon.event.stream.manager.stub.types.EventStreamDefinitionDto;

import java.rmi.RemoteException;

public class StreamMgtHelper {
    private static final Log log = LogFactory.getLog(StreamMgtHelper.class);


    public static boolean checkStreamExists(String streamName, String streamVersion) {
        String[] streamIds = getPredefinedStreamIds();
        for (String id : streamIds) {
            if (id.equals(streamName + ":" + streamVersion)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getPredefinedStreamIds() {
        try {
            return AlertConfigurationClientHolder.getInstance().getEventStreamManagerAdminServiceClient().getStreamNames();
        } catch (RemoteException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static StreamDefinition getStreamDefinition(String streamId) {
        try {
            EventStreamDefinitionDto dto = AlertConfigurationClientHolder.getInstance().getEventStreamManagerAdminServiceClient().getStreamDefinitionDto(streamId);
            StreamDefinition streamDefinition = new StreamDefinition(dto.getName(), dto.getVersion());

            if (dto.getMetaData() != null) {
                for (EventStreamAttributeDto attributeDto : dto.getMetaData()) {
                    Attribute at = convertToDatabridgeAttribute(attributeDto);
                    streamDefinition.addMetaData(at.getName(), at.getType());
                }
            }


            if (dto.getCorrelationData() != null) {
                for (EventStreamAttributeDto attributeDto : dto.getCorrelationData()) {
                    Attribute at = convertToDatabridgeAttribute(attributeDto);
                    streamDefinition.addCorrelationData(at.getName(), at.getType());
                }
            }


            if (dto.getPayloadData() != null) {
                for (EventStreamAttributeDto attributeDto : dto.getPayloadData()) {
                    Attribute at = convertToDatabridgeAttribute(attributeDto);
                    streamDefinition.addPayloadData(at.getName(), at.getType());
                }
            }
            return streamDefinition;
        } catch (RemoteException e) {
            log.error(e.getMessage(), e);
        } catch (MalformedStreamDefinitionException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static Attribute convertToDatabridgeAttribute(EventStreamAttributeDto dto) {
        return new Attribute(dto.getAttributeName(), AttributeType.valueOf(dto.getAttributeType().toUpperCase()));
    }

    public static StreamDefinitionDto[] getSiddhiStreams(StreamDefinition inStream, String queryExpressions) throws RemoteException {
        String inStreamDefinition = getSiddhiStreamDefinition(inStream);
        return AlertConfigurationClientHolder.getInstance().getEventProcessorAdminServiceClient().getSiddhiStreams(new String[]{inStreamDefinition}, queryExpressions);
    }

    public static void addRequiredStream(StreamDefinition streamDefinition, String queryExpressions) throws RemoteException {

        StreamDefinitionDto[] dtos = getSiddhiStreams(streamDefinition, queryExpressions);
        String[] streamIds = getPredefinedStreamIds();

        for (StreamDefinitionDto dto : dtos) {
            if ((!AlertConfigurationHelper.isSiddhiInputStream(dto.getName())) && (!isTemporaryStream(dto.getName()))) {
                // only the siddhi output streams are required to be added.
                // existence of input streams are assumed.
//            boolean exists =  checkStreamExists(dto.getName(), streamDefinition.getVersion());

                boolean exists = false;

                for (String id : streamIds) {
                    if (id.equals(dto.getName() + ":" + streamDefinition.getVersion())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {


                    EventStreamAttributeDto[] correlationAttributeDtos = null;
                    if (dto.getCorrelationData() != null) {
                        correlationAttributeDtos = new EventStreamAttributeDto[dto.getCorrelationData().length];
                        for (int i = 0; i < correlationAttributeDtos.length; i++) {
                            correlationAttributeDtos[i] = new EventStreamAttributeDto();
                            // attribute format: name <space> type
                            String[] attributeDef = dto.getCorrelationData()[i].split(" ");
                            correlationAttributeDtos[i].setAttributeName(attributeDef[0]);
                            correlationAttributeDtos[i].setAttributeType(convertToStreamManagerInputAttributeType(attributeDef[1]));

                        }
                    }


                    EventStreamAttributeDto[] metaAttributeDtos = null;
                    if (dto.getMetaData() != null) {
                        metaAttributeDtos = new EventStreamAttributeDto[dto.getMetaData().length];
                        for (int i = 0; i < metaAttributeDtos.length; i++) {
                            metaAttributeDtos[i] = new EventStreamAttributeDto();
                            // attribute format: name <space> type
                            String[] attributeDef = dto.getMetaData()[i].split(" ");
                            metaAttributeDtos[i].setAttributeName(attributeDef[0]);
                            metaAttributeDtos[i].setAttributeType(convertToStreamManagerInputAttributeType(attributeDef[1]));

                        }

                    }


                    EventStreamAttributeDto[] payloadAttributeDtos = null;
                    if (dto.getPayloadData() != null) {
                        payloadAttributeDtos = new EventStreamAttributeDto[dto.getPayloadData().length];
                        for (int i = 0; i < payloadAttributeDtos.length; i++) {
                            payloadAttributeDtos[i] = new EventStreamAttributeDto();
                            // attribute format: name <space> type
                            String[] attributeDef = dto.getPayloadData()[i].split(" ");
                            payloadAttributeDtos[i].setAttributeName(attributeDef[0]);
                            payloadAttributeDtos[i].setAttributeType(convertToStreamManagerInputAttributeType(attributeDef[1]));

                        }

                    }

                    AlertConfigurationClientHolder.getInstance().getEventStreamManagerAdminServiceClient().addEventStream(
                            dto.getName(),
                            streamDefinition.getVersion(),
                            metaAttributeDtos,
                            correlationAttributeDtos,
                            payloadAttributeDtos,
                            null,
                            null
                    );
                }
            }
        }

    }

    private static String convertToStreamManagerInputAttributeType(String databridgeAttributeType) {
        if (databridgeAttributeType.equalsIgnoreCase("string")) {
            return "string";

        } else if (databridgeAttributeType.equalsIgnoreCase("bool")) {
            return "boolean";
        } else if (databridgeAttributeType.equalsIgnoreCase("int")) {
            return "int";
        } else if (databridgeAttributeType.equalsIgnoreCase("float")) {
            return "float";
        } else if (databridgeAttributeType.equalsIgnoreCase("long")) {
            return "long";
        } else if (databridgeAttributeType.equalsIgnoreCase("double")) {
            return "double";
        } else if (databridgeAttributeType.equalsIgnoreCase("boolean")) {
            return "boolean";
        } else if (databridgeAttributeType.equalsIgnoreCase("integer")) {
            return "int";
        }
        return "string";
    }

    public static String getSiddhiStreamDefinition(StreamDefinition streamDef) {
        StringBuilder definitionBuilder = new StringBuilder("define stream ");
        definitionBuilder.append(AlertConfigurationHelper.convertToSiddhiInputStreamName(streamDef.getName()));
        definitionBuilder.append(" (");


        boolean appendComma = false;

        if (streamDef.getMetaData() != null) {
            for (Attribute attribute : streamDef.getMetaData()) {
                if (appendComma) {
                    definitionBuilder.append(", ");
                }
                definitionBuilder.append("meta_" + attribute.getName());
                switch (attribute.getType()) {
                    case BOOL:
                        definitionBuilder.append(" bool");
                        break;
                    case INT:
                        definitionBuilder.append(" int");
                        break;
                    case LONG:
                        definitionBuilder.append(" long");
                        break;
                    case FLOAT:
                        definitionBuilder.append(" float");
                        break;
                    case DOUBLE:
                        definitionBuilder.append(" double");
                        break;
                    case STRING:
                        definitionBuilder.append(" string");
                        break;
                }
                appendComma = true;
            }
        }

        if (streamDef.getCorrelationData() != null) {
            for (Attribute attribute : streamDef.getCorrelationData()) {
                if (appendComma) {
                    definitionBuilder.append(", ");
                }
                definitionBuilder.append("correlation_" + attribute.getName());
                switch (attribute.getType()) {
                    case BOOL:
                        definitionBuilder.append(" bool");
                        break;
                    case INT:
                        definitionBuilder.append(" int");
                        break;
                    case LONG:
                        definitionBuilder.append(" long");
                        break;
                    case FLOAT:
                        definitionBuilder.append(" float");
                        break;
                    case DOUBLE:
                        definitionBuilder.append(" double");
                        break;
                    case STRING:
                        definitionBuilder.append(" string");
                        break;
                }
                appendComma = true;
            }
        }

        if (streamDef.getPayloadData() != null) {
            for (Attribute attribute : streamDef.getPayloadData()) {
                if (appendComma) {
                    definitionBuilder.append(", ");
                }
                definitionBuilder.append(attribute.getName());
                switch (attribute.getType()) {
                    case BOOL:
                        definitionBuilder.append(" bool");
                        break;
                    case INT:
                        definitionBuilder.append(" int");
                        break;
                    case LONG:
                        definitionBuilder.append(" long");
                        break;
                    case FLOAT:
                        definitionBuilder.append(" float");
                        break;
                    case DOUBLE:
                        definitionBuilder.append(" double");
                        break;
                    case STRING:
                        definitionBuilder.append(" string");
                        break;
                }
                appendComma = true;
            }
        }

        definitionBuilder.append(" )");
        return definitionBuilder.toString();
    }

    public static void removeStream(String name, String version) throws RemoteException {
        AlertConfigurationClientHolder.getInstance().getEventStreamManagerAdminServiceClient().removeEventStream(name, version);
    }

    private static boolean isTemporaryStream(String streamName) {
        return streamName.endsWith("_temp");
    }

}
