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

package org.wso2.carbon.identity.application.authenticator.biometric.device.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.BiometricDeviceHandlerCacheKey;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.DeviceCache;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.DeviceCacheEntry;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.RegistrationRequestChallengeCache;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.RegistrationRequestChallengeCacheEntry;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.dao.DeviceDAOMockImpl;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricdeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.DiscoveryData;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.RegistrationRequest;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

/**
 * This class implements the DeviceHandler interface .
 */
public class DeviceHandlerImpl implements DeviceHandler {
    private static final Log log = LogFactory.getLog(DeviceHandler.class);

    @Override
    public Device registerDevice(RegistrationRequest registrationRequest)
            throws BiometricDeviceHandlerClientException, BiometricdeviceHandlerServerException, SQLException,
            UserStoreException, JsonProcessingException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, InvalidKeyException {
        Device device = null;
        User user = getAuthenticatedUser();
        String cacheId = user.getUserName() + user.getUserStoreDomain() + user.getTenantDomain();
        RegistrationRequestChallengeCacheEntry cacheEntry = RegistrationRequestChallengeCache.getInstance()
                .getValueFromCacheByRequestId(new BiometricDeviceHandlerCacheKey(cacheId));
        if (log.isDebugEnabled()) {
            log.debug("Verifying digital signature");
        }
        if (!verifySignature(registrationRequest.getSignature(), registrationRequest.getPublicKey(), cacheEntry)) {
            throw new BiometricdeviceHandlerServerException("Could not verify source");
        }
        device = new Device(registrationRequest.getDeviceName(), registrationRequest.getDeviceModel(),
                registrationRequest.getPushId(), registrationRequest.getPublicKey());
        device.setDeviceId(UUID.randomUUID().toString());
        DeviceCache.getInstance().addToCacheByRequestId(new BiometricDeviceHandlerCacheKey(device.getDeviceId()),
                new DeviceCacheEntry(device));
        DeviceDAOMockImpl.getInstance().registerDevice(device);
        return device;
    }

    @Override
    public void unregisterDevice(String deviceId) throws BiometricdeviceHandlerServerException,
            BiometricDeviceHandlerClientException, SQLException {
        DeviceDAOMockImpl.getInstance().unregisterDevice(deviceId);

    }

    @Override
    public void editDeviceName(String deviceId, String newDeviceName) throws BiometricdeviceHandlerServerException,
            SQLException {
        DeviceCacheEntry cacheEntry = DeviceCache.getInstance()
                .getValueFromCacheByRequestId(new BiometricDeviceHandlerCacheKey(deviceId));
        if (cacheEntry != null) {
            if (cacheEntry.getDevice().getDeviceName().equals(newDeviceName)) {
                DeviceDAOMockImpl.getInstance().editDeviceName(deviceId, newDeviceName);
            }
            DeviceCache.getInstance().clearCacheEntryByRequestId(new BiometricDeviceHandlerCacheKey(deviceId));
        } else {
            DeviceDAOMockImpl.getInstance().editDeviceName(deviceId, newDeviceName);
        }
    }

    @Override
    public Device getDevice(String deviceId) throws BiometricDeviceHandlerClientException, SQLException,
            BiometricdeviceHandlerServerException, IOException {
        return DeviceDAOMockImpl.getInstance().getDevice(deviceId);
    }

    @Override
    public ArrayList<Device> lisDevices() throws BiometricdeviceHandlerServerException,
            BiometricDeviceHandlerClientException, SQLException, UserStoreException, IOException {
        return DeviceDAOMockImpl.getInstance().listDevices();
    }

    @Override
    public DiscoveryData getDiscoveryData() {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving data to generate QR code");
        }
        User user = getAuthenticatedUser();
        String tenantDomain = user.getTenantDomain();
        UUID challenge = UUID.randomUUID();
        String registrationUrl = "https://localhost:9443/t/" +
                user.getTenantDomain() + "/me/biometricdevice/";
        String authUrl = "https://localhost:9443/t/" + user.getTenantDomain() + "/me/biometric-auth";
        String cacheId = user.getUserName() + user.getUserStoreDomain() + tenantDomain;
        RegistrationRequestChallengeCache.getInstance().addToCacheByRequestId
                (new BiometricDeviceHandlerCacheKey(cacheId), new RegistrationRequestChallengeCacheEntry(challenge));
        return new DiscoveryData(user.getUserName(), tenantDomain,
                user.getUserStoreDomain(), challenge, registrationUrl, authUrl);
    }

    private User getAuthenticatedUser() {
        User user = User.getUserFromUserName(CarbonContext.getThreadLocalCarbonContext().getUsername());
        user.setTenantDomain(CarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return user;
    }

    private boolean verifySignature(String signature, String publicKeyStr,
                                    RegistrationRequestChallengeCacheEntry cacheEntry)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        Signature sign = Signature.getInstance("SHA256withDSA");
        byte[] publicKeyData = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory kf = KeyFactory.getInstance("DSA");
        PublicKey publicKey = kf.generatePublic(spec);
        sign.initVerify(publicKey);
        sign.update(cacheEntry.getChallenge().toString().getBytes());
        return sign.verify(signatureBytes);
    }

}