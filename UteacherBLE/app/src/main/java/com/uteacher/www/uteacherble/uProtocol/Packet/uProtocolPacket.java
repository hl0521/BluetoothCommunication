package com.uteacher.www.uteacherble.uProtocol.Packet;

import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;

/**
 * Created by cartman on 15/5/27.
 */
public class uProtocolPacket extends uAbstractProtocolPacket {

    private uAbstractProtocolStack mProtocol;
    private byte mControl;
    private byte mOperation;
    private byte[] mData;
    private int mPriority;

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
    public void setData(byte[] data) {
        mData = data;
    }

    @Override
    public byte[] getData() {
        return mData;
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
    public uAbstractProtocolStack getProtocol() {
        return mProtocol;
    }

    public byte getControl() {
        return mControl;
    }

    public void setControl(byte control) {
        mControl = control;
    }
}
