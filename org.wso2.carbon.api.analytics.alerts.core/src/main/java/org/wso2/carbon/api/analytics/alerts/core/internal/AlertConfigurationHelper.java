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

import org.wso2.carbon.api.analytics.alerts.core.AlertConfiguration;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfigurationCondition;
import org.wso2.carbon.event.builder.stub.types.EventBuilderConfigurationDto;
import org.wso2.carbon.event.builder.stub.types.EventBuilderMessagePropertyDto;
import org.wso2.carbon.event.formatter.stub.types.EventFormatterConfigurationDto;
import org.wso2.carbon.event.formatter.stub.types.EventFormatterPropertyDto;
import org.wso2.carbon.event.formatter.stub.types.TextOutputMappingDto;
import org.wso2.carbon.event.formatter.stub.types.ToPropertyConfigurationDto;
import org.wso2.carbon.event.input.adaptor.manager.stub.types.InputEventAdaptorConfigurationInfoDto;
import org.wso2.carbon.event.output.adaptor.manager.stub.types.OutputEventAdaptorConfigurationInfoDto;
import org.wso2.carbon.event.processor.stub.types.ExecutionPlanConfigurationDto;
import org.wso2.carbon.event.processor.stub.types.SiddhiConfigurationDto;
import org.wso2.carbon.event.processor.stub.types.StreamConfigurationDto;

import java.util.List;
import java.util.Map;

public class AlertConfigurationHelper {

    public static String generateSiddhiQueries(AlertConfiguration configuration) {

        String streamName = convertToSiddhiInputStreamName(configuration.getStreamDefinition().getName());

        StringBuilder builder = new StringBuilder("");
        builder.append("from ");
        builder.append(streamName);
        builder.append('[');

        boolean concatenate = false;
        for (AlertConfigurationCondition condition : configuration.getConditions()) {
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
            builder.append(condition.getTargetValue());
            if (concatenate) {
                builder.append(" and ");
            }
            concatenate = true;
        }
        builder.append("] ");
        builder.append(" select * insert into ").append(getOutputStreamName(configuration));
        return builder.toString();
    }

    public static ExecutionPlanConfigurationDto getExecutionPlanDto(AlertConfiguration config) {
        ExecutionPlanConfigurationDto dto = new ExecutionPlanConfigurationDto();
        dto.setName(config.getConfigurationId());

        SiddhiConfigurationDto[] siddhiConfigurationDtos = new SiddhiConfigurationDto[2];
        siddhiConfigurationDtos[0] = new SiddhiConfigurationDto();
        siddhiConfigurationDtos[0].setKey("siddhi.enable.distributed.processing");
        siddhiConfigurationDtos[0].setValue("false");

        siddhiConfigurationDtos[1] = new SiddhiConfigurationDto();
        siddhiConfigurationDtos[1].setKey("siddhi.persistence.snapshot.time.interval.minutes");
        siddhiConfigurationDtos[1].setValue("0");

        dto.setSiddhiConfigurations(siddhiConfigurationDtos);

        StreamConfigurationDto importedStreamConfigDto = new StreamConfigurationDto();

        importedStreamConfigDto.setSiddhiStreamName(convertToSiddhiInputStreamName(config.getStreamDefinition().getName()));
        importedStreamConfigDto.setStreamId(config.getStreamDefinition().getStreamId());
        dto.addImportedStreams(importedStreamConfigDto);

        dto.setQueryExpressions(generateSiddhiQueries(config));

        StreamConfigurationDto exportedStreamConfigDto = new StreamConfigurationDto();
        // todo check validity
        exportedStreamConfigDto.setStreamId(getStreamId(getOutputStreamName(config), config.getStreamDefinition().getVersion()));
        exportedStreamConfigDto.setSiddhiStreamName(getOutputStreamName(config));
        dto.addExportedStreams(exportedStreamConfigDto);
        return dto;
    }


    public static EventBuilderConfigurationDto getEventBuilderDto(AlertConfiguration config) {

        // no custom mappings
        EventBuilderConfigurationDto dto = new EventBuilderConfigurationDto();

        dto.setEventBuilderConfigName(config.getConfigurationId());

        dto.setInputEventAdaptorName(AlertConfigurationValueHolder.getInstance().getInputEventAdaptorName());
        dto.setInputEventAdaptorType(AlertConfigurationValueHolder.getInstance().getInputEventAdaptorType());
        dto.setCustomMappingEnabled(false);

        dto.setInputMappingType(AlertConfigurationValueHolder.getInstance().getInputMappingType());

        EventBuilderMessagePropertyDto inStreamNameDto = new EventBuilderMessagePropertyDto();
        inStreamNameDto.setKey("stream");
        inStreamNameDto.setValue(config.getStreamDefinition().getName());

        EventBuilderMessagePropertyDto inStreamVersionDto = new EventBuilderMessagePropertyDto();
        inStreamVersionDto.setKey("version");
        inStreamVersionDto.setValue(config.getStreamDefinition().getVersion());

        EventBuilderMessagePropertyDto[] msgPropertyDtos = new EventBuilderMessagePropertyDto[2];
        msgPropertyDtos[0] = inStreamNameDto;
        msgPropertyDtos[1] = inStreamVersionDto;

        dto.setEventBuilderMessageProperties(msgPropertyDtos);
        dto.setToStreamName(config.getStreamDefinition().getName());
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

    public static EventFormatterConfigurationDto getEventFormatterDto(AlertConfiguration config) {
        EventFormatterConfigurationDto dto = new EventFormatterConfigurationDto();
        dto.setEventFormatterName(config.getConfigurationId());
        dto.setFromStreamNameWithVersion(getOutputStreamName(config) + ":" + config.getStreamDefinition().getVersion());

        TextOutputMappingDto txtDto = new TextOutputMappingDto();
        txtDto.setMappingText(config.getOutputMapping());
        txtDto.setRegistryResource(false);
        dto.setTextOutputMappingDto(txtDto);

        ToPropertyConfigurationDto toDto = new ToPropertyConfigurationDto();
        toDto.setEventAdaptorName(AlertConfigurationValueHolder.getInstance().getOutputEventAdaptorName());
        toDto.setEventAdaptorType(AlertConfigurationValueHolder.getInstance().getOutputEventAdaptorType());

        EventFormatterPropertyDto[] propDto = new EventFormatterPropertyDto[AlertConfigurationValueHolder.getInstance().getOutputAdaptorProperties().keySet().size()];
        int i = 0;
        for (Map.Entry<String, String> entry : AlertConfigurationValueHolder.getInstance().getOutputAdaptorProperties().entrySet()) {
            propDto[i] = new EventFormatterPropertyDto();
            propDto[i].setKey(entry.getKey());
            propDto[i].setValue(entry.getValue());
            i++;
        }

        toDto.setOutputEventAdaptorMessageConfiguration(propDto);
        dto.setToPropertyConfigurationDto(toDto);
        return dto;
    }

    public static String getOutputStreamName(AlertConfiguration config) {
        return config.getStreamDefinition().getName().replaceAll("\\.", "_") + "_out";
    }

    public static String convertToSiddhiInputStreamName(String databridgeStreamName) {
        return databridgeStreamName.replaceAll("\\.", "_") + AlertConfigurationConstants.SIDDHI_INPUT_STREAM_SUFFIX;
    }

    public static boolean isSiddhiInputStream(String streamName) {
        return streamName.endsWith(AlertConfigurationConstants.SIDDHI_INPUT_STREAM_SUFFIX);
    }

    private static String getStreamId(String streamName, String version) {
        return streamName + ":" + version;
    }
}
