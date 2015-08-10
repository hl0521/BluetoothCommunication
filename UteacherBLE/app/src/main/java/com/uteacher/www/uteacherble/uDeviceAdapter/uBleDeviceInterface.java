package com.uteacher.www.uteacherble.uDeviceAdapter;

/**
 * Created by cartman on 15/6/2.
 */
public interface uBleDeviceInterface {

    interface deviceCallback {
        void onSetTransmitDelta(String address, uDeviceAdapterInterface.STATUS status);

        void onGetTransmitDelta(String address, DELTA delta, uDeviceAdapterInterface.STATUS status);

        void onSetUARTRate(String address, uDeviceAdapterInterface.STATUS status);

        void onGetUARTRate(String address, RATE rate, uDeviceAdapterInterface.STATUS status);

        void onSetBroadcastFrequency(String address, uDeviceAdapterInterface.STATUS status);

        void onGetBroadcastFrequency(String address, FREQUENCY frequency, uDeviceAdapterInterface.STATUS status);

        void onSetTransmitPower(String address, uDeviceAdapterInterface.STATUS status);

        void onGetTransmitPower(String address, POWER power, uDeviceAdapterInterface.STATUS status);

        void onSetDeviceName(String address, uDeviceAdapterInterface.STATUS status);

        void onGetDeviceName(String address, String name, uDeviceAdapterInterface.STATUS status);

        void onResetDevice(String address, uDeviceAdapterInterface.STATUS status);

        void onPasswordVerified(String address);

        void onIncorrectPassword(String address);

        void onPasswordUpdated(String address);

        void onPassowrdCancelled(String address);

        void onReceiveData(String address, byte[] data);

    }

    boolean getTransmitDelta();

    enum DELTA {
        MS_20,
        MS_50,
        MS_100,
        MS_200,
        MS_300,
        MS_400,
        MS_500,
        MS_1000,
        MS_2000,
    }

    boolean setTransmitDelta(DELTA delta);

    boolean getUARTRate();

    enum RATE {
        BPS_4800,
        BPS_9600,
        BPS_19200,
        BPS_38400,
        BPS_57600,
        BPS_115200,
    }

    boolean setUARTRate(RATE rate);


    enum RESET {
        GENERAL,
        SHALLOW,
        DEEP,
    }

    boolean resetDevice(RESET reset);

    enum FREQUENCY {
        MS_200,
        MS_500,
        MS_1000,
        MS_1500,
        MS_2000,
        MS_2500,
        MS_3000,
        MS_4000,
        MS_5000,
    }

    boolean setBroadcastFrequency(FREQUENCY frequency);

    boolean getBroadcastFrequency();

    enum POWER {
        DB_PLUS_4,
        DB_0,
        DB_MINUS_6,
        DB_MINUS_23,

    }

    boolean setTransmitPower(POWER power);

    boolean getTransmitPower();


    int DEVICE_NAME_LEN = 15;
    int PASSWORD_LEN = 6;

    boolean setDeviceName(String name);

    boolean getDeviceName();

    int PWD_RIGHT_EVENT = 0;
    int PWD_ERROR_EVENT = 1;
    int PWD_UPDATED_EVENT = 2;
    int PWD_CANCEL_EVENT = 3;

    boolean submitPassword(String password);

    boolean changePassword(String oldPwd, String newPwd);

    boolean cancelPassword(String password);

}
