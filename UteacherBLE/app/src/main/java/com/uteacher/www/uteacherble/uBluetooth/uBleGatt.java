package com.uteacher.www.uteacherble.uBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by cartman on 15/5/26.
 */
public class uBleGatt implements uBleGattInterface {

    private final static String TAG = uBleGatt.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter = null;
    private String mAddress = null;
    private BluetoothGatt mBluetoothGatt = null;


    public uBleGatt(BluetoothAdapter adapter, String address) {
        mBluetoothAdapter = adapter;
        mAddress = address;
    }

    @Override
    public boolean connect(Context context, BluetoothGattCallback callback) {
        if (mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection: " + mAddress);
            return mBluetoothGatt.connect();
        }


        try {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
            if (device == null) {
                Log.d(TAG, "Device not found.  Unable to connect.");
                return false;
            }
            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            mBluetoothGatt = device.connectGatt(context, false, callback);
            Log.v(TAG, "Trying to create a new connection: " + mAddress);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothGatt not initialized");
            return;
        }

        Log.v(TAG, "Device disconnect.");
        mBluetoothGatt.disconnect();
    }

    @Override
    public void close() {
        if (mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothGatt not initialized");
            return;
        }

        Log.v(TAG, "Device close.");
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    @Override
    public boolean writeCharacteristic(String serviceId, String characterId, byte[] value) {
        BluetoothGattCharacteristic character = getCharacter(serviceId, characterId);
        if (character == null) {
            Log.d(TAG, "Character not found " + serviceId + "," + characterId);
            return false;
        }

        character.setValue(value);
        return mBluetoothGatt.writeCharacteristic(character);
    }

    @Override
    public boolean readCharacteristic(String serviceId, String characterId) {
        BluetoothGattCharacteristic character = getCharacter(serviceId, characterId);
        if (character == null) {
            Log.d(TAG, "Character not found " + serviceId + "," + characterId);
            return false;
        }

        return mBluetoothGatt.readCharacteristic(character);
    }

    @Override
    public boolean setCharacteristicNotification(String serviceId, String characterId, boolean enable) {
        BluetoothGattCharacteristic character = getCharacter(serviceId, characterId);
        if (character == null) {
            Log.d(TAG, "Character not found " + serviceId + "," + characterId);
            return false;
        }

        if (!mBluetoothGatt.setCharacteristicNotification(character, enable)) {
            Log.d(TAG, "setCharacteristicNotification failed " + serviceId + "," + characterId);
            return false;
        }

        for (BluetoothGattDescriptor des : character.getDescriptors()) {
            Log.v(TAG, "writeDescriptor " + des.getUuid().toString());
            if (enable) {
                des.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            } else {
                des.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            }

            if (!mBluetoothGatt.writeDescriptor(des)) {
                Log.d(TAG, "writeDescriptor failed " + serviceId + "," + characterId);
                return false;
            }
            //FIXME:enable only first descriptor?
            break;
        }

        return true;
    }


    @Override
    public List<BluetoothGattService> getServices() {
        if (mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothGatt not initialized");
            return null;
        }

        return mBluetoothGatt.getServices();
    }

    @Override
    public BluetoothGattService getService(String serviceId) {
        if (mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothGatt not initialized");
            return null;
        }

        try {
            return mBluetoothGatt.getService(UUID.fromString(serviceId));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public BluetoothGattCharacteristic getCharacter(String serviceId, String characterId) {
        BluetoothGattService service = getService(serviceId);
        if (service != null) {
            try {
                return service.getCharacteristic(UUID.fromString(characterId));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
