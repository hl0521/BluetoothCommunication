package com.uteacher.www.uteacherble.uProtocolActivity;

import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;
import com.uteacher.www.uteacherble.uProtocol.uProtocolFactory;

/**
 * Created by cartman on 15/5/29.
 */
public class uProtocolStackV1Activity extends uAbstractProtocolActivity {

    @Override
    public void onConnected(uAbstractProtocolConnection connection) {

    }

    @Override
    public void onDisConnected(uAbstractProtocolConnection connection) {

    }

    @Override
    public void onKeepAliveTimeout(uAbstractProtocolConnection connection) {

    }

    @Override
    public void onGetKeepAliveAck(uAbstractProtocolConnection connection, long peer) {

    }

    @Override
    public void onPaused(uAbstractProtocolConnection connection) {

    }

    @Override
    public void onGetDeviceInfo(uAbstractProtocolConnection connection, String info) {

    }

    @Override
    public void onGetDeviceErrorStatistics(uAbstractProtocolConnection connection, byte error, byte stats) {

    }

    @Override
    public void onGetDeviceBattery(uAbstractProtocolConnection connection, byte battery) {

    }

    @Override
    public void onSetDeviceParam(uAbstractProtocolConnection connection, byte param, byte[] data) {

    }

    @Override
    public void onGetDeviceParam(uAbstractProtocolConnection connection, byte param, byte[] data) {

    }

    @Override
    protected uAbstractProtocolStack getProtocolStack() {
        return uProtocolFactory.getProtocolInstance(uProtocolFactory.PROTO_STACK_V1);
    }
}
