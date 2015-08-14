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


    private boolean sendApplicationData(byte operation, byte control, byte[] data) {
        uAbstractProtocolPacket packet = getProtocol().newPacket();
        packet.setOperation(operation);
        packet.setControl(control);
        packet.setData(data);
        packet.setPriority(getProtocol().HIGH_PRIORITY);

        Log.v(TAG, "sendApplicationData " + getProtocol().getControl(control).toString()
                + "," + getProtocol().getOperation(operation).toString() + "," + new String(data));
        return getQueue().send(packet);
    }


    private boolean sendApplicationData(byte operation, byte control) {
        uAbstractProtocolPacket packet = getProtocol().newPacket();
        packet.setOperation(operation);
        packet.setControl(control);
        packet.setPriority(getProtocol().HIGH_PRIORITY);

        Log.v(TAG, "sendApplicationData " + getProtocol().getControl(control).toString()
                + "," + getProtocol().getOperation(operation).toString());
        return getQueue().send(packet);
    }

    private byte getOperationCode(uProtocolStackInterface.OPERATION operation) {
        return getProtocol().getOperationCode(operation);
    }

    private byte getControlCode(uProtocolStackInterface.CONTROL control) {
        return getProtocol().getControlCode(control);
    }


    @Override
    public boolean getDeviceInformation() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.DEVICE_INFO);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "getDeviceInformation " + operation + "," + control);
        return sendApplicationData(operation, control);
    }

    @Override
    public boolean startConnection() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.CONNECT);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "startConnection " + control + "," + operation);
        return sendApplicationData(operation, control);
    }

    @Override
    public boolean stopConnection() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.DISCONNECT);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "stopConnection " + operation + "," + control);
        return sendApplicationData(operation, control);
    }

    @Override
    public boolean sendKeepAlive() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.KEEPALIVE);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "sendKeepAlive " + operation + "," + control);
        return sendApplicationData(operation, control);
    }

    @Override
    public boolean deviceEnable(byte[] data) {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.DEVICE_ENABLE);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "setDeviceEnable " + operation + ", " + control + ", " + StringUtil.byte2String(data));
        return sendApplicationData(operation, control, data);
    }

    @Override
    public boolean loveEggSetting(byte[] data) {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.LOVE_EGG_SETTING);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "setDeviceEnable " + operation + ", " + control + ", " + StringUtil.byte2String(data));
        return sendApplicationData(operation, control, data);
    }

    @Override
    public boolean baseSetting(byte[] data) {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.BASE_SETTING);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "setDeviceEnable " + operation + ", " + control + ", " + StringUtil.byte2String(data));
        return sendApplicationData(operation, control, data);
    }

    @Override
    public boolean getDeviceSoc() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.SOC_INQUIRE);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "getDeviceSOC " + operation + "," + control);
        return sendApplicationData(operation, control);
    }

    @Override
    public boolean getDeviceStatus() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.STATE_INQUIRE);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "getDeviceErrorStatistics " + operation + "," + control);
        return sendApplicationData(operation, control);
    }

    @Override
    public boolean getDeviceAction() {
        byte operation = getOperationCode(uProtocolStackInterface.OPERATION.ACTION_INQUIRE);
        byte control = getControlCode(uProtocolStackInterface.CONTROL.DOWN);

        Log.v(TAG, "getDeviceParameter " + operation + "," + control);
        return sendApplicationData(operation, control);
    }

    private uProtocolStackInterface.CONTROL getControl(byte control) {
        return getProtocol().getControl(control);
    }

    private uProtocolStackInterface.OPERATION getOperation(byte operation) {
        return getProtocol().getOperation(operation);
    }

    private uProtocolStackInterface.ERROR getError(byte error) {
        return getProtocol().getError(error);
    }

    public void onReceive(uAbstractProtocolPacket packet, uAbstractProtocolPacket unackPacket) {
        dogKicker.kickDog(true); // Any valid packet received.

        receiveAndDisPatch(packet, unackPacket);
    }


    public void receiveAndDisPatch(uAbstractProtocolPacket packet, uAbstractProtocolPacket unackPacket) {
        uProtocolStackInterface.OPERATION operation = getOperation(packet.getOperation());
        uProtocolStackInterface.CONTROL control = getControl(packet.getControl());

        Log.v(TAG, "receiveAndDisPatch unackPacket " + unackPacket.toString());
        Log.v(TAG, "receiveAndDisPatch packet " + packet.toString());

        if (control != uProtocolStackInterface.CONTROL.UP) {
            onErrorReceived(packet, unackPacket, control);
            return;
        }

        if (operation == uProtocolStackInterface.OPERATION.CONNECT) {
            getProtocolCallback().onConnected(this, packet.getData());
        } else if (operation == uProtocolStackInterface.OPERATION.DISCONNECT) {
            getProtocolCallback().onDisConnected(this, packet.getData());
        } else if (operation == uProtocolStackInterface.OPERATION.KEEPALIVE) {
            onKeepAliveRecevied(packet);
        } else if (operation == uProtocolStackInterface.OPERATION.DEVICE_INFO) {
            try {
                getProtocolCallback().onGetDeviceInformation(this, getProtocol().parseDeviceInfo(packet.getData()));
            } catch (uPacketInvalidError invalidError) {
                invalidError.printStackTrace();
            }
        } else if (operation == uProtocolStackInterface.OPERATION.DEVICE_ENABLE) {
            getProtocolCallback().onDeviceEnable(this, packet.getData());
        } else if (operation == uProtocolStackInterface.OPERATION.LOVE_EGG_SETTING) {
            getProtocolCallback().onLoveEggSetting(this, packet.getData());
        } else if (operation == uProtocolStackInterface.OPERATION.BASE_SETTING) {
            getProtocolCallback().onBaseSetting(this, packet.getData());
        } else if (operation == uProtocolStackInterface.OPERATION.SOC_INQUIRE) {
            getProtocolCallback().onGetDeviceSoc(this, packet.getData());
        } else if (operation == uProtocolStackInterface.OPERATION.STATE_INQUIRE) {
            getProtocolCallback().onGetDeviceStatus(this, packet.getData());
        } else if (operation == uProtocolStackInterface.OPERATION.ACTION_INQUIRE) {
            getProtocolCallback().onGetDeviceAction(this, packet.getData());
        } else {
            Log.d(TAG, "Unexpected operation: " + packet.getOperation());
        }
    }

    protected abstract void onPreferKeepAliveTimer(long prefer);

    private void onKeepAliveRecevied(uAbstractProtocolPacket packet) {

    }


    private void onErrorReceived(uAbstractProtocolPacket packet, uAbstractProtocolPacket unackPacket, uProtocolStackInterface.CONTROL control) {

        Log.v(TAG, "onErrorReceived " + control.toString());
    }

    public void onTimeout(uAbstractProtocolPacket packet) {
        uProtocolStackInterface.OPERATION operation = getOperation(packet.getOperation());
        uProtocolStackInterface.CONTROL control = getControl(packet.getControl());

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

        sendKeepAlive(); // million seconds/1000
    }


    public abstract long getKeepAliveTimer();

    public abstract void setKeepAliveTimer(long timer);

    public abstract void setKeepAliveTimeout(int time);

    public abstract int getKeepAliveTimeout();
}
