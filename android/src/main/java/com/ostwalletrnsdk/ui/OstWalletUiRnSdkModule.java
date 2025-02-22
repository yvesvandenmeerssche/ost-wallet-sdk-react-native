/*
 Copyright © 2019 OST.com Inc

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ostwalletrnsdk.ui;

import android.app.Activity;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.ost.walletsdk.ui.OstWalletUI;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ostwalletrnsdk.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class OstWalletUiRnSdkModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public OstWalletUiRnSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "OstWalletSdkUI";
    }


    @ReactMethod
    public void activateUser(String userId, String expiresAfterInSecs, String spendingLimit, String uuid ){
        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER));

        String workflowId = OstWalletUI.activateUser(currentActivity,
                userId,
                Long.parseLong(expiresAfterInSecs),
                spendingLimit,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void initiateDeviceRecovery(String userId, String deviceAddress, String uuid ){

        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.INITIATE_DEVICE_RECOVERY));
        String workflowId = OstWalletUI.initiateDeviceRecovery(currentActivity,
                userId,
                deviceAddress,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void abortDeviceRecovery(String userId, String uuid ){

        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ABORT_DEVICE_RECOVERY));
        String workflowId = OstWalletUI.abortDeviceRecovery(currentActivity,
                userId,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void addSession(String userId, String expiresAfterInSecs, String spendingLimit, String uuid ){

        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION));
        String workflowId = OstWalletUI.createSession(currentActivity,
                userId,
                Long.parseLong(expiresAfterInSecs),
                spendingLimit,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void resetPin(String userId, String uuid) {

        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN));
        String workflowId = OstWalletUI.resetPin(currentActivity,
                userId,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);

    }

    @ReactMethod
    public void updateBiometricPreference(String userId, boolean enable, String uuid) {

        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.UPDATE_BIOMETRIC_PREFERENCE));
        String workflowId = OstWalletUI.updateBiometricPreference(currentActivity,
                userId,
                enable,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void getDeviceMnemonics(String userId, String uuid) {

        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS));
        String workflowId = OstWalletUI.getDeviceMnemonics(currentActivity,
                userId,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);

    }

    @ReactMethod
    public void revokeDevice(String userId, String address, String uuid ) {
        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE));
        String workflowId = OstWalletUI.revokeDevice(currentActivity,
                userId,
                address,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void authorizeCurrentDeviceWithMnemonics(String userId, String uuid) {
        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_MNEMONICS));
        String workflowId = OstWalletUI.authorizeCurrentDeviceWithMnemonics(currentActivity,
                userId,
                ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void getAddDeviceQRCode(String userId, String uuid) {
        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SHOW_DEVICE_QR));
        String workflowId = OstWalletUI.getAddDeviceQRCode(currentActivity, userId);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void scanQRCodeToAuthorizeDevice(String userId, String uuid) {
        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_QR_CODE));
        String workflowId = OstWalletUI.scanQRCodeToAuthorizeDevice(currentActivity, userId, ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void scanQRCodeToExecuteTransaction(String userId, String uuid) {
        Activity currentActivity = getCurrentActivity();
        OstUICallbackImpl ostUICallback = new OstUICallbackImpl( uuid, this.reactContext,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.EXECUTE_TRANSACTION));
        String workflowId = OstWalletUI.scanQRCodeToExecuteTransaction(currentActivity, userId, ostUICallback);
        SdkInteract.getInstance().subscribe(workflowId, ostUICallback);
    }

    @ReactMethod
    public void setThemeConfig(ReadableMap themeConfig){
        JSONObject themeConfigObject = null;
        try {
            themeConfigObject = Utils.convertMapToJson(themeConfig);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OstWalletUI.setThemeConfig(getReactApplicationContext(), themeConfigObject);
    }

    @ReactMethod
    public void setContentConfig(ReadableMap contentConfig)  {
        JSONObject contentConfigObject = null;
        try {
            contentConfigObject = Utils.convertMapToJson(contentConfig);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OstWalletUI.setContentConfig(getReactApplicationContext(), contentConfigObject);
    }

    @ReactMethod
    public void showComponentSheet()  {
        Activity currentActivity = getCurrentActivity();
        OstWalletUI.showComponentSheet(currentActivity);
    }
}