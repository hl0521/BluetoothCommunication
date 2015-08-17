package com.uteacher.www.uteacherble.uProtocol.Queue;

import android.os.Handler;
import android.util.Log;

import com.uteacher.www.uteacherble.TestUtil.StringUtil;
import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketCRCFailure;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketInvalidError;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketLengthError;
import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;
import com.uteacher.www.uteacherble.uProtocol.uProtocolStackInterface;

import java.util.Iterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cartman on 15/5/27.
 */
public abstract class uAbstractProtocolQueue implements uProtocolQueueInterface {

    private final static String TAG = uAbstractProtocolQueue.class.getSimpleName();

    public abstract uAbstractDeviceAdapter getDeviceAdapter();

    public abstract uAbstractProtocolStack getProtocol();

    protected abstract uAbstractProtocolConnection getConnection();

    protected abstract Handler getSendHandler(int priority);

    protected abstract Handler getReceiveHandler(int priority);

    protected abstract Queue<uAbstractProtocolPacket> getUnackQueue(int priority);

    protected abstract Queue<uAbstractProtocolPacket> getForSendQueue();

    protected abstract int getCurrentSequence(int priority);

    protected abstract void incrementSequence(int priority);

    public abstract void setSendThrottleTime(long time);

    public abstract long getSendThrottleTime();

    @Override
    public boolean send(final uAbstractProtocolPacket packet) {

        Log.v(TAG, "send packet " + packet.toString());

        if (packet.getControl() != getProtocol().getControlCode(uProtocolStackInterface.CONTROL.DOWN)) {
            Log.d(TAG, "Invalid packet control byte " + packet.getControl());
            incrementFailure(FAILURE.INVALID_PACKET);
            return false;
        }

        Log.v(TAG, "send sequence " + getCurrentSequence(packet.getPriority()));
        incrementSequence(packet.getPriority());

        if (getForSendQueue().offer(packet)) {
            Log.d(TAG, "data into sequence: @control " + packet.getControl() + " / @operation " + packet.getOperation());
            return true;
        } else {
            Log.d(TAG, "queue failure");
            incrementFailure(FAILURE.QUEUE_FAILURE);
            return false;
        }
    }

    @Override
    public void sendData() {
        if (getForSendQueue().peek() != null) {
            final uAbstractProtocolPacket packet = getForSendQueue().poll();
            getSendHandler(packet.getPriority()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] data = getProtocol().parsePacket(packet);
                        Log.v(TAG, "sendHandler data " + StringUtil.byte2String(data));
                        if (getDeviceAdapter().send(data)) {
                            getUnackQueue(1).offer(packet);
                        }
                    } catch (uPacketLengthError lengthError) {
                        lengthError.printStackTrace();
                        incrementFailure(FAILURE.INVALID_LENGTH);
                    } catch (uPacketInvalidError invalidError) {
                        invalidError.printStackTrace();
                        incrementFailure(FAILURE.INVALID_PACKET);
                    }
                }
            });
        }
    }

    protected abstract int compareSequence(byte seq1, byte seq2);

    protected abstract void incrementFailure(FAILURE failure);

    @Override
    public boolean receive(final byte[] data) {
        Log.v(TAG, "receive data " + StringUtil.byte2String(data));

        try {
            final uAbstractProtocolPacket packet = getProtocol().parseByte(data);

            return getReceiveHandler(packet.getPriority()).post(new Runnable() {
                @Override
                public void run() {

                    Log.v(TAG, "receiveHandler packet " + packet.toString());

                    if (packet.getControl() != getProtocol().getControlCode(uProtocolStackInterface.CONTROL.UP)) {
                        Log.d(TAG, "invalid pack control byte " + packet.getControl());
                        incrementFailure(FAILURE.INVALID_PACKET);
                        return;
                    }

                    uAbstractProtocolPacket unackPacket = getUnackQueue(packet.getPriority()).peek();
                    if (unackPacket == null) {
                        Log.d(TAG, "unexpected sequence");
                        incrementFailure(FAILURE.UNEXPECTED_SEQUENCE);
                        return;
                    }

                    uAbstractProtocolConnection conn = getConnection();
                    unackPacket = getUnackQueue(packet.getPriority()).poll();
                    Queue<uAbstractProtocolPacket> tempQueue = new LinkedBlockingQueue<>();
                    while ((unackPacket != null) && (unackPacket.getOperation() != packet.getOperation())) {
//                        conn.onUnack(unackPacket);
                        tempQueue.offer(unackPacket);
                        unackPacket = getUnackQueue(packet.getPriority()).poll();
                    }

                    if (unackPacket == null) {
                        Log.d(TAG, "unexpected sequence " + packet.toString());
                        incrementFailure(FAILURE.UNEXPECTED_SEQUENCE);

                        while (tempQueue.peek() != null) {
                            getUnackQueue(1).offer(tempQueue.poll());
                        }
                        return;
                    }

                    while (tempQueue.peek() != null) {
                        getForSendQueue().offer(tempQueue.poll());
                    }

                    conn.onReceive(packet, unackPacket);
                }
            });
        } catch (uPacketLengthError lengthError) {
            lengthError.printStackTrace();
            incrementFailure(FAILURE.INVALID_LENGTH);
        } catch (uPacketCRCFailure crcFailure) {
            crcFailure.printStackTrace();
            incrementFailure(FAILURE.CRC_FAILURE);
        } catch (uPacketInvalidError invalidError) {
            invalidError.printStackTrace();
            incrementFailure(FAILURE.INVALID_PACKET);
        }

        return false;
    }

    protected void startAgingTimer(Timer timer, final int priority) {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleAgingTimerExpire(priority);
            }
        }, getAgingTimer(), getAgingTimer());
    }

    protected void handleAgingTimerExpire(int priority) {
        uAbstractProtocolConnection conn = getConnection();

        Iterator<uAbstractProtocolPacket> iter = getUnackQueue(priority).iterator();
        while (iter.hasNext()) {
            uAbstractProtocolPacket packet = iter.next();
            if (packet.timeTick() > getAgingTimeout()) {
                Log.v(TAG, "handleAgingTimerExpire packet");
                iter.remove();
                if (conn != null) {
                    conn.onTimeout(packet);
                }
            }
        }

    }


    public abstract void setAgingTimeout(int time);

    public abstract int getAgingTimeout();

    public abstract long getAgingTimer();

    public abstract void setAgingTimer(long timer);
}
