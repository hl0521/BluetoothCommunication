package com.uteacher.www.uteacherble.uDeviceAdapter;

import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;

/**
 * Created by cartman on 15/5/25.
 */
public interface uDeviceAdapterInterface {

    enum STATUS {
        SUCCEED,
        FAILED,
    }

    interface adapterCallback {
        void onConnected(String address, STATUS status);
        void onDisconnected(String address, STATUS status);
    }

    boolean connect();

    void disconnect();

    boolean send(byte[] data);

     boolean receive(byte[] data);


    String getName();
    String getAddress();
}
