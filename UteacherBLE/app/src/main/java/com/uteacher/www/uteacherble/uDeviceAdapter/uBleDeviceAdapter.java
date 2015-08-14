package com.uteacher.www.uteacherble.uDeviceAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.util.Log;

import com.uteacher.www.uteacherble.TestUtil.StringUtil;
import com.uteacher.www.uteacherble.uBluetooth.uBleService;
import com.uteacher.www.uteacherble.uBluetooth.uBleServiceInterface;

/**
 * Created by cartman on 15/5/26.
 */
public class uBleDeviceAdapter extends uAbstractDeviceAdapter implements uBleDeviceInterface, uBleServiceInterface.uServiceCallback {
    private static final String TAG = uBleDeviceAdapter.class.getSimpleName();

    private uBleService mService;
    private BluetoothDevice mDevice;
    private Handler mHandler;
    private adapterCallback mAdapterCallback;
    private deviceCallback mDeviceCallback;

    public uBleDeviceAdapter(uBleService service, BluetoothDevice device, adapterCallback adapterCallback, deviceCallback deviceCallback) {
        mService = service;
        mDevice = device;
        mHandler = new Handler();
        mAdapterCallback = adapterCallback;
        mDeviceCallback = deviceCallback;
    }

    @Override
    public boolean connect() {
        return mService.connect(mDevice.getAddress(), this);
    }

    @Override
    public void disconnect() {
        mService.disconnect(mDevice.getAddress());
    }

    @Override
    public boolean setDeviceName(String name) {
        byte[] data = name.getBytes();
        if (data.length > DEVICE_NAME_LEN) {
            Log.d(TAG, "setDeviceName invalid length " + data.length);
            return false;
        }

        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.NAME_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.NAME_CHARACTER);

        Log.v(TAG, "setDeviceName " + service + "," + character + "," + name);

        return mService.write(mDevice.getAddress(), service, character, data);
    }

    @Override
    public boolean getDeviceName() {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.NAME_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.NAME_CHARACTER);

        Log.v(TAG, "getDeviceName " + service + "," + character);
        return mService.read(mDevice.getAddress(), service, character);
    }

    @Override
    public String getName() {
        return mDevice.getName();
    }

    @Override
    public boolean submitPassword(String password) {
        if (password.length() != PASSWORD_LEN) {
            Log.d(TAG, "submitPassword invalid length " + password.length());
            return false;
        }

        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.PASSWORD_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.PASSWORD_WRITE_CHARACTER);

        Log.v(TAG, "submitPassword " + service + "," + character + "," + password);
        return mService.write(mDevice.getAddress(), service, character, new String(password + password).getBytes());
    }

    @Override
    public boolean changePassword(String oldPwd, String newPwd) {
        if (oldPwd.length() != PASSWORD_LEN || newPwd.length() != PASSWORD_LEN) {
            Log.d(TAG, "changePassword invalid length " + oldPwd.length() + "," + newPwd.length());
            return false;
        }

        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.PASSWORD_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.PASSWORD_WRITE_CHARACTER);

        Log.v(TAG, "changePassword " + service + "," + character + "," + oldPwd + "," + newPwd);
        return mService.write(mDevice.getAddress(), service, character, new String(oldPwd + newPwd).getBytes());
    }

    @Override
    public boolean cancelPassword(String password) {
        if (password.length() != PASSWORD_LEN) {
            Log.d(TAG, "cancelPassword invalid length " + password.length());
            return false;
        }

        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.PASSWORD_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.PASSWORD_WRITE_CHARACTER);

        Log.v(TAG, "cancelPassword " + service + "," + character + "," + password);
        return mService.write(mDevice.getAddress(), service, character,
                new String(password + uBleDeviceAttributeFactory.getAttribute(uBleDeviceAttributeFactory.ATTRIBUTE.CANCEL_PASSWORD)).getBytes());
    }

    @Override
    public String getAddress() {
        return mDevice.getAddress();
    }

    protected boolean enableDataNotify() {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.DATA_NOTIFY_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.DATA_NOTIFY_CHARACTER);

        Log.v(TAG, "enableDataNotify " + service + "," + character);
        return mService.enableNotify(mDevice.getAddress(), service, character);
    }

    protected boolean enablePasswordNotify() {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.PASSWORD_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.PASSWORD_NOTIFY_CHARACTER);

        Log.v(TAG, "enablePasswordNotify " + service + "," + character);
        return mService.enableNotify(mDevice.getAddress(), service, character);
    }

    @Override
    public boolean getTransmitDelta() {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.TRANSMIT_DELTA_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_DELTA_CHARACTER);

        Log.v(TAG, "getTransmitDelta " + service + "," + character);
        return mService.read(mDevice.getAddress(), service, character);
    }

    @Override
    public boolean setTransmitDelta(DELTA delta) {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.TRANSMIT_DELTA_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_DELTA_CHARACTER);

        Log.v(TAG, "setTransmitDelta " + service + "," + character + "," + delta.toString());
        return mService.write(mDevice.getAddress(), service, character,
                new byte[]{uBleDeviceAttributeFactory.getDeltaByte(delta)});
    }

    @Override
    public boolean getUARTRate() {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.UART_RATE_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.UART_RATE_CHARACTER);

        Log.v(TAG, "getUARTRate " + service + "," + character);
        return mService.read(mDevice.getAddress(), service, character);
    }

    @Override
    public boolean setUARTRate(RATE rate) {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.UART_RATE_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.UART_RATE_CHARACTER);

        Log.v(TAG, "setUARTRate " + service + "," + character + "," + rate.toString());
        return mService.write(mDevice.getAddress(), service, character,
                new byte[]{uBleDeviceAttributeFactory.getRateByte(rate)});
    }

    @Override
    public boolean resetDevice(RESET reset) {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.RESET_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.RESET_CHARACTER);

        Log.v(TAG, "resetDevice " + service + "," + character + "," + reset.toString());
        return mService.write(mDevice.getAddress(), service, character,
                new byte[]{uBleDeviceAttributeFactory.getResetByte(reset)});
    }

    @Override
    public boolean setBroadcastFrequency(FREQUENCY frequency) {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.BROADCAST_FREQUENCY_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.BROADCAST_FREQUENCY_CHARACTER);

        Log.v(TAG, "setBroadcastFrequency " + service + "," + character + "," + frequency.toString());
        return mService.write(mDevice.getAddress(), service, character,
                new byte[]{uBleDeviceAttributeFactory.getFrequencyByte(frequency)});
    }

    @Override
    public boolean getBroadcastFrequency() {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.BROADCAST_FREQUENCY_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.BROADCAST_FREQUENCY_CHARACTER);

        Log.v(TAG, "getBroadcastFrequency " + service + "," + character);
        return mService.read(mDevice.getAddress(), service, character);
    }

    @Override
    public boolean setTransmitPower(POWER power) {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.TRANSMIT_POWER_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_POWER_CHARACTER);

        Log.v(TAG, "setTransmitPower " + service + "," + character + "," + power.toString());
        return mService.write(mDevice.getAddress(), service, character,
                new byte[]{uBleDeviceAttributeFactory.getPowerByte(power)});
    }

    @Override
    public boolean getTransmitPower() {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.TRANSMIT_POWER_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_POWER_CHARACTER);

        Log.v(TAG, "getTransmitPower " + service + "," + character);
        return mService.read(mDevice.getAddress(), service, character);
    }

    @Override
    protected boolean writeToDevice(byte[] data) {
        String service = uBleDeviceAttributeFactory.getServiceUUID(uBleDeviceAttributeFactory.SERVICE.DATA_WRITE_SERVICE);
        String character = uBleDeviceAttributeFactory.getCharacterUUID(uBleDeviceAttributeFactory.CHARACTER.DATA_WRITE_CHARACTER);

        Log.v(TAG, "writeToDevice " + service + "," + character + "," + StringUtil.byte2String(data));
        return mService.write(mDevice.getAddress(), service, character, data);
    }


    @Override
    public void onConnected(String address, int status) {
        Log.v(TAG, "onConnected " + address + "," + status);
    }

    private STATUS fromGattStatus(int gattStatus) {
        STATUS dStatus;
        if (gattStatus == BluetoothGatt.GATT_SUCCESS) {
            dStatus = STATUS.SUCCEED;
        } else {
            dStatus = STATUS.FAILED;
        }
        return dStatus;
    }

    @Override
    public void onDisconnected(final String address, int status) {
        final STATUS dStatus = fromGattStatus(status);

        Log.v(TAG, "onDisconnected " + address + "," + status);

        if (dStatus == STATUS.SUCCEED) {
            mService.close(address);
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapterCallback.onDisconnected(address, dStatus);
            }
        });

    }

    @Override
    public void onServiceDiscovered(final String address, final int status) {
        final STATUS dStatus = fromGattStatus(status);

        Log.v(TAG, "onServiceDiscovered " + address + "," + status);

        if (dStatus == STATUS.SUCCEED) {
            enableDataNotify();
            enablePasswordNotify();
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapterCallback.onConnected(address, dStatus);
            }
        });

    }

    @Override
    public void onDataRead(final String address, String character, int status, byte[] data) {
        final STATUS dStatus = fromGattStatus(status);

        Log.v(TAG, "onDataRead " + address + "," + character + "," + status + "," + StringUtil.byte2String(data));

        if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_DELTA_CHARACTER))) {
            final DELTA delta = uBleDeviceAttributeFactory.parseDeltaFromByte(data[0]);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onGetTransmitDelta(address, delta, dStatus);
                }
            });

        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.NAME_CHARACTER))) {

            final String name = new String(data);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onGetDeviceName(address, name, dStatus);
                }
            });

        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_POWER_CHARACTER))) {

            final POWER power = uBleDeviceAttributeFactory.parsePowerFromByte(data[0]);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onGetTransmitPower(address, power, dStatus);
                }
            });

        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.BROADCAST_FREQUENCY_CHARACTER))) {

            final FREQUENCY frequency = uBleDeviceAttributeFactory.parseFrequencyFromByte(data[0]);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onGetBroadcastFrequency(address, frequency, dStatus);
                }
            });

        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.UART_RATE_CHARACTER))) {

            final RATE rate = uBleDeviceAttributeFactory.parseRateFromByte(data[0]);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onGetUARTRate(address, rate, dStatus);
                }
            });
        }
    }

    @Override
    public void onDataWrite(final String address, String character, int status) {
        final STATUS dStatus = fromGattStatus(status);

        Log.v(TAG, "onDataWrite " + address + "," + character + "," + status);

        if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_DELTA_CHARACTER))) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onSetTransmitDelta(address, dStatus);
                }
            });
        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.NAME_CHARACTER))) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onSetDeviceName(address, dStatus);
                }
            });
        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.TRANSMIT_POWER_CHARACTER))) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onSetTransmitPower(address, dStatus);
                }
            });
        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.BROADCAST_FREQUENCY_CHARACTER))) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onSetBroadcastFrequency(address, dStatus);
                }
            });
        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.UART_RATE_CHARACTER))) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onSetUARTRate(address, dStatus);
                }
            });
        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.RESET_CHARACTER))) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onResetDevice(address, dStatus);
                }
            });
        }
    }

    @Override
    public void onDataNotify(final String address, String character, final byte[] data) {

        Log.v(TAG, "onDataNotify " + address + "," + character + "," + StringUtil.byte2String(data));

        if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.DATA_NOTIFY_CHARACTER))) {
            receive(data);         // receive will enqueue data to connection queue for later handling.
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDeviceCallback.onReceiveData(address, data);
                }
            });
        } else if (character.equals(uBleDeviceAttributeFactory.getCharacterUUID(
                uBleDeviceAttributeFactory.CHARACTER.PASSWORD_NOTIFY_CHARACTER))) {
            if (data[0] == PWD_RIGHT_EVENT) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceCallback.onPasswordVerified(address);
                    }
                });
            } else if (data[0] == PWD_ERROR_EVENT) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceCallback.onIncorrectPassword(address);
                    }
                });

            } else if (data[0] == PWD_UPDATED_EVENT) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceCallback.onPasswordUpdated(address);
                    }
                });

            } else if (data[0] == PWD_CANCEL_EVENT) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceCallback.onPassowrdCancelled(address);
                    }
                });
            }
        }
    }
}
