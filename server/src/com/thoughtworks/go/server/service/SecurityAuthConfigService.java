/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.server.service;

import com.thoughtworks.go.config.SecurityAuthConfig;
import com.thoughtworks.go.config.SecurityAuthConfigs;
import com.thoughtworks.go.config.update.SecurityAuthConfigCreateCommand;
import com.thoughtworks.go.config.update.SecurityAuthConfigDeleteCommand;
import com.thoughtworks.go.config.update.SecurityAuthConfigUpdateCommand;
import com.thoughtworks.go.i18n.LocalizedMessage;
import com.thoughtworks.go.plugin.access.authorization.AuthorizationExtension;
import com.thoughtworks.go.server.domain.Username;
import com.thoughtworks.go.server.service.result.LocalizedOperationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuthConfigService extends PluginProfilesService<SecurityAuthConfig> {
    private final AuthorizationExtension authorizationExtension;

    @Autowired
    public SecurityAuthConfigService(GoConfigService goConfigService, EntityHashingService hashingService, AuthorizationExtension authorizationExtension) {
        super(goConfigService, hashingService);
        this.authorizationExtension = authorizationExtension;
    }

    protected SecurityAuthConfigs getPluginProfiles() {
        return goConfigService.serverConfig().security().securityAuthConfigs();
    }

    public void update(Username currentUser, String md5, SecurityAuthConfig newSecurityAuthConfig, LocalizedOperationResult result) {
        update(currentUser, newSecurityAuthConfig, result, new SecurityAuthConfigUpdateCommand(goConfigService, newSecurityAuthConfig, authorizationExtension, currentUser, result, hashingService, md5));
    }

    public void delete(Username currentUser, SecurityAuthConfig newSecurityAuthConfig, LocalizedOperationResult result) {
        update(currentUser, newSecurityAuthConfig, result, new SecurityAuthConfigDeleteCommand(goConfigService, newSecurityAuthConfig,authorizationExtension, currentUser, result));
        if (result.isSuccessful()) {
            result.setMessage(LocalizedMessage.string("RESOURCE_DELETE_SUCCESSFUL", "security auth config", newSecurityAuthConfig.getId()));
        }
    }

    public void create(Username currentUser, SecurityAuthConfig securityAuthConfig, LocalizedOperationResult result) {
        update(currentUser, securityAuthConfig, result, new SecurityAuthConfigCreateCommand(goConfigService, securityAuthConfig, authorizationExtension, currentUser, result));
    }

}
