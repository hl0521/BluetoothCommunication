package com.uteacher.www.uteacherble.uProtocol.Connection;

import com.uteacher.www.uteacherble.uProtocol.Queue.uAbstractProtocolQueue;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;

import java.util.Timer;

/**
 * Created by cartman on 15/5/27.
 */
public class uProtocolConnection extends uAbstractProtocolConnection {

    private final static String TAG = uProtocolConnection.class.getSimpleName();

    private uAbstractProtocolQueue mQueue;
    private uAbstractProtocolStack mProtocol;
    private Timer mKeepAliveTimer = null;
    private final String keepAliveTimerName = uProtocolConnection.class.getName() + ".keepAliveTimer";

    private ProtocolCallback mCallback;

    public uProtocolConnection(uAbstractProtocolStack protocol, uAbstractProtocolQueue queue, ProtocolCallback callback) {
        mProtocol = protocol;
        mQueue = queue;
        mCallback = callback;
    }

    @Override
    public uAbstractProtocolStack getProtocol() {
        return mProtocol;
    }

    @Override
    public uAbstractProtocolQueue getQueue() {
        return mQueue;
    }

    @Override
    protected ProtocolCallback getProtocolCallback() {
        return mCallback;
    }

    @Override
    protected void onPreferKeepAliveTimer(long prefer) {

    }

    @Override
    public void startKeepAlive() {
        if (mKeepAliveTimer == null) {
            mKeepAliveTimer = new Timer(keepAliveTimerName);
            startKeepAliveTimer(mKeepAliveTimer);
        }
    }

    @Override
    public void stopKeepAlive() {
        if (mKeepAliveTimer != null) {
            mKeepAliveTimer.cancel();
            mKeepAliveTimer = null;
        }
    }

    private int keepAliveTimeout = 3;
    private long keepAliveTimer = 10000;

    @Override
    public long getKeepAliveTimer() {
        return keepAliveTimer;
    }

    @Override
    public void setKeepAliveTimer(long timer) {
        keepAliveTimer= timer;
    }

    @Override
    public void setKeepAliveTimeout(int time) {
        keepAliveTimeout = time;
    }

    @Override
    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

}
