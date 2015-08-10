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

    enum TYPE {
        CTRL,
        SET,
        GET,
    }

    enum OPERATION {
        CONNECT,
        DISCONNECT,
        KEEPALIVE,
        DEVICE_INFO,
        ERROR_STATISTIC,
        BATTERY,
        PARAMETER,
    }

    enum ERROR {
        ERROR_OK,
        ERROR_DISCONNECT,
        ERROR_INVALID_OPERATION,
        ERROR_INVALID_TYPE,
        ERROR_INVALID_PARAM,
        ERROR_DEVICE_PAUSED,
    }

    int REQUEST = 0;
    int RESPONSE = 1;

    int HIGH_PRIORITY = 0;
    int LOW_PRIORITY = 1;

    int getProtocolVersion();

    boolean isVersionSupported(int version);

    byte getOperationCode(OPERATION operation);

    OPERATION getOperation(byte operation);

    byte getTypeCode(TYPE type);

    TYPE getType(byte type);

    byte getErrorCode(ERROR error);

    ERROR getError(byte error);

    uAbstractProtocolPacket newPacket();

    uAbstractProtocolConnection newConnection(uAbstractDeviceAdapter adapter, uProtocolConnectionInterface.ProtocolCallback callback);

    uAbstractProtocolConnection getConnection(uAbstractDeviceAdapter adapter);

    void destroyConnection(uAbstractDeviceAdapter adapter);

    byte[] parsePacket(uAbstractProtocolPacket packet) throws uPacketLengthError, uPacketInvalidError;

    uAbstractProtocolPacket parseByte(byte[] data) throws uPacketLengthError, uPacketCRCFailure, uPacketInvalidError;


}
