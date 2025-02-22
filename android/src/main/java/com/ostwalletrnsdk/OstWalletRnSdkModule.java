/*
 Copyright © 2019 OST.com Inc

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ostwalletrnsdk;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import android.util.Log;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstSession;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.OstWalletUI;
import com.ost.walletsdk.utils.CommonUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import jnr.a64asm.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OstWalletRnSdkModule extends ReactContextBaseJavaModule {

  private static final String TAG = "OstWalletRnSdkModule";

  private final ReactApplicationContext reactContext;

  public OstWalletRnSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "OstWalletSdk";
  }

  // region - Methods
  @ReactMethod
  public void initialize(
    String BASE_URL,
    ReadableMap rmConfig,
    Callback callback
  ) {
    JSONObject config = null;
    if ( null != rmConfig ) {
      try {
        config = Utils.convertMapToJson( rmConfig );
      } catch (JSONException e) {
        Log.e(TAG, "Unable to parse config");
        e.printStackTrace();
      }
    }

    try{
      OstWalletUI.initialize(getReactApplicationContext(), BASE_URL, config);
    } catch(Throwable e){
      callback.invoke( Utils.getError( e , "rn_ownsm_i_1")  );
      return;
    }
    callback.invoke();
  }

  @ReactMethod
  public void getUser(
          String userId,
          Callback callback
  ) {
    try{
      OstUser ostUser = OstSdk.getUser(userId);
      if (null == ostUser) {
        callback.invoke();
      } else {
        callback.invoke(Utils.convertJsonToMap(ostUser.getData()));
      }
    } catch(Throwable e){
      callback.invoke( Utils.getError( e , "rn_ownsm_gu_1")  );
      return;
    }
  }

  @ReactMethod
  public void getToken(
          String tokenId,
          Callback callback
  ) {
    try{
      OstToken ostToken = OstSdk.getToken(tokenId);
      if (null == ostToken) {
        callback.invoke();
      } else {
        callback.invoke(Utils.convertJsonToMap(ostToken.getData()));
      }
    } catch(Throwable e){
      callback.invoke( Utils.getError( e , "rn_ownsm_gt_1")  );
      return;
    }
  }

  @ReactMethod
  public void getCurrentDeviceForUserId(@NonNull String userId, @NonNull Callback callback) {
    try{
      OstDevice ostDevice = OstSdk.getCurrentDeviceForUserId(userId);
      if (null == ostDevice) {
        callback.invoke();
      } else {
        callback.invoke(Utils.convertJsonToMap(ostDevice.getData()));
      }
    } catch(Throwable e){
      callback.invoke( Utils.getError( e , "rn_ownsm_gcdfuid_1")  );
      return;
    }
  }

  @ReactMethod
  public void getActiveSessionsForUserId(@NonNull String userId, @Nullable String minimumSpendingLimitInWei, @NonNull Callback callback) {
    try{
      List<OstSession> activeSessions = OstSdk.getActiveSessionsForUserId(userId, minimumSpendingLimitInWei);

      // Convert to JSON Array.
      JSONArray jsonArray = new JSONArray();
      for (OstSession session : activeSessions) {
        jsonArray.put( session.getData() );
      }

      // Invoke callback
      callback.invoke(Utils.convertJsonToArray( jsonArray ));
    } catch(Throwable e){
      callback.invoke( Utils.getError( e , "rn_gasfuid_gt_1")  );
      return;
    }
  }

  @ReactMethod
  public void getAddDeviceQRCode(String userId ,Callback successCallback ,  Callback errorCallback ){
    try {
      Bitmap bitmap = OstSdk.getAddDeviceQRCode( userId );
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
      byte[] byteArray = byteArrayOutputStream .toByteArray();
      String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
      successCallback.invoke( encoded );
    }catch ( Throwable e ){
      errorCallback.invoke( Utils.getError( e , "rn_ownsm_gadqrc_1" )  );
    }
  }

  @ReactMethod
  public void isBiometricEnabled(@NonNull String userId, @NonNull Callback callback) {
    if ( null == userId ) {
      callback.invoke(false);
    }
    callback.invoke( OstSdk.isBiometricEnabled(userId) );
  }

  // endregion

  @ReactMethod
  public void setupDevice(
    String userId ,
    String tokenId ,
    String uuid) {
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SETUP_DEVICE);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    OstSdk.setupDevice(userId, tokenId, workFlowCallback  );
  }


  @ReactMethod
  public void activateUser(String userId, String pin, String passphrasePrefix,
                           String expiresAfterInSecs, String spendingLimit, String uuid ){
      UserPassphrase userPassPhrase = null;
      OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER);
      OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
      try{
         userPassPhrase = new UserPassphrase(userId, pin, passphrasePrefix) ;
      } catch(OstError e){
        workFlowCallback.flowInterrupt( context , e );
        return;
      }

      long expiresAfterInSecsLong =  Long.valueOf(expiresAfterInSecs);
      OstSdk.activateUser(userPassPhrase, expiresAfterInSecsLong, spendingLimit, workFlowCallback );
  }

  @ReactMethod
  public void addSession(String userId, String expiresAfterInSecs, String spendingLimit, String uuid ){
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SETUP_DEVICE);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );

    long expiresAfterInSecsLong = 0;
    try {
       expiresAfterInSecsLong =  Long.valueOf(expiresAfterInSecs);
    }catch (Throwable e ){
      workFlowCallback.flowInterrupt(context , new OstError( "rn_ownsm_as_1" , OstErrors.ErrorCode.INVALID_SESSION_EXPIRY_TIME));
      return;
    }

    OstSdk.addSession(userId, spendingLimit , expiresAfterInSecsLong, workFlowCallback );
  }

  @ReactMethod
  public void executeTransaction(String userId,
                                 String tokenHolderAddresses,
                                 String amounts,
                                 String ruleName,
                                 ReadableMap meta,
                                 ReadableMap options,
                                 String uuid ){

    List<String> listAddresses = null ;
    List<String> listAmounts = null ;
    Map<String, Object> metaMap =null;
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.EXECUTE_TRANSACTION);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    try {
      JSONArray jsonArrayAddresses = new JSONArray(tokenHolderAddresses);
      listAddresses = new CommonUtils().jsonArrayToList(jsonArrayAddresses);
    }catch (Throwable e ) {
      workFlowCallback.flowInterrupt(context , new OstError( "rn_ownsm_et_1" , OstErrors.ErrorCode.INVALID_JSON_ARRAY));
      return;
    }

    try{
      JSONArray jsonArrayAmounts = new JSONArray( amounts ) ;
      listAmounts = new CommonUtils().jsonArrayToList(jsonArrayAmounts);
    } catch(Throwable e){
      workFlowCallback.flowInterrupt(context , new OstError( "rn_ownsm_et_2" , OstErrors.ErrorCode.INVALID_JSON_ARRAY));
      return;
    }

    if ( null == meta ) {
      metaMap = new HashMap<>();
    } else {
      metaMap = meta.toHashMap();
    }

    HashMap<String, Object> optionsMap = null;
    if ( null != options) {
      optionsMap = options.toHashMap();
    } else {
      optionsMap = new HashMap<>();
    }
    OstSdk.executeTransaction(userId, listAddresses, listAmounts, ruleName, metaMap, optionsMap ,workFlowCallback);
  }

  @ReactMethod
  public void getDeviceMnemonics(String userId, String uuid ){
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    OstSdk.getDeviceMnemonics(userId,  workFlowCallback);
  }

  @ReactMethod
    public void authorizeCurrentDeviceWithMnemonics(String userId , String mnemonics , String uuid  ){
      byte[] byteArrayMnemonics = mnemonics.getBytes();
      OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_MNEMONICS);
      OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
      OstSdk.authorizeCurrentDeviceWithMnemonics(userId , byteArrayMnemonics ,workFlowCallback ) ;
  }

  @ReactMethod
  public void performQRAction( String userId ,  String data , String uuid  ){
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.PERFORM_QR_ACTION);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    try {
      OstSdk.performQRAction( userId , data ,workFlowCallback) ;
    }catch (Throwable e ){
        workFlowCallback.flowInterrupt(context , new OstError( "rn_ownsm_pqra_1" , OstErrors.ErrorCode.INVALID_JSON_STRING));
    }

  }

  @ReactMethod
  public void resetPin( String userId, String appSalt, String currentPin, String newPin , String uuid ){
    UserPassphrase currentPassphrase = null ;
    UserPassphrase newPassphrase = null ;
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    try{
      currentPassphrase = new UserPassphrase(userId, currentPin, appSalt) ;
      newPassphrase = new UserPassphrase(userId, newPin, appSalt) ;
    }catch ( OstError e ){
      workFlowCallback.flowInterrupt( context , e);
      return;
    }
    OstSdk.resetPin(userId, currentPassphrase, newPassphrase, workFlowCallback );
  }

  @ReactMethod
  public  void revokeDevice(String userId ,  String deviceAddress , String uuid ){
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    OstSdk.revokeDevice(userId , deviceAddress , workFlowCallback );
  }

  @ReactMethod
  public void initiateDeviceRecovery( String userId, String pin , String appSalt , String deviceAddressToRecover,
                                      String uuid  ){
    UserPassphrase passphrase = null ;
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.INITIATE_DEVICE_RECOVERY);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );

    try{
      passphrase = new UserPassphrase(userId, pin, appSalt) ;
    }catch (OstError e ){
      workFlowCallback.flowInterrupt( context , e);
      return;
    }
    OstSdk.initiateDeviceRecovery(userId, passphrase, deviceAddressToRecover, workFlowCallback );
  }

  @ReactMethod
  public void abortDeviceRecovery(String userId, String pin , String appSalt , String uuid  ){
    UserPassphrase passphrase = null ;
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ABORT_DEVICE_RECOVERY);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    try{
      passphrase = new UserPassphrase(userId, pin, appSalt) ;
    }catch (OstError e ){
      workFlowCallback.flowInterrupt( context ,  e);
      return;
    }

      OstSdk.abortDeviceRecovery(userId, passphrase, workFlowCallback );

  }

  @ReactMethod
  public void updateBiometricPreference(String userId, boolean enable, String uuid){
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.UPDATE_BIOMETRIC_PREFERENCE);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    OstSdk.updateBiometricPreference(userId , enable , workFlowCallback);
  }

  @ReactMethod
  public void logoutAllSessions(String userId , String uuid ){
    OstWorkflowContext context = new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.LOGOUT_ALL_SESSIONS);
    OstWorkFlowCallbackImpl workFlowCallback = new OstWorkFlowCallbackImpl( uuid, this.reactContext, context );
    OstSdk.logoutAllSessions(userId, workFlowCallback );
  }


}
