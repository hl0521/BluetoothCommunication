package com.uteacher.www.uteacherble.uProtocol;

import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Connection.uProtocolConnectionInterface;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketCRCFailure;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketInvalidError;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketLengthError;
import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;

/**
 * Created by cartman on 15/5/25.
 */
public interface uProtocolStackInterface {

    enum CONTROL {
        DOWN,
        UP,
    }

    enum OPERATION {
        CONNECT,
        DISCONNECT,
        KEEPALIVE,
        DEVICE_INFO,
        DEVICE_ENABLE,
        LOVE_EGG_SETTING,
        BASE_SETTING,
        SOC_INQUIRE,
        STATE_INQUIRE,
        ACTION_INQUIRE,
    }

    enum ERROR {
        ERROR_OK,
        ERROR_ERR,
    }

    int REQUEST = 0;
    int RESPONSE = 1;

    int HIGH_PRIORITY = 0;
    int LOW_PRIORITY = 1;

    int getProtocolVersion();

    boolean isVersionSupported(int version);

    byte getOperationCode(OPERATION operation);

    OPERATION getOperation(byte operation);

    byte getControlCode(CONTROL type);

    CONTROL getControl(byte type);

    byte getErrorCode(ERROR error);

    ERROR getError(byte error);

    uAbstractProtocolPacket newPacket();

    uAbstractProtocolConnection newConnection(uAbstractDeviceAdapter adapter, uProtocolConnectionInterface.ProtocolCallback callback);

    uAbstractProtocolConnection getConnection(uAbstractDeviceAdapter adapter);

    void destroyConnection(uAbstractDeviceAdapter adapter);

    byte[] parsePacket(uAbstractProtocolPacket packet) throws uPacketLengthError, uPacketInvalidError;

    uAbstractProtocolPacket parseByte(byte[] data) throws uPacketLengthError, uPacketCRCFailure, uPacketInvalidError;
}
