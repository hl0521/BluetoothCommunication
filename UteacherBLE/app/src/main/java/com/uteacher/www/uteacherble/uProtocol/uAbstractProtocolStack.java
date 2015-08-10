package com.uteacher.www.uteacherble.uProtocol;

import android.util.Log;

import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Connection.uProtocolConnectionInterface;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketCRCFailure;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketInvalidError;
import com.uteacher.www.uteacherble.uProtocol.Exception.uPacketLengthError;
import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by cartman on 15/5/26.
 */
public abstract class uAbstractProtocolStack implements uProtocolStackInterface {

    private final static String TAG = uAbstractProtocolStack.class.getSimpleName();

    private HashMap<uAbstractDeviceAdapter, uAbstractProtocolConnection> mConnections = new HashMap<>();

    protected abstract uAbstractProtocolConnection createConnection(uAbstractDeviceAdapter adapter, uProtocolConnectionInterface.ProtocolCallback callback);

    @Override
    public uAbstractProtocolConnection newConnection(uAbstractDeviceAdapter adapter, uProtocolConnectionInterface.ProtocolCallback callback) {
        uAbstractProtocolConnection conn = mConnections.get(adapter);
        if (conn != null) {
            return conn;
        }

        conn = createConnection(adapter, callback);
        if (conn != null) {
            adapter.bindConnection(conn);
            mConnections.put(adapter, conn);
            return conn;
        }
        return null;
    }

    @Override
    public uAbstractProtocolConnection getConnection(uAbstractDeviceAdapter adapter) {
        return mConnections.get(adapter);
    }

    protected abstract void destroyConn(uAbstractProtocolConnection conn);

    @Override
    public void destroyConnection(uAbstractDeviceAdapter adapter) {
        uAbstractProtocolConnection conn = mConnections.get(adapter);
        if (conn != null) {
            mConnections.remove(adapter);
            //FIXME: connection should be detached from adapter, and throw exception for successive operation.
            destroyConn(conn);
        }
    }

    protected abstract byte parseControl(int version, int priority, int type) throws uPacketInvalidError;

    @Override
    public byte[] parsePacket(uAbstractProtocolPacket packet) throws uPacketLengthError, uPacketInvalidError {
        int length = 4; // 1 byte seq + 1 byte control + 1 byte operation + 1 byte type.
        if (packet.getData() != null) {
            length += packet.getData().length;
        }

        if (length > getMaxPacketLength()) {
            throw new uPacketLengthError();
        }

        byte[] data = new byte[length + 2];
        data[0] = (byte) length;
        data[1] = packet.getSequence();
        data[2] = parseControl(packet.getVersion(), packet.getPriority(), packet.getPacketType());
        data[3] = packet.getOperation();
        if (packet.getPacketType() == REQUEST) {
            data[4] = packet.getType();
        } else if (packet.getPacketType() == RESPONSE) {
            data[4] = packet.getError();
        } else {
            throw new uPacketInvalidError();
        }

        for (int i = 5; i < data.length - 1; i++) {
            data[i] = packet.getData()[i - 5];
        }
        data[data.length - 1] = CRC(data, 0, data.length-1);

        return data;
    }

    protected abstract int getMinPacketLength();

    protected abstract int getMaxPacketLength();

    protected abstract byte CRC(byte[] data);

    protected abstract byte CRC(byte[] data, int begin, int end);

    protected abstract int parseVersion(byte control) throws uPacketInvalidError;

    protected abstract int parsePriority(byte control) throws uPacketInvalidError;

    protected abstract int parsePacketType(byte control) throws uPacketInvalidError;

    @Override
    public uAbstractProtocolPacket parseByte(byte[] data) throws uPacketLengthError, uPacketCRCFailure, uPacketInvalidError {

        if (data.length < getMinPacketLength() || data.length > getMaxPacketLength()) {
            throw new uPacketLengthError();
        }

        if (data[0] != data.length - 2) { //data[0] is 1 byte length, exclude 1st byte and crc
            throw new uPacketLengthError();
        }

        if (CRC(data) != 0) {
            throw new uPacketCRCFailure();
        }

        uAbstractProtocolPacket packet = newPacket();
        packet.setSequence(data[1]);
        packet.setVersion(parseVersion(data[2]));
        packet.setPriority(parsePriority(data[2]));
        packet.setPacketType(parsePacketType(data[2]));

        packet.setOperation(data[3]);
        if (packet.getPacketType() == REQUEST) {
            packet.setType(data[4]);
        } else if (packet.getPacketType() == RESPONSE) {
            packet.setError(data[4]);
        } else {
            throw new uPacketInvalidError();
        }

        packet.setData(Arrays.copyOfRange(data, 5, data.length - 1));

        return packet;
    }



    public abstract String parseDeviceInfo(byte[] data) throws uPacketInvalidError;

    public abstract long parseKeepAlive(byte[] data) throws uPacketInvalidError;



}
