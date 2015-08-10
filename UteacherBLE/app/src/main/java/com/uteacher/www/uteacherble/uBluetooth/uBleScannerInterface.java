package com.uteacher.www.uteacherble.uBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by cartman on 15/5/25.
 */
public interface uBleScannerInterface {

    interface uBleScannerCallback {
        void onScan(final BluetoothDevice device);

        void onScanStop();
    }

    boolean startScan(long stopTime, uBleScannerCallback callback);

    boolean startScan(long stopTime, Pattern name, Pattern address, uBleScannerCallback callback);

    void stopScan();

    Collection<BluetoothDevice> getScannedDevices();

    BluetoothDevice getScannedDevice(String address);
}
