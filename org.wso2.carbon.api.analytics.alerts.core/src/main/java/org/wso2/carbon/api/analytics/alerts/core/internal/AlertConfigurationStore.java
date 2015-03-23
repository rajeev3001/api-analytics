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
import org.wso2.carbon.api.analytics.alerts.core.internal.ds.ServiceHolder;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.core.utils.RegistryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AlertConfigurationStore {

    // todo caching.
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, AlertConfiguration>> tenantSpecificConfigurations;
    private static AlertConfigurationStore instance;

    private AlertConfigurationStore() {
        this.tenantSpecificConfigurations = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, AlertConfiguration>>();
    }

    public static AlertConfigurationStore getInstance() {
        if (instance == null) {
            instance = new AlertConfigurationStore();
        }
        return instance;
    }


    public List<AlertConfiguration> getAllAlertConfigurations(int tenantId) throws RegistryException {

        List<AlertConfiguration> configs = new ArrayList<AlertConfiguration>();
        UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenantId);

        if (!registry.resourceExists(AlertConfigurationConstants.REGISTRY_PATH)) {
            registry.put(AlertConfigurationConstants.REGISTRY_PATH, registry.newCollection());
        } else {
            org.wso2.carbon.registry.core.Collection collection =
                    (org.wso2.carbon.registry.core.Collection) registry.get(AlertConfigurationConstants.REGISTRY_PATH);

            for (String configurationId : collection.getChildren()) {
                Resource resource = (Resource) registry.get(configurationId);
                AlertConfiguration alertConfig = AlertConfigurationUtil.fromJsonToAlertConfig(RegistryUtils.decodeBytes((byte[]) resource.getContent()));
                configs.add(alertConfig);
            }
        }
        return configs;
    }

    public void saveAlertConfiguration(AlertConfiguration config, int tenantId) throws RegistryException {
        UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenantId);
        if (!registry.resourceExists(AlertConfigurationConstants.REGISTRY_PATH)) {
            registry.put(AlertConfigurationConstants.REGISTRY_PATH, registry.newCollection());
        }
        Resource resource = registry.newResource();
        resource.setContent(AlertConfigurationUtil.fromAlertConfigToJson(config));
        resource.setMediaType("application/json");
        registry.put(AlertConfigurationUtil.getAlertConfigPath(config.getConfigurationId()), resource);
    }

    public AlertConfiguration getAlertConfiguration(String configurationId, int tenantId) throws RegistryException {
        UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenantId);
        Resource resource = registry.get(AlertConfigurationUtil.getAlertConfigPath(configurationId));
        AlertConfiguration alertConfig = AlertConfigurationUtil.fromJsonToAlertConfig(RegistryUtils.decodeBytes((byte[]) resource.getContent()));
        return alertConfig;
    }


    public boolean deleteAlertConfiguration(String configurationId, int tenantId) throws RegistryException {
        UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenantId);
        registry.delete(AlertConfigurationUtil.getAlertConfigPath(configurationId));
        return !registry.resourceExists(AlertConfigurationUtil.getAlertConfigPath(configurationId));

    }
}
