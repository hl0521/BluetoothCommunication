package com.uteacher.www.uteacherble.uBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by cartman on 15/5/25.
 */
public class uBleScanner implements uBleScannerInterface {
    private final static String TAG = uBleScanner.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter = null;
    private Handler mHandler = null;

    private HashMap<String, BluetoothDevice> mBleDevices = new HashMap<>();
    private boolean mScanning = false;

    private Pattern mPatternName = null;
    private Pattern mPatternAddress = null;
    private uBleScannerCallback mOnScanCallback = null;

    private final BluetoothAdapter.LeScanCallback mCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.v(TAG, "onLeScan device scanned, address " + device.getAddress() + ", name " + device.getName());

            if (mPatternName != null) {
                if (device.getName() == null || !mPatternName.matcher(device.getName()).matches()) {
                    Log.d(TAG, "Device Name does not match.");
                    return;
                }
            }

            if (mPatternAddress != null) {
                if (!mPatternAddress.matcher(device.getAddress()).matches()) {
                    Log.d(TAG, "Device Address does not match.");
                    return;
                }
            }

            if (!mBleDevices.containsKey(device.getAddress())) {
                mBleDevices.put(device.getAddress(), device);

                if (mOnScanCallback != null) {
                    mOnScanCallback.onScan(device);
                }
            }

        }
    };

    public uBleScanner(BluetoothAdapter adapter) {
        mBluetoothAdapter = adapter;
        mHandler = new Handler();
    }


    @Override
    public boolean startScan(long stopTime, uBleScannerCallback callback) {
        if (mScanning) {
            Log.d(TAG, "Start Scan failed: already scanning.");
            return false;
        }

        if (mBluetoothAdapter.startLeScan(mCallback)) {
            Log.v(TAG, "StartLeScan.");
            mScanning = true;
            mBleDevices.clear();
            mHandler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            stopScan();
                        }
                    }, stopTime);
            mOnScanCallback = callback;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean startScan(long stopTime, @Nullable Pattern name, @Nullable Pattern address, uBleScannerCallback callback) {
        if (mScanning) {
            Log.d(TAG, "Start Scan failed: already scanning.");
            return false;
        }

        if (startScan(stopTime, callback)) {
            mPatternName = name;
            mPatternAddress = address;
            return true;
        }
        return false;
    }

    @Override
    public void stopScan() {
        if (mScanning) {
            Log.v(TAG, "StopLeScan.");
            mBluetoothAdapter.stopLeScan(mCallback);
            mPatternName = null;
            mPatternAddress = null;
            if (mOnScanCallback != null) {
                mOnScanCallback.onScanStop();
            }
            mOnScanCallback = null;
            mScanning = false;
        }
    }

    @Override
    public Collection<BluetoothDevice> getScannedDevices() {
        return mBleDevices.values();
    }

    @Override
    public BluetoothDevice getScannedDevice(String address) {
        return mBleDevices.get(address);
    }
}
