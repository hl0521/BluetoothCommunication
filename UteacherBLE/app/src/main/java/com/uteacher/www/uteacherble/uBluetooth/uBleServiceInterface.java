package com.uteacher.www.uteacherble.uBluetooth;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by cartman on 15/5/25.
 */
public interface uBleServiceInterface {


     interface uServiceCallback {
         void onConnected(String address, int status);

         void onDisconnected(String address, int status);

         void onServiceDiscovered(String address, int status);

         void onDataRead(String address, String character, int status, byte[] data);

         void onDataWrite(String address, String character, int status);

         void onDataNotify(String address, String character, byte[] data);
    }

     boolean bind();

     void register(@Nullable uServiceCallback cb);

     void unregister();

     void unbind();

     boolean connect(String address);

     boolean connect(String address, uServiceCallback cb);

     void disconnect(String address);

     void close(String address);

     boolean write(String address, String serviceId, String characterId, byte[] value);

     boolean enableNotify(String address, String serviceId, String characterId);

     boolean read(String address, String serviceId, String characterId);

     List<BluetoothGattService> getServices(String address);


}
