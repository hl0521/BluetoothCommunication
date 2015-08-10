package com.uteacher.www.uteacherble.uProtocol.Connection;

import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;

/**
 * Created by cartman on 15/5/27.
 */
public interface uProtocolConnectionInterface {

    public interface ProtocolCallback {
        public void onConnected(uAbstractProtocolConnection connection);

        public void onDisConnected(uAbstractProtocolConnection connection);

        public void onKeepAliveTimeout(uAbstractProtocolConnection connection);

        public void onGetKeepAliveAck(uAbstractProtocolConnection connection, long peer);

        public void onPaused(uAbstractProtocolConnection connection);

        public void onGetDeviceInfo(uAbstractProtocolConnection connection, String info);

        public void onGetDeviceErrorStatistics(uAbstractProtocolConnection connection, byte error, byte stats);

        public void onGetDeviceBattery(uAbstractProtocolConnection connection, byte battery);

        public void onSetDeviceParam(uAbstractProtocolConnection connection, byte param, byte[] data);

        public void onGetDeviceParam(uAbstractProtocolConnection connection, byte param, byte[] data);
    }

    public boolean startConnection();

    public boolean stopConnection();

    public boolean sendKeepAlive(byte timer);

    public boolean getDeviceInformation();

    public boolean getDeviceErrorStatistics(byte error);

    public boolean getDeviceBattery();

    public boolean getDeviceParameter(byte param);

    public boolean setDeviceParameter(byte param, byte[] data);

}
