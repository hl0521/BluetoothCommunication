package com.uteacher.www.uteacherble.uProtocol.Packet;

import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;

/**
 * Created by cartman on 15/5/27.
 */
public class uProtocolPacket extends uAbstractProtocolPacket {

    private uAbstractProtocolStack mProtocol;
    private byte mOperation;
    private byte mType;
    private byte[] mData;
    private byte mError;
    private byte mSequence;
    private int mVersion;
    private int mPriority;
    private int mPacketType;

    public uProtocolPacket(uAbstractProtocolStack protocol) {
        mProtocol = protocol;
    }

    @Override
    public void setOperation(byte operation) {
        mOperation = operation;
    }

    @Override
    public byte getOperation() {
        return mOperation;
    }

    @Override
    public void setType(byte type) {
        mType = type;
    }

    @Override
    public byte getType() {
        return mType;
    }

    @Override
    public void setData(byte[] data) {
        mData = data;
    }

    @Override
    public byte[] getData() {
        return mData;
    }

    @Override
    public void setError(byte error) {
        mError = error;
    }

    @Override
    public byte getError() {
        return mError;
    }

    @Override
    public void setSequence(byte sequence) {
        mSequence = sequence;
    }

    @Override
    public byte getSequence() {
        return mSequence;
    }

    @Override
    public void setVersion(int version) {
        mVersion = version;
    }

    @Override
    public int getVersion() {
        return mVersion;
    }

    @Override
    public void setPriority(int priority) {
        mPriority = priority;
    }

    @Override
    public int getPriority() {
        return mPriority;
    }

    @Override
    public void setPacketType(int type) {
        mPacketType = type;
    }

    @Override
    public int getPacketType() {
        return mPacketType;
    }

    @Override
    public uAbstractProtocolStack getProtocol() {
        return mProtocol;
    }
}
