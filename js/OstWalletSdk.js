/*
 Copyright © 2019 OST.com Inc
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 */
 
import { NativeModules } from 'react-native';
const { OstWalletSdk } = NativeModules;

class OstWalletRNSdk {
    
     /**
     * Initialize wallet sdk
     * @param {String} endpoint - OST Platform endpoint
     * @param {Object} config - SDK Config
     * @param {function} callback -   A typical node-style, error-first callback.
     * @callback params {Object}error , {Boolean} success
     * @public
     */
    initialize( endpoint , config, callback ) {
        let fCallback, fConfig;
        if ( typeof config === 'object') {
            fConfig = config;
        } 

        if ( callback && typeof callback === 'function') {
            fCallback = callback;
        } else if ( typeof config === 'function' ) {
            fCallback = config;
        }

        OstWalletSdk.initialize( endpoint , fConfig, (error)=>{
          fCallback && fCallback( error , !error );
        });
    }

    /**
     * Setup user device 
     * @param {String} userId - Ost User id
     * @param {String} tokenId - Id assigned by Ost to token
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    setupDevice(userId, tokenId, workflow) {
        OstWalletSdk.setupDevice(userId, tokenId, workflow.uuid);
    }

     /**
     * Active user 
     * @param {String} userId - Ost User id
     * @param {String} pin - user entered pin
     * @param {String} passphrasePrefix- user passphrase prefix
     * @param {String} expiresAfterInSecs - session key expiry time. 
     * @param {String} spendingLimit - spending limit once in a transaction of session
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    activateUser(userId, pin, passphrasePrefix, expiresAfterInSecs, spendingLimit, workflow) {
        OstWalletSdk.activateUser(userId, pin, passphrasePrefix, String(expiresAfterInSecs), spendingLimit, workflow.uuid );
    }

    /**
     * Add user session
     * @param {String} userId - Ost User id
     * @param {String} expiresAfterInSecs - session key expiry time. 
     * @param {String} spendingLimit - spending limit once in a transaction of session
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    addSession(userId, expireAfterInSecs, spendingLimit, workflow) {
        OstWalletSdk.addSession(userId, String(expireAfterInSecs), spendingLimit, workflow.uuid);
    }

    /**
     * Execute user transactions
     * @param {string} userId - Ost User id
     * @param {Array<String>} tokenHolderAddresses - Token holder addresses of amount receiver. 
     * @param {Array<String>} amounts -Amounts corresponding to tokenHolderAddresses to be transfered
     * @param {String} ruleName - Rule name to be executed.
     * @param {object} meta - additional data.
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @param {object} [options={}] - Transaction workflow options.
     * @param {boolean} [options.wait_for_finalization=true]
     * @param {boolean} [options.currency_code=null]
     * @public
     */
    executeTransaction(userId, tokenHolderAddresses, amounts, ruleName, meta, workflow, options) {
        if( tokenHolderAddresses instanceof Array ){
          tokenHolderAddresses = JSON.stringify(tokenHolderAddresses);
        }
        if( amounts instanceof Array ){
          amounts = JSON.stringify(amounts);
        }

        if ( !options ) {
            options = {};
        }

        if ( typeof options.wait_for_finalization === 'undefined') {
            options.wait_for_finalization = true;
        }
       
        OstWalletSdk.executeTransaction(userId, tokenHolderAddresses, amounts, ruleName, meta , options, workflow.uuid);
    }

     /**
     * Get Device mnemonics 
     * @param {String} userId - Ost User id
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    getDeviceMnemonics(userId, workflow) {
        OstWalletSdk.getDeviceMnemonics(userId, workflow.uuid);
    }

     /**
     * Authorize user device with mnemonics
     * @param {String} userId - Ost User id
     * @param {String} mnemonics - string of mnemonics
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    authorizeCurrentDeviceWithMnemonics(userId, mnemonics, workflow) {
        OstWalletSdk.authorizeCurrentDeviceWithMnemonics(userId, mnemonics, workflow.uuid);
    }

    /**
     * Get device QR code
     * @param {String} userId - Ost User id
     * @param {function} successCallback - returns image as base64 string.
     * @param {function} errorCallback.
     * @public
     */
    getAddDeviceQRCode(userId , successCallback , errorCallback ) {
        OstWalletSdk.getAddDeviceQRCode( userId , successCallback , errorCallback); 
    }

    /**
     * Get device QR code
     * @param {String} userId - Ost User id
     * @param {String} deviceAddress - device address 
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    revokeDevice( userId , deviceAddress , workflow){
        OstWalletSdk.revokeDevice( userId , deviceAddress , workflow.uuid); 
    }

    /**
     * Perform QR action 
     * @param {String} userId - Ost User id
     * @param {String} data - Json string of payload is scanned by QR-Code.
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    performQRAction(userId, data, workflow) {
        OstWalletSdk.performQRAction( userId , data ,  workflow.uuid   );
    }

    /**
     * Reset user pin 
     * @param {String} userId - Ost User id
     * @param {String} appSalt - Passphrase prefix provided by application server
     * @param {String} currentPin - user current pin
     * @param {String} newPin - user new pin 
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    resetPin(userId, appSalt, currentPin, newPin, workflow ) {
        OstWalletSdk.resetPin( userId , appSalt , currentPin , newPin , workflow.uuid ); 
    }

    /**
     * Initiate device recovery 
     * @param {String} userId - Ost User id
     * @param {String} pin - user current pin
     * @param {String} appSalt - Passphrase prefix provided by application server
     * @param {String} deviceAddressToRecover - Device address which wants to recover
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    initiateDeviceRecovery(userId, pin, appSalt,  deviceAddressToRecover, workflow ) {
        OstWalletSdk.initiateDeviceRecovery( userId, pin, appSalt, deviceAddressToRecover, workflow.uuid ); 
    }

    /**
     * Abort device recovery 
     * @param {String} userId - Ost User id
     * @param {String} pin - user current pin
     * @param {String} appSalt - Passphrase prefix provided by application server
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    abortDeviceRecovery( userId,  pin ,  appSalt , workflow ) {
        OstWalletSdk.abortDeviceRecovery( userId,  pin ,  appSalt ,  workflow.uuid  ); 
    }

    /**
     * Update biometric prederence  
     * @param {String} userId - Ost User id
     * @param {boolean} enable - to enable biometric prefernce
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    updateBiometricPreference( userId , enable ,workflow ){
        enable =  !!enable; 
        OstWalletSdk.updateBiometricPreference( userId,  enable,  workflow.uuid  ); 
    }

     /**
     * Logout user all sessions  
     * @param {String} userId - Ost User id
     * @param {OstWalletWorkFlowCallback} workflow - callback implementation instances for application communication 
     * @public
     */
    logoutAllSessions(userId, workflow ) {
        OstWalletSdk.logoutAllSessions( userId ,  workflow.uuid  ); 
    }

    /**
    * Get token object for provided userId
    * @param {String} tokenId - Ost Token id
    * @param {function} callback - Gets token object if present else nil
    * @callback params {Object}token
    * @public
    */
    getToken(tokenId, callback) {
        OstWalletSdk.getToken(tokenId, (tokenEntity)=>{
            callback( tokenEntity );
        });
    }

  /**
   * Get user object for provided userId
   * @param {String} userId - Ost User id
   * @param {function} callback - Gets object if present else nil
   * @callback params {Object}user
   * @public
   */
    getUser(userId, callback) {
        OstWalletSdk.getUser(userId, (userEntity)=>{
          callback( userEntity );
        });
    }

  /**
   * Get current device object for provided userId
   * @param {String} userId - Ost User id
   * @param {function} callback - Gets current device object if present else nil
   * @callback params {Object} device
   * @public
   */
    getCurrentDeviceForUserId(userId, callback) {
        OstWalletSdk.getCurrentDeviceForUserId(userId, (device)=>{
          callback( device );
        });
    }

  /**
   * Get biometric preference for user
   *
   * @param userId - Ost User id
   * @param callback - Gets biometric preference boolean value
   */
  isBiometricEnabled(userId, callback) {
    OstWalletSdk.isBiometricEnabled(userId, (status) => {
      callback( status );
    })
  }

  /**
   * Get user object for provided userId
   * @param {String} userId - Ost User id
   * @param {String} minimumSpendingLimitInWei - 
   * @param {function} callback - Gets array of current device sessions.
   * @callback params {Array} array of sessions
   * @public
   */
    getActiveSessionsForUserId(userId, minimumSpendingLimitInWei, callback) {
        let theCallback;
        if ( typeof minimumSpendingLimitInWei === 'function' ) {
            theCallback = minimumSpendingLimitInWei;
            minimumSpendingLimitInWei = "0";
        } else {
            theCallback = callback;
            minimumSpendingLimitInWei = String( minimumSpendingLimitInWei );
        }
        console.log("getActiveSessionsForUserId userId", userId);
        OstWalletSdk.getActiveSessionsForUserId(userId, minimumSpendingLimitInWei, (activeSessions)=>{
          theCallback && theCallback( activeSessions );
        });
    }
}

export default new OstWalletRNSdk();
