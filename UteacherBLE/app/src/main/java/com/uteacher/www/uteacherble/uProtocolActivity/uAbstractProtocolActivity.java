package com.uteacher.www.uteacherble.uProtocolActivity;

import android.app.Activity;
import android.os.Bundle;

import com.uteacher.www.uteacherble.uDeviceAdapter.uAbstractDeviceAdapter;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Connection.uProtocolConnectionInterface;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;

/**
 * Created by cartman on 15/5/29.
 */
public abstract class uAbstractProtocolActivity extends Activity implements uProtocolConnectionInterface.ProtocolCallback {

    private uAbstractProtocolStack mProtocolStack;

    protected abstract uAbstractProtocolStack getProtocolStack();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProtocolStack = getProtocolStack();
    }

    protected uAbstractProtocolConnection newConnection(uAbstractDeviceAdapter adapter) {
        return mProtocolStack.newConnection(adapter, this);
    }

    protected uAbstractProtocolConnection getConnection(uAbstractDeviceAdapter adapter) {
        return mProtocolStack.getConnection(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
