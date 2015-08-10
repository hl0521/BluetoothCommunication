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

    protected abstract byte getCurrentSequence(int priority);

    protected abstract void incrementSequence(int priority);

    public abstract void setSendThrottleTime(long time);

    public abstract long getSendThrottleTime();

    @Override
    public boolean send(final uAbstractProtocolPacket packet) {

        Log.v(TAG, "send packet " + packet.toString());

        if (!getProtocol().isVersionSupported(packet.getVersion()) ||
                packet.getPacketType() != uProtocolStackInterface.REQUEST) {
            Log.d(TAG, "Invalid packet version " + packet.getVersion() + ",type " + packet.getPacketType());
            incrementFailure(FAILURE.INVALID_PACKET);
            return false;
        }

        Log.v(TAG, "send sequence " + getCurrentSequence(packet.getPriority()));
        packet.setSequence(getCurrentSequence(packet.getPriority()));
        incrementSequence(packet.getPriority());

        if (getUnackQueue(packet.getPriority()).offer(packet)) {
            return getSendHandler(packet.getPriority()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] data = getProtocol().parsePacket(packet);
                        Log.v(TAG, "sendHandler data " + StringUtil.byte2String(data));
                        getDeviceAdapter().send(data);
                    } catch (uPacketLengthError lengthError) {
                        lengthError.printStackTrace();
                        incrementFailure(FAILURE.INVALID_LENGTH);
                    } catch (uPacketInvalidError invalidError) {
                        invalidError.printStackTrace();
                        incrementFailure(FAILURE.INVALID_PACKET);
                    }
                }
            }, getSendThrottleTime());
        } else {
            Log.d(TAG, "queue failure");
            incrementFailure(FAILURE.QUEUE_FAILURE);
        }
        return false;
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

                    if (!getProtocol().isVersionSupported(packet.getVersion()) ||
                            packet.getPacketType() != uProtocolStackInterface.RESPONSE) {
                        Log.d(TAG, "invalid packet version " + packet.getVersion() + ",type " + packet.getPacketType());
                        incrementFailure(FAILURE.INVALID_PACKET);
                        return;
                    }

                    uAbstractProtocolPacket unackPacket = getUnackQueue(packet.getPriority()).peek();
                    if (unackPacket == null || compareSequence(packet.getSequence(), unackPacket.getSequence()) < 0) {
                        Log.d(TAG, "unexpected sequence " + packet.getSequence() + "," + unackPacket.getSequence());
                        incrementFailure(FAILURE.UNEXPECTED_SEQUENCE);
                        return;
                    }

                    uAbstractProtocolConnection conn = getConnection();
                    unackPacket = getUnackQueue(packet.getPriority()).poll();
                    while (unackPacket != null && compareSequence(packet.getSequence(), unackPacket.getSequence()) > 0) {
                        Log.v(TAG, "Sequence packet " + packet.getSequence() + ",unack " + unackPacket.getSequence());
                        conn.onUnack(unackPacket);
                        unackPacket = getUnackQueue(packet.getPriority()).poll();
                    }

                    if (unackPacket == null || compareSequence(packet.getSequence(), unackPacket.getSequence()) != 0) {
                        Log.d(TAG, "unexpected sequence " + packet.getSequence() + "," + unackPacket.getSequence());
                        incrementFailure(FAILURE.UNEXPECTED_SEQUENCE);
                        return;
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
