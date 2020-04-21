/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache;

import org.wso2.carbon.identity.application.common.cache.CacheEntry;

import java.util.UUID;

/**
 * Biometric device handler cache entry.
 */
public class RegistrationRequestChallengeCacheEntry extends CacheEntry {
    private UUID challenge;

    public RegistrationRequestChallengeCacheEntry(UUID challenge) {
        this.challenge = challenge;
    }

    public UUID getChallenge() {
        return challenge;
    }
}