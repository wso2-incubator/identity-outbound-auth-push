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

package org.wso2.carbon.identity.application.authenticator.push.device.handler;

import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationDiscoveryData;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationRequest;

import java.util.List;

/**
 * This interface contains the operations of the DeviceHandlerImpl class.
 */
public interface DeviceHandler {

    /**
     * Register a new device.
     *
     * @param registrationRequest HTTP request for device registration
     * @return registered device
     * @throws PushDeviceHandlerServerException
     * @throws PushDeviceHandlerClientException
     */
    Device registerDevice(RegistrationRequest registrationRequest)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException;

    /**
     * Unregister a device.
     *
     * @param deviceId ID of the device to unregister
     * @throws PushDeviceHandlerClientException
     * @throws PushDeviceHandlerServerException
     */
    void unregisterDevice(String deviceId) throws PushDeviceHandlerClientException, PushDeviceHandlerServerException;

    /**
     * Remove all devices of a registered user.
     *
     * @param userId Unique ID of the user
     * @throws PushDeviceHandlerServerException if the UserID is not valid
     * @throws PushDeviceHandlerClientException if server errors occur
     */
    void removeUserDevices(String userId) throws PushDeviceHandlerServerException, PushDeviceHandlerClientException;

    /**
     * Edit the name of a registered device.
     *
     * @param deviceId ID of the device to update the name of
     * @param path     Indication of the attribute to be modified
     * @param value    New value for the attribute to be modified
     * @throws PushDeviceHandlerServerException
     */
    void editDevice(String deviceId, String path, String value)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException;

    /**
     * Get a device by the device ID.
     *
     * @param deviceId ID of the registered device
     * @return the device
     * @throws PushDeviceHandlerClientException
     * @throws PushDeviceHandlerServerException
     */
    Device getDevice(String deviceId) throws PushDeviceHandlerClientException, PushDeviceHandlerServerException;

    /**
     * Get the list of registered devices for a given user.
     *
     * @param userId Unique ID of the authenticated user
     * @return list of devices of the authenticated user
     * @throws PushDeviceHandlerServerException
     */
    List<Device> listDevices(String userId) throws PushDeviceHandlerServerException;

    /**
     * Get discovery data for a new device registration.
     *
     * @return discovery data
     */
    RegistrationDiscoveryData getRegistrationDiscoveryData() throws PushDeviceHandlerServerException;

    /**
     * Get public key for registered device.
     *
     * @param deviceId ID of the registered device
     * @return Public key string
     * @throws PushDeviceHandlerServerException
     */
    String getPublicKey(String deviceId) throws PushDeviceHandlerServerException, PushDeviceHandlerClientException;

}
