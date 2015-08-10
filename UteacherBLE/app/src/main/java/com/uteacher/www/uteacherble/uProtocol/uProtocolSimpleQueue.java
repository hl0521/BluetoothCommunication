package com.uteacher.www.uteacherble.uProtocol;

import android.os.HandlerThread;
import android.os.Handler;

import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;
import com.uteacher.www.uteacherble.uProtocol.Queue.uAbstractProtocolQueue;

import java.util.HashMap;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by cartman on 15/5/26.
 */
public class uProtocolSimpleQueue extends uAbstractProtocolQueue {

    private final static String TAG = uProtocolSimpleQueue.class.getSimpleName();

    private uAbstractDeviceAdapter mAdapter;
    private uAbstractProtocolStack mProtocol;

    private HandlerThread mSendThread;
    private HandlerThread mReceiveThread;

    private final String sendThreadName = uProtocolSimpleQueue.class.getName() + ".sendThread";
    private final String receiveThreadName = uProtocolSimpleQueue.class.getName() + ".receiveThread";

    private Handler mSendHandler;
    private Handler mReceiveHandler;

    private Queue<uAbstractProtocolPacket> mUnackQueue;

    private Timer mAgingTimer;
    private final String agingTimerName = uProtocolSimpleQueue.class.getName() + ".agingTimer";

    private byte mSequence;

    private HashMap<FAILURE, Integer> mFailureMap = new HashMap<>();
    private static HashMap<FAILURE, Integer> mFailueThreshold = new HashMap<>();

    static {
        mFailueThreshold.put(FAILURE.INVALID_LENGTH, 100);
        mFailueThreshold.put(FAILURE.CRC_FAILURE, 100);
        mFailueThreshold.put(FAILURE.INVALID_PACKET, 100);
        mFailueThreshold.put(FAILURE.QUEUE_FAILURE, 100);
        mFailueThreshold.put(FAILURE.UNEXPECTED_SEQUENCE, 100);
    }


    public uProtocolSimpleQueue(uAbstractProtocolStack protocol, uAbstractDeviceAdapter adapter) {
        mAdapter = adapter;
        mProtocol = protocol;
        mSequence = 0;

        mSendThread = new HandlerThread(sendThreadName);
        mSendThread.start();
        mSendHandler = new Handler(mSendThread.getLooper());

        mReceiveThread = new HandlerThread(receiveThreadName);
        mReceiveThread.start();
        mReceiveHandler = new Handler(mReceiveThread.getLooper());

        mUnackQueue = new LinkedBlockingQueue<>();

        clearFailureStatistic();

        mAgingTimer = new Timer(agingTimerName);
        startAgingTimer(mAgingTimer, uProtocolStackInterface.HIGH_PRIORITY);
    }


    @Override
    protected Handler getSendHandler(int priority) {
        return mSendHandler;
    }

    @Override
    protected Handler getReceiveHandler(int priority) {
        return mReceiveHandler;
    }

    @Override
    protected Queue getUnackQueue(int priority) {
        return mUnackQueue;
    }


    @Override
    synchronized protected byte getCurrentSequence(int priority) {
        return mSequence;
    }

    @Override
    synchronized protected void incrementSequence(int priority) {
        mSequence += 1;
    }

    private long sendThrottleTime = 100;

    @Override
    public void setSendThrottleTime(long time) {
        sendThrottleTime = time;
    }

    @Override
    public long getSendThrottleTime() {
        return sendThrottleTime;
    }

    @Override
    protected int compareSequence(byte seq1, byte seq2) {
        if (seq1 >= 0 && seq2 >= 0) {
            return seq1 - seq2;
        } else if (seq1 < 0 && seq2 < 0) {
            return seq1 - seq2;
        } else if (seq1 > seq2 ){
            return seq1 - seq2 - (byte)0x7f;
        } else {
            return seq2 - seq1 - (byte)0x7f;
        }
    }

    @Override
    synchronized protected void incrementFailure(FAILURE failure) {
        if (mFailureMap.containsKey(failure)) {
            int statistics = mFailureMap.get(failure);
            statistics += 1;
            if (statistics > mFailueThreshold.get(failure)) {
                getConnection().onLinkFailureOverThreshold(failure, mFailueThreshold.get(failure));
            }
            mFailureMap.put(failure, statistics);
        }
    }


    private int agingTimeout = 5;
    private long agingTimer = 1000;

    @Override
    public void setAgingTimeout(int time) {
        agingTimeout = time;
    }

    @Override
    public int getAgingTimeout() {
        return agingTimeout;
    }

    @Override
    public long getAgingTimer() {
        return agingTimer;
    }

    @Override
    public void setAgingTimer(long timer) {
        agingTimer = timer;
    }

    @Override
    public uAbstractDeviceAdapter getDeviceAdapter() {
        return mAdapter;
    }

    @Override
    public uAbstractProtocolStack getProtocol() {
        return mProtocol;
    }

    @Override
    protected uAbstractProtocolConnection getConnection() {
        return mProtocol.getConnection(mAdapter);
    }

    @Override
    synchronized public int getFailureStatistic(FAILURE failure) {
        return mFailureMap.get(failure);
    }

    synchronized private void clearFailureStatistic(FAILURE failure) {
        mFailureMap.put(failure, 0);
    }

    synchronized private void clearFailureStatistic() {
        for (FAILURE failure : FAILURE.values()) {
            mFailureMap.put(failure, 0);
        }
    }
}
