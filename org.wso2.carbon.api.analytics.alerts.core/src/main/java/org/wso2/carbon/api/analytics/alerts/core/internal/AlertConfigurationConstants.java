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

public interface AlertConfigurationConstants {

    String CONFIG_PREFIX = "ac_";
    String REGISTRY_PATH = "/APIAlertConfigurations";
    String SIDDHI_INPUT_STREAM_SUFFIX = "_in";

    String ADAPTOR_TYPE_SMS = "sms";
    String ADAPTOR_TYPE_EMAIL = "email";

    String ADAPTOR_TYPE_EMAIL_NAME = "emailAlertsAdaptor";
    String ADAPTOR_TYPE_SMS_NAME = "smsAlertsAdaptor";
}
