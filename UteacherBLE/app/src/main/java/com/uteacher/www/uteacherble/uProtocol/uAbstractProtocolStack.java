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

    @Override
    public byte[] parsePacket(uAbstractProtocolPacket packet) throws uPacketLengthError, uPacketInvalidError {
        int length = 4; // 1byte length + 1 byte control + 1 byte operation + 1 byte CRC.
        if (packet.getData() != null) {
            length += packet.getData().length;
        }

        if (length > getMaxPacketLength()) {
            throw new uPacketLengthError();
        }

        byte[] data = new byte[length];
        data[0] = (byte) length;
        data[1] = packet.getControl();
        data[2] = packet.getOperation();

        for (int i = 3; i < data.length - 1; i++) {
            data[i] = packet.getData()[i - 3];
        }
        data[data.length - 1] = CRC(data, 0, data.length - 1);

        return data;
    }

    protected abstract int getMinPacketLength();

    protected abstract int getMaxPacketLength();

    protected abstract byte CRC(byte[] data);

    protected abstract byte CRC(byte[] data, int begin, int end);

    @Override
    public uAbstractProtocolPacket parseByte(byte[] data) throws uPacketLengthError, uPacketCRCFailure, uPacketInvalidError {

        if (data.length < getMinPacketLength() || data.length > getMaxPacketLength()) {
            throw new uPacketLengthError();
        }

        if (data[0] != data.length) { // data[0] is 1 byte length
            throw new uPacketLengthError();
        }

        if (CRC(data) != 0) {
            throw new uPacketCRCFailure();
        }

        uAbstractProtocolPacket packet = newPacket();
        packet.setControl(data[1]);
        packet.setOperation(data[2]);
        packet.setData(Arrays.copyOfRange(data, 3, data.length - 1));

        return packet;
    }


    public abstract String parseDeviceInfo(byte[] data) throws uPacketInvalidError;

    public abstract long parseKeepAlive(byte[] data) throws uPacketInvalidError;


}
