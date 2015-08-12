package com.uteacher.www.uteacherble.TestActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.uteacher.www.uteacherble.R;
import com.uteacher.www.uteacherble.TestUtil.StringUtil;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceAdapter;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceAttributeFactory;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceInterface;
import com.uteacher.www.uteacherble.uDeviceAdapter.uDeviceAdapterInterface;
import com.uteacher.www.uteacherble.uProtocol.Connection.uAbstractProtocolConnection;
import com.uteacher.www.uteacherble.uProtocol.Connection.uProtocolConnectionInterface;
import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;
import com.uteacher.www.uteacherble.uProtocol.uProtocolFactory;


public class SimpleProtocolActivity extends BaseScanActivity implements uProtocolConnectionInterface.ProtocolCallback {

    private Handler mHandler;
    private TextView tvScreen;
    private uBleDeviceAdapter mAdapter;

    private TextView bluetoothStatus;
    private TextView deviceInformation;
    private Switch connectControl;
    private TextView connectState;

    private CheckBox gameEnable;
    private CheckBox actionEnable;
    private CheckBox loveEgg1Enable;
    private CheckBox loveEgg2Enable;
    private CheckBox baseEnable;
    private Button functionSetting;
    private TextView functionResult;

    private EditText loveEgg1Mode;
    private EditText loveEgg1Time;
    private EditText loveEgg2Mode;
    private EditText loveEgg2Time;
    private Button   loveEggSetting;
    private TextView loveEggResult;

    private uAbstractProtocolStack mProtocolStack;
    private uAbstractProtocolConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        tvScreen = (TextView) findViewById(R.id.id_screen);
        mProtocolStack = uProtocolFactory.getProtocolInstance(uProtocolFactory.PROTO_STACK_V1);

        bluetoothStatus = (TextView) findViewById(R.id.bluetooth_state);
        deviceInformation = (TextView) findViewById(R.id.device_information);
        connectControl = (Switch) findViewById(R.id.connect_control);
        connectState = (TextView) findViewById(R.id.connect_state);

        gameEnable = (CheckBox) findViewById(R.id.game_enable);
        actionEnable = (CheckBox) findViewById(R.id.action_enable);
        loveEgg1Enable = (CheckBox) findViewById(R.id.love_egg1_enable);
        loveEgg2Enable = (CheckBox) findViewById(R.id.love_egg2_enable);
        baseEnable = (CheckBox) findViewById(R.id.base_enable);
        functionSetting = (Button) findViewById(R.id.function_setting);
        functionResult = (TextView) findViewById(R.id.function_result);

        loveEgg1Mode = (EditText) findViewById(R.id.love_egg1_mode);
        loveEgg1Time = (EditText) findViewById(R.id.love_egg1_time);
        loveEgg2Mode = (EditText) findViewById(R.id.love_egg2_mode);
        loveEgg2Time = (EditText) findViewById(R.id.love_egg2_time);
        loveEggSetting = (Button) findViewById(R.id.love_egg_setting);
        loveEggResult = (TextView) findViewById(R.id.love_egg_result);


        connectControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    connectState.setText("设备状态：已连接");
                } else {
                    connectState.setText("设备状态：已断开");
                }
            }
        });

        functionSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number;
                number = (gameEnable.isChecked() ? 1 : 0) * 10000 +
                        (actionEnable.isChecked() ? 1 : 0) * 1000 +
                        (loveEgg1Enable.isChecked() ? 1 : 0) * 100 +
                        (loveEgg2Enable.isChecked() ? 1 : 0) * 10 +
                        (baseEnable.isChecked() ? 1 : 0);
                functionResult.setText("设备功能设置结果：" + String.valueOf(number));
            }
        });

        loveEggSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
                        tvScreen.setText("其它调试信息：\n");
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
                    String password = uBleDeviceAttributeFactory.getAttribute(uBleDeviceAttributeFactory.ATTRIBUTE.CANCEL_PASSWORD);
                    tvScreen.append("Submitting password " + password + "\n");
                    if (!mAdapter.submitPassword(password)) {
                        tvScreen.append("Failed to submit password\n");
                    }
                }

            }
        }, 500);

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
        }, 10000);

    }

    @Override
    public void onDisconnected(String address, uDeviceAdapterInterface.STATUS status) {
        super.onDisconnected(address, status);
        if (mConnection != null) {
            mProtocolStack.destroyConnection(mConnection.getAdapter());
            mConnection = null;
        }

        bluetoothStatus.setText("蓝牙状态：断开");
    }

    @Override
    public void onConnected(uAbstractProtocolConnection connection) {
        if (mConnection != null) {
            tvScreen.append("Sending keep alive\n");
            if (!mConnection.sendKeepAlive((byte) (mConnection.getKeepAliveTimer() / 1000))) {
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
                        for (int i = 0; i < 4; i++) {
                            tvScreen.append("Getting device error stats: " + i + "\n");
                            if (!mConnection.getDeviceErrorStatistics((byte) i)) {
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
                        for (int i = 0; i < 4; i++) {
                            tvScreen.append("Setting device param: " + i + "\n");
                            if (!mConnection.setDeviceParameter((byte) i, new byte[]{99})) {
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
                        for (int i = 0; i < 4; i++) {
                            tvScreen.append("Getting device param: " + i + "\n");
                            if (!mConnection.getDeviceParameter((byte) i)) {
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
        if (mConnection != null) {
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


    @Override
    public void onGetTransmitDelta(String address, uBleDeviceInterface.DELTA delta, uDeviceAdapterInterface.STATUS status) {
        super.onGetTransmitDelta(address, delta, status);
        if (mAdapter != null) {
            if (status == uDeviceAdapterInterface.STATUS.SUCCEED) {
                tvScreen.append("Transmit delta is " + delta.toString() + "\n");
            } else {
                tvScreen.append("Failed to get transmit delta\n");
            }

            tvScreen.append("Getting UART rate\n");
            if (!mAdapter.getUARTRate()) {
                tvScreen.append("Failed to get UART rate\n");
            }
        }
    }

    @Override
    public void onGetUARTRate(String address, uBleDeviceInterface.RATE rate, uDeviceAdapterInterface.STATUS status) {
        super.onGetUARTRate(address, rate, status);
        if (mAdapter != null) {
            if (status == uDeviceAdapterInterface.STATUS.SUCCEED) {
                tvScreen.append("UART rate is " + rate.toString() + "\n");
            } else {
                tvScreen.append("Failed to get UART rate\n");
            }

            tvScreen.append("Getting broadcast frequency\n");
            if (!mAdapter.getBroadcastFrequency()) {
                tvScreen.append("Failed to get broadcast frequency\n");
            }
        }
    }

    @Override
    public void onGetBroadcastFrequency(String address, uBleDeviceInterface.FREQUENCY frequency, uDeviceAdapterInterface.STATUS status) {
        super.onGetBroadcastFrequency(address, frequency, status);
        if (mAdapter != null) {
            if (status == uDeviceAdapterInterface.STATUS.SUCCEED) {
                tvScreen.append("Broadcast frequency is " + frequency.toString() + "\n");
            } else {
                tvScreen.append("Failed to get broadcast frequency\n");
            }

            tvScreen.append("Getting transmit power\n");
            if (!mAdapter.getTransmitPower()) {
                tvScreen.append("Failed to get transmit power\n");
            }
        }
    }

    @Override
    public void onGetTransmitPower(String address, uBleDeviceInterface.POWER power, uDeviceAdapterInterface.STATUS status) {
        super.onGetTransmitPower(address, power, status);
        if (mAdapter != null) {
            if (status == uDeviceAdapterInterface.STATUS.SUCCEED) {
                tvScreen.append("Transmit power is " + power.toString() + "\n");
            } else {
                tvScreen.append("Failed to get transmit power\n");
            }

            tvScreen.append("蓝牙协议、队列、连接初始化...\n");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mConnection = mProtocolStack.newConnection(mAdapter, SimpleProtocolActivity.this);
                        tvScreen.append("初始化已完成，可进行正常操作\n");

                        tvScreen.append("Connecting protocol\n");
                        if (!mConnection.startConnection()) {
                            tvScreen.append("Failed to connect protocol\n");
                        }
                    }

                }
            }, 100);
        }
    }

    @Override
    public void onGetDeviceName(String address, String name, uDeviceAdapterInterface.STATUS status) {
        super.onGetDeviceName(address, name, status);
        if (mAdapter != null) {
            if (status == uDeviceAdapterInterface.STATUS.SUCCEED) {
                tvScreen.append("Device name is " + name + "\n");
            } else {
                tvScreen.append("Failed to get device name\n");
            }

            tvScreen.append("Getting transmit delta\n");
            if (!mAdapter.getTransmitDelta()) {
                tvScreen.append("Failed to get transmit delta\n");
            }
        }
    }

    @Override
    public void onPasswordVerified(String address) {
        super.onPasswordVerified(address);
        if (mAdapter != null) {
            tvScreen.append("Password verified\n");

            bluetoothStatus.setText("蓝牙状态：连接");

            tvScreen.append("Getting device name\n");
            if (!mAdapter.getDeviceName()) {
                tvScreen.append("Failed to get device name\n");
            }
        }
    }

    @Override
    public void onIncorrectPassword(String address) {
        super.onIncorrectPassword(address);
        if (mAdapter != null) {
            tvScreen.append("Incorrect password\n");
        }
    }
}
