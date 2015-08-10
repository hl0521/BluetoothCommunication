package com.uteacher.www.uteacherble.uBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

/**
 * Created by cartman on 15/6/2.
 */
public class uBluetoothFactory {

    private static uBleScanner scanner = null;
    private static uBleService service = null;

    public static uBleScanner getScannerInstance(BluetoothAdapter adapter) {
        if (scanner == null) {
            scanner = new uBleScanner(adapter);
        }
        return scanner;
    }

    public static uBleService getServiceInstance(BluetoothAdapter adapter, Context context) {
        if (service == null) {
            service = new uBleService();
        }
        service.init(adapter,context);
        return service;
    }
}
