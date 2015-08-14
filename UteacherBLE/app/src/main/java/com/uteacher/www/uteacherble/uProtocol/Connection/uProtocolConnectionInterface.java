package com.uteacher.www.uteacherble.uProtocol.Connection;

/**
 * Created by cartman on 15/5/27.
 */
public interface uProtocolConnectionInterface {

    interface ProtocolCallback {

        void onGetDeviceInformation(uAbstractProtocolConnection connection, String info);

        void onConnected(uAbstractProtocolConnection connection, byte[] ack);

        void onDisConnected(uAbstractProtocolConnection connection, byte[] ack);

        void onDeviceEnable(uAbstractProtocolConnection connection, byte[] ack);

        void onLoveEggSetting(uAbstractProtocolConnection connection, byte[] ack);

        void onBaseSetting(uAbstractProtocolConnection connection, byte[] ack);

        void onGetDeviceSoc(uAbstractProtocolConnection connection, byte[] data);

        void onGetDeviceStatus(uAbstractProtocolConnection connection, byte[] data);

        void onGetDeviceAction(uAbstractProtocolConnection connection, byte[] data);

        void onKeepAliveTimeout(uAbstractProtocolConnection connection);

        void onGetKeepAliveAck(uAbstractProtocolConnection connection, long peer);

        void onPaused(uAbstractProtocolConnection connection);
    }

    boolean getDeviceInformation();

    boolean startConnection();

    boolean stopConnection();

    boolean sendKeepAlive();

    boolean deviceEnable(byte[] data);

    boolean loveEggSetting(byte[] data);

    boolean baseSetting(byte[] data);

    boolean getDeviceSoc();

    boolean getDeviceStatus();

    boolean getDeviceAction();
}
