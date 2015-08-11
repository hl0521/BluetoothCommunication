package com.uteacher.www.uteacherble.TestActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.uteacher.www.uteacherble.R;
import com.uteacher.www.uteacherble.TestUtil.StringUtil;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceAdapter;
import com.uteacher.www.uteacherble.uDeviceAdapter.uDeviceAdapterInterface;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Connection.uProtocolConnectionInterface;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;
import com.uteacher.www.uteacherble.uProtocol.uProtocolFactory;


public class SimpleProtocolActivity extends BaseScanActivity implements uProtocolConnectionInterface.ProtocolCallback{

    private Handler mHandler;
    private TextView tvScreen;
    private uBleDeviceAdapter mAdapter;

    private uAbstractProtocolStack mProtocolStack;
    private uAbstractProtocolConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        tvScreen = (TextView) findViewById(R.id.id_screen);
        mProtocolStack = uProtocolFactory.getProtocolInstance(uProtocolFactory.PROTO_STACK_V1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_protocol, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mConnection != null) {
            mConnection.stopConnection();
        }

        if (mAdapter != null) {
            mAdapter.disconnect();
            mAdapter = null;
        }
    }

    @Override
    protected int getContentViewResID() {
        return R.layout.activity_simple_protocol;
    }

    @Override
    protected void onDeviceItemClick(BluetoothDevice device) {
        if (mAdapter != null) {
            Toast.makeText(SimpleProtocolActivity.this, " Another device is in testing. ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            mAdapter = newDeviceAdapter(device);
            Toast.makeText(SimpleProtocolActivity.this, "Start device " + device.getAddress(),
                    Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        tvScreen.setText("");
                        tvScreen.append("Connecting to device\n");
                        if (!mAdapter.connect()) {
                            tvScreen.append("Failed to connect device\n");
                        }
                    }
                }
            }, 1000);
        }
    }

    @Override
    public void onConnected(String address, uDeviceAdapterInterface.STATUS status) {
        super.onConnected(address, status);
        tvScreen.append("Device connected\n");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    tvScreen.append("Connecting protocol\n");
                    mConnection = mProtocolStack.newConnection(mAdapter, SimpleProtocolActivity.this);
                    if (!mConnection.startConnection()) {
                        tvScreen.append("Failed to connect protocol\n");
                    }
                }

            }
        }, 100);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.stopConnection();
                }

                if (mAdapter != null) {
                    mAdapter.disconnect();
                    mAdapter = null;
                    tvScreen.append("Device disconnected\n");
                }
            }
        }, 30000);

    }

    @Override
    public void onDisconnected(String address, uDeviceAdapterInterface.STATUS status) {
        super.onDisconnected(address, status);
        if (mConnection != null) {
            mProtocolStack.destroyConnection(mConnection.getAdapter());
            mConnection = null;
        }
    }

    @Override
    public void onConnected(uAbstractProtocolConnection connection) {
        if (mConnection != null) {
            tvScreen.append("Sending keep alive\n");
            if (!mConnection.sendKeepAlive((byte)(mConnection.getKeepAliveTimer()/1000))) {
                tvScreen.append("Failed to send keep alive\n");
            }

            mConnection.startKeepAlive();

            tvScreen.append("Getting device info\n");
            if (!mConnection.getDeviceInformation()) {
                tvScreen.append("Failed to get device info\n");
            }

            tvScreen.append("Getting device battery");
            if (!mConnection.getDeviceBattery()) {
                tvScreen.append("Failed to get device battery");
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mConnection != null) {
                        for (int i=0;i<4;i++) {
                            tvScreen.append("Getting device error stats: " + i + "\n");
                            if (!mConnection.getDeviceErrorStatistics((byte)i)) {
                                tvScreen.append("Failed to get device error stats\n");
                            }
                        }
                    }
                }
            }, 1000);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mConnection != null) {
                        for (int i=0;i<4;i++) {
                            tvScreen.append("Setting device param: " + i + "\n");
                            if (!mConnection.setDeviceParameter((byte)i, new byte[]{99})) {
                                tvScreen.append("Failed to set device param\n");
                            }
                        }
                    }
                }
            }, 2000);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mConnection != null) {
                        for (int i=0;i<4;i++) {
                            tvScreen.append("Getting device param: " + i + "\n");
                            if (!mConnection.getDeviceParameter((byte)i)) {
                                tvScreen.append("Failed to get device param\n");
                            }
                        }
                    }
                }
            }, 3000);
        }
    }

    @Override
    public void onDisConnected(uAbstractProtocolConnection connection) {
        if (mConnection != null ) {
            tvScreen.append("Protocol disconnected\n");
        }
    }

    @Override
    public void onKeepAliveTimeout(uAbstractProtocolConnection connection) {
        if (mConnection != null) {
            tvScreen.append("Keep alive timeout\n");
        }
    }

    @Override
    public void onGetKeepAliveAck(uAbstractProtocolConnection connection, long peer) {
        if (mConnection != null) {
            tvScreen.append("Get keep alive time: " + peer + "\n");
        }
    }

    @Override
    public void onPaused(uAbstractProtocolConnection connection) {
        if (mConnection != null) {
            tvScreen.append("Device is paused\n");
        }
    }

    @Override
    public void onGetDeviceInfo(uAbstractProtocolConnection connection, String info) {
        if (mConnection != null) {
            tvScreen.append("Device info: " + info + "\n");
        }
    }

    @Override
    public void onGetDeviceErrorStatistics(uAbstractProtocolConnection connection, byte error, byte stats) {
        if (mConnection != null) {
            tvScreen.append("Device error stats: " + error + ", " + stats + "\n");
        }
    }

    @Override
    public void onGetDeviceBattery(uAbstractProtocolConnection connection, byte battery) {
        if (mConnection != null) {
            tvScreen.append("Device battery is " + battery + "\n");
        }
    }

    @Override
    public void onSetDeviceParam(uAbstractProtocolConnection connection, byte param, byte[] data) {
        if (mConnection != null) {
            tvScreen.append("Set device param: " + param + ", " + StringUtil.byte2String(data) + "\n");
        }
    }

    @Override
    public void onGetDeviceParam(uAbstractProtocolConnection connection, byte param, byte[] data) {
        if (mConnection != null) {
            tvScreen.append("Get device param: " + param + ", " + StringUtil.byte2String(data) + "\n");
        }
    }
}
