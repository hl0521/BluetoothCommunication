package com.uteacher.www.uteacherble.uBluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.uteacher.www.uteacherble.TestUtil.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cartman on 15/5/25.
 */
public class uBleService extends Service implements uBleServiceInterface {

    private final static String TAG = uBleService.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter = null;
    private Context mContext = null;

    private uBleServiceInterface.uServiceCallback mCallback = null;

    private HashMap<String, uBleGatt> mGattHashMap = new HashMap<>();
    private HashMap<String, uServiceCallback> mCallbackHashMap = new HashMap<>();

    private ArrayList<uBleGatt> mBleGattList = new ArrayList<>();

    private uBleService mService = null;


    public class LocalBinder extends Binder {
        uBleService getService() {
            return uBleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = ((uBleService.LocalBinder) service).getService();

            // Automatically connects to the device upon successful start-up initialization.
            tryConnectAll();
            Log.v(TAG, "Service connected ");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            disconnectAndCloseAll();
            mService = null;
            Log.v(TAG, "Service disconnected ");
        }
    };

    private final static String ACTION_GATT_CONNECTED =
            "com.uteacher.www.uteacherble.ACTION_GATT_CONNECTED";
    private final static String ACTION_GATT_DISCONNECTED =
            "com.uteacher.www.uteacherble.ACTION_GATT_DISCONNECTED";
    private final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.uteacher.www.uteacherble.ACTION_GATT_SERVICES_DISCOVERED";
    private final static String ACTION_DATA_READ =
            "com.uteacher.www.uteacherble.ACTION_DATA_READ";
    private final static String ACTION_DATA_WRITE =
            "com.uteacher.www.uteacherble.ACTION_DATA_WRITE";
    private final static String ACTION_DATA_NOTIFY =
            "com.uteacher.www.uteacherble.ACTION_DATA_NOTIFY";
    private final static String EXTRA_DATA_DEVICE_ADDRESS =
            "com.uteacher.www.uteacherble.EXTRA_DATA_DEVICE_ADDRESS";
    private final static String EXTRA_DATA_CHARACTER =
            "com.uteacher.www.uteacherble.EXTRA_DATA_CHARACTER";
    private final static String EXTRA_DATA_STATUS =
            "com.uteacher.www.uteacherble.EXTRA_DATA_STATUS";
    private final static String EXTRA_DATA_VALUE =
            "com.uteacher.www.uteacherble.EXTRA_DATA_VALUE";


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                broadcastUpdate(ACTION_GATT_CONNECTED, gatt, status);
                Log.v(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                gatt.discoverServices();
                Log.v(TAG, "Attempting to start service discovery.");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.v(TAG, "Disconnected from GATT server.");
                broadcastUpdate(ACTION_GATT_DISCONNECTED, gatt, status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.v(TAG, "onServicesDiscovered received: " + status);
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.v(TAG, "onCharacteristicRead received: " + characteristic.getUuid().toString() + "," + status);
            Log.v(TAG, "data bytes: " + characteristic.getValue().length);
            broadcastUpdate(ACTION_DATA_READ, gatt, characteristic, status, characteristic.getValue());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.v(TAG, "onCharacteristicWrite received: " + characteristic.getUuid().toString() + "," + status);
            broadcastUpdate(ACTION_DATA_WRITE, gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.v(TAG, "onCharacteristicChanged received: " + characteristic.getUuid().toString());
            Log.v(TAG, "onCharacteristicChanged data: " + StringUtil.byte2String(characteristic.getValue()));
            broadcastUpdate(ACTION_DATA_NOTIFY, gatt, characteristic, characteristic.getValue());
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            final String address = intent.getStringExtra(EXTRA_DATA_DEVICE_ADDRESS);
            final String character = intent.getStringExtra(EXTRA_DATA_CHARACTER);
            final int status = intent.getIntExtra(EXTRA_DATA_STATUS, 0);
            final byte[] value = intent.getByteArrayExtra(EXTRA_DATA_VALUE);

            uServiceCallback callback = mCallbackHashMap.get(address);

            Log.v(TAG, "Service Call Back on Action " + action);
            if (ACTION_GATT_CONNECTED.equals(action)) {
                if (mCallback != null) {
                    mCallback.onConnected(address, status);
                }

                if (callback != null) {
                    callback.onConnected(address, status);
                }

            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                if (mCallback != null) {
                    mCallback.onDisconnected(address, status);
                }

                if (callback != null) {
                    callback.onDisconnected(address, status);
                }

            } else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (mCallback != null) {
                    mCallback.onServiceDiscovered(address, status);
                }

                if (callback != null) {
                    callback.onServiceDiscovered(address, status);
                }

            } else if (ACTION_DATA_READ.equals(action)) {
                if (mCallback != null) {
                    mCallback.onDataRead(address, character, status, value);
                }

                if (callback != null) {
                    callback.onDataRead(address, character, status, value);
                }

            } else if (ACTION_DATA_WRITE.equals(action)) {
                if (mCallback != null) {
                    mCallback.onDataWrite(address, character, status);
                }

                if (callback != null) {
                    callback.onDataWrite(address, character, status);
                }

            } else if (ACTION_DATA_NOTIFY.equals(action)) {
                if (mCallback != null) {
                    mCallback.onDataNotify(address, character, value);
                }

                if (callback != null) {
                    callback.onDataNotify(address, character, value);
                }

            }
        }
    };


    public void init(BluetoothAdapter adapter, Context context) {
        mBluetoothAdapter = adapter;
        mContext = context.getApplicationContext();
    }

    @Override
    public boolean bind() {
        Log.v(TAG, "Bind service on device ");
        Intent gattServiceIntent = new Intent(mContext, uBleService.class);
        return mContext.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void register(@Nullable uServiceCallback cb) {
        Log.v(TAG, "Register service :" + mContext.getClass().getName());
        mCallback = cb;
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void unregister() {
        Log.v(TAG, "Unregister service " + mContext.getClass().getName());
        mContext.unregisterReceiver(mGattUpdateReceiver);
        mCallback = null;
    }

    @Override
    public void unbind() {
        Log.v(TAG, "Unbind service " + mContext.getClass().getName());
        mContext.unbindService(mServiceConnection);
    }

    @Override
    public boolean connect(String address) {
        uBleGatt gatt = new uBleGatt(mBluetoothAdapter, address);
        if (gatt.connect(mContext, mGattCallback)) {
            mGattHashMap.put(address, gatt);
            return true;
        }
        Log.d(TAG, "failed gatt connect.");
        return false;
    }

    @Override
    public boolean connect(String address, uServiceCallback cb) {
        uBleGatt gatt = new uBleGatt(mBluetoothAdapter, address);
        if (gatt.connect(mContext, mGattCallback)) {
            mGattHashMap.put(address, gatt);
            mCallbackHashMap.put(address, cb);
            return true;
        }
        Log.d(TAG, "failed gatt connect");
        return false;
    }

    private void tryConnectAll() {
        for (uBleGatt gatt : mGattHashMap.values()) {
            gatt.connect(mService, mGattCallback);
        }
    }

    private void disconnectAndCloseAll() {
        for (uBleGatt gatt : mGattHashMap.values()) {
            gatt.disconnect();
            gatt.close();
        }
        mGattHashMap.clear();
        mCallbackHashMap.clear();
    }

    @Override
    public void disconnect(String address) {
        uBleGatt gatt = mGattHashMap.get(address);
        if (gatt != null) {
            Log.v(TAG, "disconnect " + address);
            gatt.disconnect();
        }
    }

    @Override
    public void close(String address) {
        uBleGatt gatt = mGattHashMap.get(address);
        if (gatt != null) {
            Log.v(TAG, "close " + address);
            gatt.close();
            mGattHashMap.remove(address);
            mCallbackHashMap.remove(address);
        }
    }

    @Override
    public boolean write(String address, String serviceId, String characterId, byte[] value) {
        uBleGatt gatt = mGattHashMap.get(address);
        if (gatt != null) {
            Log.v(TAG, "write: " + address + "," + characterId);
            return gatt.writeCharacteristic(serviceId, characterId, value);
        }
        return false;
    }

    @Override
    public boolean enableNotify(String address, String serviceId, String characterId) {
        uBleGatt gatt = mGattHashMap.get(address);
        if (gatt != null) {
            Log.v(TAG, "enableNotify: " + address + "," + characterId);
            return gatt.setCharacteristicNotification(serviceId, characterId, true);
        }
        return false;
    }

    @Override
    public boolean read(String address, String serviceId, String characterId) {
        uBleGatt gatt = mGattHashMap.get(address);
        if (gatt != null) {
            Log.v(TAG, "read: " + address + "," + characterId);
            return gatt.readCharacteristic(serviceId, characterId);
        }
        return false;
    }

    @Override
    public List<BluetoothGattService> getServices(String address) {
        uBleGatt gatt = mGattHashMap.get(address);
        if (gatt != null) {
            return gatt.getServices();
        }
        return null;
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_READ);
        intentFilter.addAction(ACTION_DATA_WRITE);
        intentFilter.addAction(ACTION_DATA_NOTIFY);
        return intentFilter;
    }

    private void broadcastUpdate(final String action, final BluetoothGatt gatt, final int status) {
        final Intent intent = new Intent(action);

        intent.putExtra(EXTRA_DATA_DEVICE_ADDRESS, gatt.getDevice().getAddress());
        intent.putExtra(EXTRA_DATA_STATUS, status);
        Log.v(TAG, "broadcastUpdate: " + gatt.getDevice().getAddress());
        mService.sendBroadcast(intent);
    }


    private void broadcastUpdate(final String action,
                                 final BluetoothGatt gatt,
                                 final BluetoothGattCharacteristic characteristic,
                                 final int status) {
        final Intent intent = new Intent(action);

        intent.putExtra(EXTRA_DATA_DEVICE_ADDRESS, gatt.getDevice().getAddress());
        intent.putExtra(EXTRA_DATA_CHARACTER, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_DATA_STATUS, status);
        Log.v(TAG, "broadcastUpdate: " + gatt.getDevice().getAddress() + "," + characteristic.getUuid().toString());
        mService.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGatt gatt,
                                 final BluetoothGattCharacteristic characteristic,
                                 final int status,
                                 final byte[] value) {
        final Intent intent = new Intent(action);

        intent.putExtra(EXTRA_DATA_DEVICE_ADDRESS, gatt.getDevice().getAddress());
        intent.putExtra(EXTRA_DATA_CHARACTER, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_DATA_STATUS, status);
        intent.putExtra(EXTRA_DATA_VALUE, value);
        Log.v(TAG, "broadcastUpdate: " + gatt.getDevice().getAddress() + "," + characteristic.getUuid().toString());
        mService.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGatt gatt,
                                 final BluetoothGattCharacteristic characteristic,
                                 final byte[] value) {

        final Intent intent = new Intent(action);

        intent.putExtra(EXTRA_DATA_DEVICE_ADDRESS, gatt.getDevice().getAddress());
        intent.putExtra(EXTRA_DATA_CHARACTER, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_DATA_VALUE, value);
        Log.v(TAG, "broadcastUpdate: " + gatt.getDevice().getAddress() + "," + characteristic.getUuid().toString());
        mService.sendBroadcast(intent);
    }

}
