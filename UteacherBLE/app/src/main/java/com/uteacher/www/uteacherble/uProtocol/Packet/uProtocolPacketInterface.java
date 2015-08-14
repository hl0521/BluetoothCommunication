package com.uteacher.www.uteacherble.uProtocol.Packet;

/**
 * Created by cartman on 15/5/27.
 */
public interface uProtocolPacketInterface {

    void setOperation(byte operation);

    byte getOperation();

    void setControl(byte control);

    byte getControl();

    void setData(byte[] data);

    byte[] getData();

    void setPriority(int priority);

    int getPriority();
}
