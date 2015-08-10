package com.uteacher.www.uteacherble.uProtocol.Connection;

import android.util.Log;

import com.uteacher.www.uteacherble.TestUtil.StringUtil;
import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketInvalidError;
import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;
import com.uteacher.www.uteacherble.uProtocol.Queue.uAbstractProtocolQueue;
import com.uteacher.www.uteacherble.uProtocol.Queue.uProtocolQueueInterface;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;
import com.uteacher.www.uteacherble.uProtocol.uProtocolStackInterface;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cartman on 15/5/27.
 */
public abstract class uAbstractProtocolConnection implements uProtocolConnectionInterface {

    private static final String TAG = uAbstractProtocolConnection.class.getSimpleName();

    public abstract uAbstractProtocolStack getProtocol();

    public abstract uAbstractProtocolQueue getQueue();

    public uAbstractDeviceAdapter getAdapter() {
        return getQueue().getDeviceAdapter();
    }

    protected abstract ProtocolCallback getProtocolCallback();


    private boolean sendApplicationData(byte operation, byte type, byte[] data) {
        uAbstractProtocolPacket packet = getProtocol().newPacket();
        packet.setOperation(operation);
        packet.setType(type);
        packet.setData(data);
        packet.setVersion(getProtocol().getProtocolVersion());
        packet.setPriority(getProtocol().HIGH_PRIORITY);
        packet.setPacketType(getProtocol().REQUEST);

        Log.v(TAG, "sendApplicationData " + getProtocol().getOperation(operation).toString()
                + "," + getProtocol().getType(type).toString() + "," + new String(data));
        return getQueue().send(packet);
    }


    private boolean sendApplicationData(byte operation, byte type) {
        uAbstractProtocolPacket packet = getProtocol().newPacket();
        packet.setOperation(operation);
        packet.setType(type);
        packet.setVersion(getProtocol().getProtocolVersion());
        packet.setPriority(getProtocol().HIGH_PRIORITY);
        packet.setPacketType(getProtocol().REQUEST);

        Log.v(TAG, "sendApplicationData " + getProtocol().getOperation(operation).toString()
                + "," + getProtocol().getType(type).toString());
        return getQueue().send(packet);
    }

    private byte getOperationCode(uProtocolStackInterface.OPERATION operation) {
        return getProtocol().getOperationCode(operation);
    }

    private byte getTypeCode(uProtocolStackInterface.TYPE type) {
        return getProtocol().getTypeCode(type);
    }


    @Override
    public boolean startConnection() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.CONNECT);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.CTRL);

        Log.v(TAG, "startConnection " + operation + "," + type);
        return sendApplicationData(operation, type);
    }

    @Override
    public boolean stopConnection() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.DISCONNECT);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.CTRL);

        Log.v(TAG, "stopConnection " + operation + "," + type);
        return sendApplicationData(operation, type);
    }

    @Override
    public boolean sendKeepAlive(byte timer) {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.KEEPALIVE);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.CTRL);

        Log.v(TAG, "sendKeepAlive " + operation + "," + type + "," + timer);
        return sendApplicationData(operation, type, new byte[]{timer});
    }

    @Override
    public boolean getDeviceInformation() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.DEVICE_INFO);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.GET);

        Log.v(TAG, "getDeviceInformation " + operation + "," + type);
        return sendApplicationData(operation, type);
    }

    @Override
    public boolean getDeviceErrorStatistics(byte error) {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.ERROR_STATISTIC);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.GET);

        Log.v(TAG, "getDeviceErrorStatistics " + operation + "," + type + "," + error);
        return sendApplicationData(operation, type, new byte[]{error});
    }

    @Override
    public boolean getDeviceBattery() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.BATTERY);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.GET);

        Log.v(TAG, "getDeviceBattery " + operation + "," + type);
        return sendApplicationData(operation, type);
    }

    @Override
    public boolean getDeviceParameter(byte param) {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.PARAMETER);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.GET);

        Log.v(TAG, "getDeviceParameter " + operation + "," + type + "," + param);
        return sendApplicationData(operation, type, new byte[]{param});
    }

    @Override
    public boolean setDeviceParameter(byte param, byte[] data) {
        byte[] aData = new byte[data.length + 1];
        aData[0] = param;
        for (int i = 1; i < data.length + 1; i++) {
            aData[i] = data[i - 1];
        }

        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.PARAMETER);
        byte type = getTypeCode(uProtocolStackInterface.TYPE.SET);

        Log.v(TAG, "setDeviceParameter " + operation + "," + type + "," + StringUtil.byte2String(aData));
        return sendApplicationData(operation, type, aData);
    }

    private uProtocolStackInterface.TYPE getType(byte type) {
        return getProtocol().getType(type);
    }

    private uProtocolStackInterface.OPERATION getOperation(byte operation) {
        return getProtocol().getOperation(operation);
    }

    private uProtocolStackInterface.ERROR getError(byte error) {
        return getProtocol().getError(error);
    }

    public void onReceive(uAbstractProtocolPacket packet, uAbstractProtocolPacket unackPacket) {
        dogKicker.kickDog(true); //Any valid packet received.

        receiveAndDisPatch(packet, unackPacket);
    }


    public void receiveAndDisPatch(uAbstractProtocolPacket packet, uAbstractProtocolPacket unackPacket) {
        uProtocolStackInterface.OPERATION operation = getOperation(packet.getOperation());
        uProtocolStackInterface.TYPE type = getType(unackPacket.getType());
        uProtocolStackInterface.ERROR error = getError(packet.getError());

        Log.v(TAG, "receiveAndDisPatch packet " + packet.toString());
        Log.v(TAG, "receiveAndDisPatch unackPacket " + unackPacket.toString());

        if (error != uProtocolStackInterface.ERROR.ERROR_OK) {
            onErrorReceived(packet, unackPacket, error);
            return;
        }

        if (operation == uProtocolStackInterface.OPERATION.CONNECT) {
            getProtocolCallback().onConnected(this);
        } else if (operation == uProtocolStackInterface.OPERATION.DISCONNECT) {
            getProtocolCallback().onDisConnected(this);
        } else if (operation == uProtocolStackInterface.OPERATION.KEEPALIVE) {
            onKeepAliveRecevied(packet);
        } else if (operation == uProtocolStackInterface.OPERATION.DEVICE_INFO) {
            if (type == uProtocolStackInterface.TYPE.GET) {
                try {
                    getProtocolCallback().onGetDeviceInfo(this, getProtocol().parseDeviceInfo(packet.getData()));
                } catch (uPacketInvalidError invalidError) {
                    invalidError.printStackTrace();
                }
            }

        } else if (operation == uProtocolStackInterface.OPERATION.ERROR_STATISTIC) {
            if (type == uProtocolStackInterface.TYPE.GET) {
                // FIXME: it's protocol's decision how to parse error statistics from packet data.
                getProtocolCallback().onGetDeviceErrorStatistics(this, packet.getData()[0], packet.getData()[1]);
            }

        } else if (operation == uProtocolStackInterface.OPERATION.BATTERY) {
            if (type == uProtocolStackInterface.TYPE.GET) {
                // FIXME
                getProtocolCallback().onGetDeviceBattery(this, packet.getData()[0]);
            }

        } else if (operation == uProtocolStackInterface.OPERATION.PARAMETER) {
            if (type == uProtocolStackInterface.TYPE.SET) {
                //FIXME
                getProtocolCallback().onSetDeviceParam(this, unackPacket.getData()[0],
                        Arrays.copyOfRange(unackPacket.getData(), 1, unackPacket.getData().length));
            } else if (type == uProtocolStackInterface.TYPE.GET) {
                //FIXME
                getProtocolCallback().onGetDeviceParam(this, packet.getData()[0],
                        Arrays.copyOfRange(packet.getData(), 1, packet.getData().length));

            }

        }

    }

    protected abstract void onPreferKeepAliveTimer(long prefer);

    private void onKeepAliveRecevied(uAbstractProtocolPacket packet) {
        try {
            long prefer = getProtocol().parseKeepAlive(packet.getData());
            if (prefer != getKeepAliveTimer()) {
                onPreferKeepAliveTimer(prefer);
            }

            Log.v(TAG, "onKeepAliveRecevied " + prefer);
            getProtocolCallback().onGetKeepAliveAck(this, prefer);
        } catch (uPacketInvalidError invalidError) {
            invalidError.printStackTrace();
        }
    }


    private void onErrorReceived(uAbstractProtocolPacket packet, uAbstractProtocolPacket unackPacket, uProtocolStackInterface.ERROR error) {

        Log.v(TAG, "onErrorReceived " + error.toString());

        if (error == uProtocolStackInterface.ERROR.ERROR_DISCONNECT) {
            getProtocolCallback().onDisConnected(this); //Device indicates disconnected.
        } else if (error == uProtocolStackInterface.ERROR.ERROR_DEVICE_PAUSED) {
            getProtocolCallback().onPaused(this);
        } else if (error == uProtocolStackInterface.ERROR.ERROR_INVALID_OPERATION) {

        } else if (error == uProtocolStackInterface.ERROR.ERROR_INVALID_TYPE) {

        } else if (error == uProtocolStackInterface.ERROR.ERROR_INVALID_PARAM) {

        }

    }

    public void onTimeout(uAbstractProtocolPacket packet) {
        uProtocolStackInterface.OPERATION operation = getOperation(packet.getOperation());
        uProtocolStackInterface.TYPE type = getType(packet.getType());

        Log.v(TAG, "onTimeout packet " + packet.toString());

    }

    public void onUnack(uAbstractProtocolPacket packet) {
        Log.v(TAG, "onUnack packet " + packet.toString());
    }

    public void onLinkFailureOverThreshold(uProtocolQueueInterface.FAILURE failure, int threshold) {
        Log.v(TAG, "onLinkFailureOverThreshold " + failure.toString() + "," + threshold);
    }


    public abstract void startKeepAlive();

    public abstract void stopKeepAlive();

    protected void startKeepAliveTimer(Timer timer) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleKeepAliveTimerExpire();
            }
        }, getKeepAliveTimer(), getKeepAliveTimer());
    }

    private class DogKicker {
        private int mDog = 0;
        private boolean mKickDog = false;

        public void kickDog(boolean kick) {
            mKickDog = kick;
        }

        public boolean isDogKicked() {
            return mKickDog;
        }

        public int dogBites() {
            mDog += 1;
            return mDog;
        }

        public void biteDog() {
            mDog = 0;
        }

    }

    private DogKicker dogKicker = new DogKicker();

    private void handleKeepAliveTimerExpire() {

        if (dogKicker.isDogKicked()) {
            dogKicker.biteDog();
            dogKicker.kickDog(false);
            return;
        }

        if (dogKicker.dogBites() > getKeepAliveTimeout()) {
            Log.v(TAG, "handleKeepAliveTimerExpire ");
            getProtocolCallback().onKeepAliveTimeout(this);
        }

        sendKeepAlive((byte) (getKeepAliveTimer() / 1000)); // million seconds/1000
    }


    public abstract long getKeepAliveTimer();

    public abstract void setKeepAliveTimer(long timer);

    public abstract void setKeepAliveTimeout(int time);

    public abstract int getKeepAliveTimeout();
}
