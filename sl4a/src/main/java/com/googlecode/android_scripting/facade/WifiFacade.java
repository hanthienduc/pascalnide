/*
 *  Copyright 2017 Tran Le Duy
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

package com.googlecode.android_scripting.facade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.googlecode.android_scripting.jsonrpc.RpcReceiver;
import com.googlecode.android_scripting.rpc.PascalMethod;
import com.googlecode.android_scripting.rpc.RpcOptional;
import com.googlecode.android_scripting.rpc.RpcParameter;

import java.util.List;

/**
 * Wifi functions.
 */
public class WifiFacade extends RpcReceiver {

    private WifiManager mWifi;
    private WifiLock mLock;

    @SuppressLint("WifiManagerLeak")
    public WifiFacade(FacadeManager manager) {
        super(manager);
        Context mContext = manager.getContext();
        mWifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mLock = null;
    }

    private void makeLock(int wifiMode) {
        if (mLock == null) {
            mLock = mWifi.createWifiLock(wifiMode, "sl4a");
            mLock.acquire();
        }
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Returns the list of access points found during the most recent Wifi scan.")
    public List<ScanResult> wifiGetScanResults() {
        return mWifi.getScanResults();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Acquires a full Wifi lock.")
    public void wifiLockAcquireFull() {
        makeLock(WifiManager.WIFI_MODE_FULL);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Acquires a scan only Wifi lock.")
    public void wifiLockAcquireScanOnly() {
        makeLock(WifiManager.WIFI_MODE_SCAN_ONLY);
    }

    @PascalMethod(description = "Releases a previously acquired Wifi lock.")
    public void wifiLockRelease() {
        if (mLock != null) {
            mLock.release();
            mLock = null;
        }
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Starts a scan for Wifi access points.", returns = "True if the scan was initiated successfully.")
    public Boolean wifiStartScan() {
        return mWifi.startScan();
    }

    @PascalMethod(description = "Checks Wifi state.", returns = "True if Wifi is enabled.")
    public Boolean checkWifiState() {
        return mWifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Toggle Wifi on and off.", returns = "True if Wifi is enabled.")
    public Boolean toggleWifiState(@RpcParameter(name = "enabled") @RpcOptional Boolean enabled) {
        if (enabled == null) {
            enabled = !checkWifiState();
        }
        mWifi.setWifiEnabled(enabled);
        return enabled;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Disconnects from the currently active access point.", returns = "True if the operation succeeded.")
    public Boolean wifiDisconnect() {
        return mWifi.disconnect();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Returns information about the currently active access point.")
    public WifiInfo wifiGetConnectionInfo() {
        return mWifi.getConnectionInfo();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Reassociates with the currently active access point.", returns = "True if the operation succeeded.")
    public Boolean wifiReassociate() {
        return mWifi.reassociate();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Reconnects to the currently active access point.", returns = "True if the operation succeeded.")
    public Boolean wifiReconnect() {
        return mWifi.reconnect();
    }

    @Override
    public void shutdown() {
        wifiLockRelease();
    }
}