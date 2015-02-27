package org.wso2.carbon.api.analytics.alerts.core.internal;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfiguration;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationCondition;
import org.wso2.carbon.databridge.commons.Attribute;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.event.builder.stub.types.EventBuilderConfigurationDto;
import org.wso2.carbon.event.builder.stub.types.EventBuilderMessagePropertyDto;
import org.wso2.carbon.event.formatter.stub.types.EventFormatterConfigurationDto;
import org.wso2.carbon.event.formatter.stub.types.EventFormatterPropertyDto;
import org.wso2.carbon.event.formatter.stub.types.TextOutputMappingDto;
import org.wso2.carbon.event.formatter.stub.types.ToPropertyConfigurationDto;
import org.wso2.carbon.event.input.adaptor.manager.stub.types.InputEventAdaptorConfigurationInfoDto;
import org.wso2.carbon.event.output.adaptor.manager.stub.types.OutputEventAdaptorConfigurationInfoDto;
import org.wso2.carbon.event.processor.stub.types.ExecutionPlanConfigurationDto;
import org.wso2.carbon.event.processor.stub.types.StreamConfigurationDto;

public class AlertConfigurationHelper {

    public String generateSiddhiQueries(AlertConfiguration configuration) {

        String streamName = configuration.getStreamDefinition().getName();

        StringBuilder builder = new StringBuilder("define stream ");
        builder.append(streamName);
        builder.append(" (");

        for (Attribute at: configuration.getStreamDefinition().getPayloadData()) {
            builder.append(at.getName());
            builder.append(" ");
            builder.append(at.getType().toString().toLowerCase());
        }

        builder.append(" )");
        builder.append("\n");

        builder.append("from ");
        builder.append(streamName);
        builder.append('[');

        boolean concatenate = false;
        for (AlertConfigurationCondition condition: configuration.getConditions()) {
            builder.append(condition.getAttribute());
            switch (condition.getOperation()) {
                case GREATER_THAN:
                    builder.append(">");
                    break;
                case LESS_THAN:
                    builder.append("<");
                    break;
                // todo more operations.
            }
            if (concatenate) {
                builder.append(" and ");
            }
            concatenate = true;
        }
        builder.append("] ");
        builder.append(" select * insert into ").append(configuration.getOutputStream());
        return builder.toString();
    }

    public ExecutionPlanConfigurationDto getExecutionPlanDto(String execPlanName, StreamDefinition inputStreamDef, String siddhiQueries, String outputStream) throws AxisFault {
        ExecutionPlanConfigurationDto dto = new ExecutionPlanConfigurationDto();
        dto.setName(execPlanName);
        StreamConfigurationDto importedStreamConfigDto = new StreamConfigurationDto();

        importedStreamConfigDto.setSiddhiStreamName(inputStreamDef.getName());
        importedStreamConfigDto.setStreamId(inputStreamDef.getStreamId());
        dto.addImportedStreams(importedStreamConfigDto);
        dto.setQueryExpressions(siddhiQueries);

        StreamConfigurationDto exportedStreamConfigDto = new StreamConfigurationDto();
        // todo check validity
        exportedStreamConfigDto.setStreamId(outputStream);
        exportedStreamConfigDto.setSiddhiStreamName(outputStream);
        dto.addExportedStreams(exportedStreamConfigDto);
        return dto;
     }


    public EventBuilderConfigurationDto getEventBuilderDto(String adaptorName, String adaptorType,
                                                           String inputStreamName, String inputStreamVersion, String outputStreamName,
                                                           String outputStreamVersion) {

        // no custom mappings
        EventBuilderConfigurationDto dto = new EventBuilderConfigurationDto();

        dto.setEventBuilderConfigName(adaptorName + "_" + inputStreamName + "_" + outputStreamName);

        dto.setInputEventAdaptorName(adaptorName);
        dto.setInputEventAdaptorType(adaptorType);

        EventBuilderMessagePropertyDto inStreamNameDto = new EventBuilderMessagePropertyDto();
        inStreamNameDto.setKey("stream");
        inStreamNameDto.setValue(inputStreamName);

        EventBuilderMessagePropertyDto inStreamVersionDto = new EventBuilderMessagePropertyDto();
        inStreamVersionDto.setKey("version");
        inStreamVersionDto.setValue(inputStreamVersion);

        EventBuilderMessagePropertyDto[] msgPropertyDtos = new EventBuilderMessagePropertyDto[2];
        msgPropertyDtos[0] = inStreamNameDto;
        msgPropertyDtos[1] = inStreamVersionDto;

        dto.setEventBuilderMessageProperties(msgPropertyDtos);

        dto.setToStreamName(outputStreamName);
        dto.setToStreamVersion(outputStreamVersion);

        return dto;
    }

    public InputEventAdaptorConfigurationInfoDto getInputEventAdaptorDto(String name, String type) {
        InputEventAdaptorConfigurationInfoDto dto = new InputEventAdaptorConfigurationInfoDto();

        dto.setEventAdaptorName(name);
        dto.setEventAdaptorType(type);

        return dto;
    }

    public OutputEventAdaptorConfigurationInfoDto getOutputEventAdaptorDto(String name, String type) {
        OutputEventAdaptorConfigurationInfoDto dto = new OutputEventAdaptorConfigurationInfoDto();

        dto.setEventAdaptorName(name);
        dto.setEventAdaptorType(type);

        return dto;
    }

    public EventFormatterConfigurationDto getEventFormatterDto(String fromStreamName, String fromStreamVersion,
                                                               String customMapping, String toAdaptorName, String toAdaptorType,
                                                               String[][] toAdaptorProperties) {
        EventFormatterConfigurationDto dto = new EventFormatterConfigurationDto();

        dto.setEventFormatterName(toAdaptorName + "_" + fromStreamName + "_");

        dto.setFromStreamNameWithVersion(fromStreamName + ":" + fromStreamVersion);

        TextOutputMappingDto txtDto = new TextOutputMappingDto();
        txtDto.setMappingText(customMapping);

        dto.setTextOutputMappingDto(txtDto);

        ToPropertyConfigurationDto toDto = new ToPropertyConfigurationDto();
        toDto.setEventAdaptorName(toAdaptorName);
        toDto.setEventAdaptorType(toAdaptorType);

        EventFormatterPropertyDto[] propDto = new EventFormatterPropertyDto[toAdaptorProperties.length];

        for (int i= 0;i<toAdaptorProperties.length ;i++) {
            propDto[i] = new EventFormatterPropertyDto();
            propDto[i].setKey(toAdaptorProperties[i][0]);
            propDto[i].setValue(toAdaptorProperties[i][1]);

        }
        toDto.setOutputEventAdaptorMessageConfiguration(propDto);
        dto.setToPropertyConfigurationDto(toDto);

        return dto;
    }
}
