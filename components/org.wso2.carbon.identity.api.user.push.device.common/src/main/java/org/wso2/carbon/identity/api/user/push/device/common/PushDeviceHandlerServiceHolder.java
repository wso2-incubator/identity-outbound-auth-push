/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.api.user.push.device.common;

import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;

/**
 * Push API device handler service holder class.
 */
public class PushDeviceHandlerServiceHolder {

    private static DeviceHandler deviceHandler;

    public static void setPushDeviceHandlerService(DeviceHandler deviceHandler) {

        PushDeviceHandlerServiceHolder.deviceHandler = deviceHandler;
    }

    /**
     * Get TaskOperationService osgi service.
     *
     * @return TaskOperationService
     */
    public static DeviceHandler getPushDeviceHandlerService() {

        return deviceHandler;
    }

}
