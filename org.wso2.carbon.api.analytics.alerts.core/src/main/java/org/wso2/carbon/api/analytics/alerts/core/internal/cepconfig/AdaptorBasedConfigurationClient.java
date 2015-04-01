package org.wso2.carbon.api.analytics.alerts.core.internal.cepconfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.api.analytics.alerts.core.AlertConfiguration;
import org.wso2.carbon.api.analytics.alerts.core.exception.AlertConfigurationException;
import org.wso2.carbon.api.analytics.alerts.core.internal.*;
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

import java.rmi.RemoteException;
import java.util.Map;

public class AdaptorBasedConfigurationClient implements CEPConfigurationClient {

    private static final Log log = LogFactory.getLog(AdaptorBasedConfigurationClient.class);


    @Override
    public void addAlertConfiguration(AlertConfiguration alertConfiguration, int tenantId) throws AlertConfigurationException {
        // todo check if alert config already exists ....

        EventBuilderConfigurationDto builderDto = getEventBuilderDto(alertConfiguration);
        ExecutionPlanConfigurationDto execPlanDto = getExecutionPlanDto(alertConfiguration);
        EventFormatterConfigurationDto formatterDto = getEventFormatterDto(alertConfiguration);

        try {
            // event stream is added by the datapublisher, until it is added, these will be inactive.
            // todo add wso2 event inputs and email outputs - currently their existence is assumed..

            AlertConfigurationClientFactory.getInstance().getEventBuilderAdminServiceClient().deployEventBuilderConfiguration(builderDto);
            StreamMgtHelper.addRequiredStream(alertConfiguration.getStreamDefinition(), execPlanDto.getQueryExpressions());

            AlertConfigurationClientFactory.getInstance().getEventProcessorAdminServiceClient().addExecutionPlan(execPlanDto);
            AlertConfigurationClientFactory.getInstance().getEventFormatterAdminServiceClient().addEventFormatterConfiguration(formatterDto);

            AlertConfigurationStore.getInstance().saveAlertConfiguration(alertConfiguration, tenantId);

        } catch (org.wso2.carbon.registry.api.RegistryException e) {
            log.error("Error while saving alert configuration", e);
            throw new AlertConfigurationException(e.getMessage());
        } catch (RemoteException e) {
            log.error("Error while configuring cep.", e);
            throw new AlertConfigurationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error while configuring alerts.", e);
            throw new AlertConfigurationException(e.getMessage());
        }
    }

    @Override
    public void removeAlertConfiguration(String configurationId, int tenantId) throws AlertConfigurationException {
        // todo do this for inactive ones too??
        try {
            AlertConfigurationClientFactory.getInstance().getEventFormatterAdminServiceClient().removeEventFormatterConfiguration(configurationId);
            AlertConfigurationClientFactory.getInstance().getEventProcessorAdminServiceClient().removeExecutionPlan(configurationId);
            AlertConfigurationClientFactory.getInstance().getEventBuilderAdminServiceClient().removeEventBuilderConfiguration(configurationId);

            // cleaning up output stream.
            AlertConfiguration config = AlertConfigurationStore.getInstance().getAlertConfiguration(configurationId, tenantId);
            String outputStream = AlertConfigurationHelper.getOutputStreamName(config);
            AlertConfigurationClientFactory.getInstance().getEventStreamManagerAdminServiceClient().removeEventStream(outputStream, config.getStreamDefinition().getVersion());

            AlertConfigurationStore.getInstance().deleteAlertConfiguration(configurationId, tenantId);
        } catch (org.wso2.carbon.registry.api.RegistryException e) {
            log.error("Error while removing the configurations.", e);
            throw new AlertConfigurationException(e.getMessage());
        } catch (RemoteException e) {
            log.error("Error while removing the configurations.", e);
            throw new AlertConfigurationException(e.getMessage());
        }
    }


    public ExecutionPlanConfigurationDto getExecutionPlanDto(AlertConfiguration config) {
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

        importedStreamConfigDto.setSiddhiStreamName(AlertConfigurationHelper.convertToSiddhiInputStreamName(config.getStreamDefinition().getName()));
        importedStreamConfigDto.setStreamId(config.getStreamDefinition().getStreamId());
        dto.addImportedStreams(importedStreamConfigDto);

        dto.setQueryExpressions(AlertConfigurationHelper.generateSiddhiQueries(config));

        StreamConfigurationDto exportedStreamConfigDto = new StreamConfigurationDto();
        // todo check validity
        exportedStreamConfigDto.setStreamId(AlertConfigurationHelper.getStreamId(AlertConfigurationHelper.getOutputStreamName(config), config.getStreamDefinition().getVersion()));
        exportedStreamConfigDto.setSiddhiStreamName(AlertConfigurationHelper.getOutputStreamName(config));
        dto.addExportedStreams(exportedStreamConfigDto);
        return dto;
    }


    public EventBuilderConfigurationDto getEventBuilderDto(AlertConfiguration config) {

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

    public EventFormatterConfigurationDto getEventFormatterDto(AlertConfiguration config) {
        EventFormatterConfigurationDto dto = new EventFormatterConfigurationDto();
        dto.setEventFormatterName(config.getConfigurationId());
        dto.setFromStreamNameWithVersion(AlertConfigurationHelper.getOutputStreamName(config) + ":" + config.getStreamDefinition().getVersion());

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


}
