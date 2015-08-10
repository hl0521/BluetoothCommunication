package com.uteacher.www.uteacherble.uDeviceAdapter;

import android.util.Log;

import com.uteacher.www.uteacherble.TestUtil.StringUtil;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;

/**
 * Created by cartman on 15/5/25.
 */
public abstract class uAbstractDeviceAdapter implements uDeviceAdapterInterface {

    private final static String TAG = uAbstractDeviceAdapter.class.getSimpleName();

    private uAbstractProtocolConnection mConnection = null;

    public uAbstractProtocolConnection getConnection() {
        return mConnection;
    }

    public void bindConnection(uAbstractProtocolConnection connection) {
        mConnection = connection;
    }

    protected abstract boolean writeToDevice(byte[] data);

    @Override
    public boolean send(byte[] data) {
        Log.v(TAG, "send data: " + StringUtil.byte2String(data));
        return writeToDevice(data);
    }

    @Override
    public boolean receive(byte[] data) {
        if (getConnection() != null) {
            Log.v(TAG, "receive data: " + StringUtil.byte2String(data));
            return getConnection().getQueue().receive(data);
        }
        return false;
    }
}
