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
import org.wso2.carbon.api.analytics.alerts.core.DerivedAttribute;
import org.wso2.carbon.api.analytics.alerts.core.exception.AlertConfigurationException;
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
import java.util.regex.Pattern;

public class AlertConfigurationHelper {

    private static Pattern alphanumericPattern;

    static {
        alphanumericPattern = Pattern.compile("^[a-zA-Z0-9]*$");
    }

    public static String generateSiddhiQueries(AlertConfiguration configuration) {

        String streamName = convertToSiddhiInputStreamName(configuration.getStreamDefinition().getName());

        StringBuilder builder = new StringBuilder("");

        if (configuration.getDerivedAttributes() != null) {
            String query = getDerivedAttributesQuery(streamName, configuration.getConfigurationId(), configuration.getDerivedAttributes().get(0)); // todo one query for initial impl.
            builder.append(query).append(";");
            builder.append("\n");

            builder.append("from ");
            builder.append(getIntermediateStreamId(streamName, configuration.getConfigurationId()));

        } else {
            builder.append("from ");
            builder.append(streamName);

        }


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

    public static String getDerivedAttributesQuery(String streamName, String configurationId, DerivedAttribute attribute) {

        StringBuilder builder = new StringBuilder("");

        builder.append("from ").append(streamName);
        if (attribute.getAggregationType() != null) {
            builder.append("#window.").append(attribute.getAggregationType()).append("(");
            builder.append(attribute.getAggregationLength()).append(") ");
        }
        builder.append(" select ").append(attribute.getSelectExpressions());
        if (attribute.getGroupByAttributes() != null) {
            builder.append(" group by ");
            builder.append(attribute.getGroupByAttributes());
        }
        builder.append(" insert into ").append(getIntermediateStreamId(streamName, configurationId));
        return builder.toString();
    }


    public static String getOutputStreamName(AlertConfiguration config) {
        return config.getStreamDefinition().getName().replaceAll("\\.", "_") + "_" + config.getConfigurationId() + "_out";
    }

    public static String convertToSiddhiInputStreamName(String databridgeStreamName) {
        return databridgeStreamName.replaceAll("\\.", "_") + AlertConfigurationConstants.SIDDHI_INPUT_STREAM_SUFFIX;
    }

    public static boolean isSiddhiInputStream(String streamName) {
        return streamName.endsWith(AlertConfigurationConstants.SIDDHI_INPUT_STREAM_SUFFIX);
    }

    public static String getIntermediateStreamId(String streamName, String configurationId) {
        return streamName + "_" + configurationId + "_temp";
    }

    public static String getStreamId(String streamName, String version) {
        return streamName + ":" + version;
    }

    public static void validateAlertConfiguration(AlertConfiguration config) throws AlertConfigurationException {
        if (config.getConfigurationId() == null) {
            throw new AlertConfigurationException("Configuration id is null.");
        }
        if (alphanumericPattern.matcher(config.getConfigurationId()).matches()) {
            throw new AlertConfigurationException("Non alphanumeric characters exist in the configuration id.");
        }
    }
}
