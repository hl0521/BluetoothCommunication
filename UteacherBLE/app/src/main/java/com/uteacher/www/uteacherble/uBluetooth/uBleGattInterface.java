package com.uteacher.www.uteacherble.uBluetooth;

import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.List;

/**
 * Created by cartman on 15/5/26.
 */
public interface uBleGattInterface {
    boolean connect(Context context, BluetoothGattCallback callback);

    void disconnect();

    void close();

    boolean writeCharacteristic(String serviceId, String characterId, byte[] value);

    boolean readCharacteristic(String serviceId, String characterId);

    boolean setCharacteristicNotification(String serviceId, String characterId, boolean enable);

    List<BluetoothGattService> getServices();

    BluetoothGattService getService(String serviceId);

    BluetoothGattCharacteristic getCharacter(String serviceId, String characterId);
}
