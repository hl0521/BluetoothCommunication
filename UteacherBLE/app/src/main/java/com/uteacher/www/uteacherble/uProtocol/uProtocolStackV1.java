package com.uteacher.www.uteacherble.uProtocol;

import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uProtocol.CRC.CRCFactory;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Connection.uProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Connection.uProtocolConnectionInterface;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketInvalidError;
import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;
import com.uteacher.www.uteacherble.uProtocol.Packet.uProtocolPacket;
import com.uteacher.www.uteacherble.uProtocol.Queue.uAbstractProtocolQueue;

import java.util.HashMap;

/**
 * Created by cartman on 15/5/26.
 */
public class uProtocolStackV1 extends uAbstractProtocolStack {


    private static HashMap<OPERATION, Byte> mOperationCode = new HashMap<>();
    private static HashMap<TYPE, Byte> mTypeCode = new HashMap<>();
    private static HashMap<ERROR, Byte> mErrorCode = new HashMap<>();

    static {

        mOperationCode.put(OPERATION.CONNECT, (byte) 0xc0);
        mOperationCode.put(OPERATION.DISCONNECT, (byte) 0xc1);
        mOperationCode.put(OPERATION.KEEPALIVE, (byte) 0xc2);
        mOperationCode.put(OPERATION.DEVICE_INFO, (byte) 0xe2);
        mOperationCode.put(OPERATION.ERROR_STATISTIC, (byte) 0xe3);
        mOperationCode.put(OPERATION.BATTERY, (byte) 0xe4);
        mOperationCode.put(OPERATION.PARAMETER, (byte) 0xd0);

        mTypeCode.put(TYPE.CTRL, (byte) 0x0);
        mTypeCode.put(TYPE.GET, (byte) 0x1);
        mTypeCode.put(TYPE.SET, (byte) 0x2);

        mErrorCode.put(ERROR.ERROR_OK, (byte) 0x0);
        mErrorCode.put(ERROR.ERROR_DISCONNECT, (byte) 0x01);
        mErrorCode.put(ERROR.ERROR_INVALID_OPERATION, (byte) 0x02);
        mErrorCode.put(ERROR.ERROR_INVALID_TYPE, (byte) 0x03);
        mErrorCode.put(ERROR.ERROR_INVALID_PARAM, (byte) 0x04);
        mErrorCode.put(ERROR.ERROR_DEVICE_PAUSED, (byte) 0x05);

    }

    private static HashMap<Byte, OPERATION> mOperationEnum = new HashMap<>();
    private static HashMap<Byte, TYPE> mTypeEnum = new HashMap<>();
    private static HashMap<Byte, ERROR> mErrorEnum = new HashMap<>();

    static {
        for (OPERATION op : mOperationCode.keySet()) {
            mOperationEnum.put(mOperationCode.get(op), op);
        }

        for (TYPE tp : mTypeCode.keySet()) {
            mTypeEnum.put(mTypeCode.get(tp), tp);
        }

        for (ERROR err : mErrorCode.keySet()) {
            mErrorEnum.put(mErrorCode.get(err), err);
        }
    }

    public final static int VERSION = 1;

    private final static int DEVICE_INFO_LENGTH = 13;
    private final static int MIN_PACKET_LENGTH = 6;
    private final static int MAX_PACKET_LENGTH = 20;


    public uProtocolStackV1() {

    }

    @Override
    public boolean isVersionSupported(int version) {
        return version == VERSION;
    }

    @Override
    public int getProtocolVersion() {
        return VERSION;
    }

    @Override
    public byte getOperationCode(OPERATION operation) {
        return mOperationCode.get(operation);
    }

    @Override
    public OPERATION getOperation(byte operation) {
        return mOperationEnum.get(operation);
    }

    @Override
    public byte getTypeCode(TYPE type) {
        return mTypeCode.get(type);
    }

    @Override
    public TYPE getType(byte type) {
        return mTypeEnum.get(type);
    }

    @Override
    public byte getErrorCode(ERROR error) {
        return mErrorCode.get(error);
    }

    @Override
    public ERROR getError(byte error) {
        return mErrorEnum.get(error);
    }

    private uAbstractProtocolQueue createQueue(uAbstractDeviceAdapter adapter) {
        return new uProtocolSimpleQueue(this, adapter);
    }

    @Override
    protected uAbstractProtocolConnection createConnection(uAbstractDeviceAdapter adapter, uProtocolConnectionInterface.ProtocolCallback callback) {
        return new uProtocolConnection(this, createQueue(adapter), callback);
    }

    @Override
    protected void destroyConn(uAbstractProtocolConnection conn) {
    }

    @Override
    protected byte parseControl(int version, int priority, int type) throws uPacketInvalidError {
        if (version != getProtocolVersion()) {
            throw new uPacketInvalidError();
        }

        if (priority != HIGH_PRIORITY && priority != LOW_PRIORITY) {
            throw new uPacketInvalidError();
        }

        if (type != REQUEST && type != RESPONSE) {
            throw new uPacketInvalidError();
        }

        return (byte) ((version << 5) | (priority << 4) | (type << 3));
    }

    @Override
    protected int getMinPacketLength() {
        return MIN_PACKET_LENGTH;
    }

    @Override
    protected int getMaxPacketLength() {
        return MAX_PACKET_LENGTH;
    }

    @Override
    protected byte CRC(byte[] data) {
        return CRCFactory.calculateCRC8(data);
    }

    @Override
    protected byte CRC(byte[] data, int begin, int end) {
        return CRCFactory.calculateCRC8(data, begin, end);
    }

    @Override
    protected int parseVersion(byte control) throws uPacketInvalidError {
        int version = (control >> 5) & 0x03;
        return version;
    }

    @Override
    protected int parsePriority(byte control) throws uPacketInvalidError {
        int priority = (control >> 4) & 0x01;
        return priority;
    }

    @Override
    protected int parsePacketType(byte control) throws uPacketInvalidError {
        int type = (control >> 3) & 0x01;
        return type;
    }

    @Override
    public String parseDeviceInfo(byte[] data) throws uPacketInvalidError {
        if (data.length != DEVICE_INFO_LENGTH) {
            throw new uPacketInvalidError();
        }

        return new String(data);
    }

    @Override
    public long parseKeepAlive(byte[] data) throws uPacketInvalidError {
        if (data.length > 1) {
            throw new uPacketInvalidError();
        }

        return (long) (data[0] * 1000);
    }


    @Override
    public uAbstractProtocolPacket newPacket() {
        return new uProtocolPacket(this);
    }
}
