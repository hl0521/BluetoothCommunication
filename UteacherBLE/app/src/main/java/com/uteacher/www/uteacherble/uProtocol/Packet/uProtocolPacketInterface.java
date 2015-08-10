package com.uteacher.www.uteacherble.uProtocol.Packet;

/**
 * Created by cartman on 15/5/27.
 */
public interface uProtocolPacketInterface {

    void setOperation(byte operation);

    byte getOperation();

    void setType(byte type);

    byte getType();

    void setData(byte[] data);

    byte[] getData();

    void setError(byte error);

    byte getError();

    void setSequence(byte sequence);

    byte getSequence();

    void setVersion(int version);

    int getVersion();

    void setPriority(int priority);

    int getPriority();

    void setPacketType(int type);

    int getPacketType();

}
