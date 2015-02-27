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

import java.util.Map;

public class AlertConfigurationHelper {

    public static String generateSiddhiQueries(AlertConfiguration configuration) {

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
                case EQUALS:
                    builder.append("==");
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

    // String execPlanName, StreamDefinition inputStreamDef, String siddhiQueries, String outputStream
    public static ExecutionPlanConfigurationDto getExecutionPlanDto(AlertConfiguration config)  {
        ExecutionPlanConfigurationDto dto = new ExecutionPlanConfigurationDto();
        dto.setName(config.getConfigurationId());
        StreamConfigurationDto importedStreamConfigDto = new StreamConfigurationDto();

        importedStreamConfigDto.setSiddhiStreamName(config.getStreamDefinition().getName());
        importedStreamConfigDto.setStreamId(config.getStreamDefinition().getStreamId());
        dto.addImportedStreams(importedStreamConfigDto);
        dto.setQueryExpressions(generateSiddhiQueries(config));

        StreamConfigurationDto exportedStreamConfigDto = new StreamConfigurationDto();
        // todo check validity
        exportedStreamConfigDto.setStreamId(getOutputStreamName(config) + ":" + config.getStreamDefinition().getVersion());
        exportedStreamConfigDto.setSiddhiStreamName(getOutputStreamName(config));
        dto.addExportedStreams(exportedStreamConfigDto);
        return dto;
     }



    public static EventBuilderConfigurationDto getEventBuilderDto(AlertConfiguration config) {

        // no custom mappings
        EventBuilderConfigurationDto dto = new EventBuilderConfigurationDto();

        dto.setEventBuilderConfigName(AlertConfigurationValueHolder.getInstance().getInputEventAdaptorName() + "_" + config.getStreamDefinition().getName() + "_" + getOutputStreamName(config));

        dto.setInputEventAdaptorName(AlertConfigurationValueHolder.getInstance().getInputEventAdaptorName());
        dto.setInputEventAdaptorType(AlertConfigurationValueHolder.getInstance().getInputEventAdaptorType());

        dto.setCustomMappingEnabled(false);

        EventBuilderMessagePropertyDto inStreamNameDto = new EventBuilderMessagePropertyDto();
        inStreamNameDto.setKey("stream");
        inStreamNameDto.setValue(config.getStreamDefinition().getStreamId());

        EventBuilderMessagePropertyDto inStreamVersionDto = new EventBuilderMessagePropertyDto();
        inStreamVersionDto.setKey("version");
        inStreamVersionDto.setValue(config.getStreamDefinition().getVersion());

        EventBuilderMessagePropertyDto[] msgPropertyDtos = new EventBuilderMessagePropertyDto[2];
        msgPropertyDtos[0] = inStreamNameDto;
        msgPropertyDtos[1] = inStreamVersionDto;

        dto.setEventBuilderMessageProperties(msgPropertyDtos);



        dto.setToStreamName(getOutputStreamName(config));
        dto.setToStreamVersion(config.getStreamDefinition().getVersion());

        return dto;
    }

    public static InputEventAdaptorConfigurationInfoDto getInputEventAdaptorDto(String name, String type) {
        InputEventAdaptorConfigurationInfoDto dto = new InputEventAdaptorConfigurationInfoDto();

        dto.setEventAdaptorName(name);
        dto.setEventAdaptorType(type);

        return dto;
    }

    public static OutputEventAdaptorConfigurationInfoDto getOutputEventAdaptorDto(String name, String type) {
        OutputEventAdaptorConfigurationInfoDto dto = new OutputEventAdaptorConfigurationInfoDto();

        dto.setEventAdaptorName(name);
        dto.setEventAdaptorType(type);

        return dto;
    }

    /*
    String fromStreamName, String fromStreamVersion,
                                                               String customMapping, String toAdaptorName, String toAdaptorType,
                                                               String[][] toAdaptorProperties
     */
    public static EventFormatterConfigurationDto getEventFormatterDto(AlertConfiguration config) {
        EventFormatterConfigurationDto dto = new EventFormatterConfigurationDto();

        dto.setEventFormatterName(AlertConfigurationValueHolder.getInstance().getOutputEventAdaptorName() + "_" + config.getStreamDefinition().getName() + "_");

        dto.setFromStreamNameWithVersion(getOutputStreamName(config) + ":" + config.getStreamDefinition().getVersion());

        TextOutputMappingDto txtDto = new TextOutputMappingDto();
        txtDto.setMappingText(config.getOutputMapping());

        dto.setTextOutputMappingDto(txtDto);

        ToPropertyConfigurationDto toDto = new ToPropertyConfigurationDto();
        toDto.setEventAdaptorName(AlertConfigurationValueHolder.getInstance().getOutputEventAdaptorName());
        toDto.setEventAdaptorType(AlertConfigurationValueHolder.getInstance().getOutputEventAdaptorType());

        EventFormatterPropertyDto[] propDto = new EventFormatterPropertyDto[AlertConfigurationValueHolder.getInstance().getOutputAdaptorProperties().keySet().size()];
        int i = 0;
        for (Map.Entry<String, String> entry: AlertConfigurationValueHolder.getInstance().getOutputAdaptorProperties().entrySet()) {
            propDto[i] = new EventFormatterPropertyDto();
            propDto[i].setKey(entry.getKey());
            propDto[i].setValue(entry.getValue());
        }

        toDto.setOutputEventAdaptorMessageConfiguration(propDto);
        dto.setToPropertyConfigurationDto(toDto);

        return dto;
    }

    public static String getOutputStreamName(AlertConfiguration config) {
        return config.getStreamDefinition().getName().replaceAll("\\.", "_") + "_out" ;
    }
}
