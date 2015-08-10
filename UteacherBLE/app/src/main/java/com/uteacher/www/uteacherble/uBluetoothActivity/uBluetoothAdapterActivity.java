package com.uteacher.www.uteacherble.uBluetoothActivity;

import android.bluetooth.BluetoothDevice;

import com.uteacher.www.uteacherble.uBluetooth.uBleService;
import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceAdapter;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceInterface;
import com.uteacher.www.uteacherble.uDeviceAdapter.uDeviceAdapterInterface;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;

import java.util.HashMap;

/**
 * Created by cartman on 15/6/3.
 */
public abstract class uBluetoothAdapterActivity extends uAbstractBluetoothActivity implements
        uDeviceAdapterInterface.adapterCallback, uBleDeviceInterface.deviceCallback {

    private final static String TAG = uBluetoothAdapterActivity.class.getSimpleName();

    @Override
    public void onReceiveData(String address, byte[] data) {

    }

    public uBleDeviceAdapter newDeviceAdapter(BluetoothDevice device) {
        return new uBleDeviceAdapter(getService(), device, this, this);
    }

    @Override
    public void onConnected(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onDisconnected(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onSetTransmitDelta(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onGetTransmitDelta(String address, uBleDeviceInterface.DELTA delta, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onSetUARTRate(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onGetUARTRate(String address, uBleDeviceInterface.RATE rate, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onSetBroadcastFrequency(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onGetBroadcastFrequency(String address, uBleDeviceInterface.FREQUENCY frequency, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onSetTransmitPower(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onGetTransmitPower(String address, uBleDeviceInterface.POWER power, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onSetDeviceName(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onGetDeviceName(String address, String name, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onResetDevice(String address, uDeviceAdapterInterface.STATUS status) {

    }

    @Override
    public void onPasswordVerified(String address) {

    }

    @Override
    public void onIncorrectPassword(String address) {

    }

    @Override
    public void onPasswordUpdated(String address) {

    }

    @Override
    public void onPassowrdCancelled(String address) {

    }

    @Override
    protected void onServiceBindFailed(uBleService service) {

    }
}
