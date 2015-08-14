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
    private static HashMap<CONTROL, Byte> mControlCode = new HashMap<>();
    private static HashMap<ERROR, Byte> mErrorCode = new HashMap<>();

    static {

        mOperationCode.put(OPERATION.CONNECT, (byte) 0xc0);
        mOperationCode.put(OPERATION.DISCONNECT, (byte) 0xc1);
        mOperationCode.put(OPERATION.KEEPALIVE, (byte) 0xc2);
        mOperationCode.put(OPERATION.DEVICE_INFO, (byte) 0xe0);
        mOperationCode.put(OPERATION.DEVICE_ENABLE, (byte) 0xa0);
        mOperationCode.put(OPERATION.LOVE_EGG_SETTING, (byte) 0xa1);
        mOperationCode.put(OPERATION.BASE_SETTING, (byte) 0xa2);
        mOperationCode.put(OPERATION.SOC_INQUIRE, (byte) 0xd0);
        mOperationCode.put(OPERATION.STATE_INQUIRE, (byte) 0xd1);
        mOperationCode.put(OPERATION.ACTION_INQUIRE, (byte) 0xd2);

        mControlCode.put(CONTROL.DOWN, (byte) 0x40);
        mControlCode.put(CONTROL.UP, (byte) 0x50);

        mErrorCode.put(ERROR.ERROR_OK, (byte) 0x00);
        mErrorCode.put(ERROR.ERROR_ERR, (byte) 0xFF);
    }

    private static HashMap<Byte, OPERATION> mOperationEnum = new HashMap<>();
    private static HashMap<Byte, CONTROL> mControlEnum = new HashMap<>();
    private static HashMap<Byte, ERROR> mErrorEnum = new HashMap<>();

    static {
        for (OPERATION op : mOperationCode.keySet()) {
            mOperationEnum.put(mOperationCode.get(op), op);
        }

        for (CONTROL co : mControlCode.keySet()) {
            mControlEnum.put(mControlCode.get(co), co);
        }

        for (ERROR err : mErrorCode.keySet()) {
            mErrorEnum.put(mErrorCode.get(err), err);
        }
    }

    public final static int VERSION = 10;  // software version: 1.0

    private final static int DEVICE_INFO_LENGTH = 10;
    private final static int MIN_PACKET_LENGTH = 4;
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
    public byte getControlCode(CONTROL control) {
        return mControlCode.get(control);
    }

    @Override
    public CONTROL getControl(byte control) {
        return mControlEnum.get(control);
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
