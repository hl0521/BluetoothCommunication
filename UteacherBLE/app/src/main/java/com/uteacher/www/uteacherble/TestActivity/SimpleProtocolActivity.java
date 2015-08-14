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
import com.uteacher.www.uteacherble.uProtocol.uProtocolStackInterface;

import java.util.Timer;
import java.util.TimerTask;


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
    private Button enableSetting;
    private TextView enableResult;

    private EditText loveEgg1Mode;
    private EditText loveEgg1Time;
    private EditText loveEgg2Mode;
    private EditText loveEgg2Time;
    private Button loveEggSetting;
    private TextView loveEggResult;

    private Button baseSetting;
    private EditText baseModeSetting;
    private EditText baseFreqSetting;
    private TextView baseSettingResult;

    private TextView socInquire;
    private TextView stateInquire;
    private TextView actionInquire;

    private Timer mTimer;

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
        enableSetting = (Button) findViewById(R.id.function_setting);
        enableResult = (TextView) findViewById(R.id.function_result);

        loveEgg1Mode = (EditText) findViewById(R.id.love_egg1_mode);
        loveEgg1Time = (EditText) findViewById(R.id.love_egg1_time);
        loveEgg2Mode = (EditText) findViewById(R.id.love_egg2_mode);
        loveEgg2Time = (EditText) findViewById(R.id.love_egg2_time);
        loveEggSetting = (Button) findViewById(R.id.love_egg_setting);
        loveEggResult = (TextView) findViewById(R.id.love_egg_result);

        baseModeSetting = (EditText) findViewById(R.id.base_mode_setting);
        baseFreqSetting = (EditText) findViewById(R.id.base_frequency_setting);
        baseSetting = (Button) findViewById(R.id.base_setting);
        baseSettingResult = (TextView) findViewById(R.id.base_setting_result);

        socInquire = (TextView) findViewById(R.id.soc_inquire);
        stateInquire = (TextView) findViewById(R.id.state_inquire);
        actionInquire = (TextView) findViewById(R.id.action_inquire);

        // Initial output
        socInquire.setText("电池的电量xxx    接收数据包xxx    长度错误包xxx\n" +
                "校验错误包xxx    控制错误包xxx    操作错误包xxx");

        stateInquire.setText("设备状态xxxx-xxxx    底座状态xxxx-0000");

        actionInquire.setText("累计时间xxxxx    瞬时位置x    瞬时方向x\n" +
                "总次数xxxxx    总长度xxxxx    瞬时深度x");

        connectControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mConnection != null) {
                        tvScreen.append("Connecting protocol\n");
                        if (!mConnection.startConnection()) {
                            tvScreen.append("Failed to connect protocol\n");
                        }
                    } else {
                        connectState.setText("设备状态：设备未连接");
                    }
                } else {
                    if (mConnection != null) {
                        tvScreen.append("Stop connecting protocal\n");
                        if (!mConnection.stopConnection()) {
                            tvScreen.append("Stop connecting failed");
                        }
                    } else {
                        connectState.setText("设备状态：设备未连接");
                    }
                }
            }
        });

        enableSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] number = new byte[1];
                number[0] = (byte) (((gameEnable.isChecked() ? 1 : 0) << 7) +
                        ((actionEnable.isChecked() ? 1 : 0) << 6) +
                        ((loveEgg1Enable.isChecked() ? 1 : 0) << 5) +
                        ((loveEgg2Enable.isChecked() ? 1 : 0) << 4) +
                        (baseEnable.isChecked() ? 1 : 0));

                if (mConnection != null) {
                    tvScreen.append("Device enable setting...\n");
                    if (!mConnection.deviceEnable(number)) {
                        enableResult.setText("设备功能设置结果：设置失败");
                        tvScreen.append("Device enable setting failed\n");
                    } else {
                        enableResult.setText("设备功能设置结果：" + Integer.toBinaryString(number[0]));
                    }
                } else {
                    enableResult.setText("设备功能设置结果：设备未连接");
                }
            }
        });

        loveEggSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int loveEggM1;
                int loveEggT1;
                int loveEggM2;
                int loveEggT2;
                byte[] number = new byte[2];

                try {
                    loveEggM1 = Integer.parseInt(loveEgg1Mode.getText().toString());
                    loveEggT1 = Integer.parseInt(loveEgg1Time.getText().toString());
                    loveEggM2 = Integer.parseInt(loveEgg2Mode.getText().toString());
                    loveEggT2 = Integer.parseInt(loveEgg2Time.getText().toString());

                    if (loveEggM1 > 15) {
                        loveEggM1 = 15;
                        loveEgg1Mode.setText(Integer.toString(loveEggM1));
                    }
                    if (loveEggT1 > 15) {
                        loveEggT1 = 15;
                        loveEgg1Time.setText(Integer.toString(loveEggT1));
                    }
                    if (loveEggM2 > 15) {
                        loveEggM2 = 15;
                        loveEgg2Mode.setText(Integer.toString(loveEggM2));
                    }
                    if (loveEggT2 > 15) {
                        loveEggT2 = 15;
                        loveEgg2Time.setText(Integer.toString(loveEggT2));
                    }

                    number[0] = (byte) (loveEggM1 * 16 + loveEggT1);
                    number[1] = (byte) (loveEggM2 * 16 + loveEggT2);

                    if (mConnection != null) {
                        tvScreen.append("Love egg setting...\n");
                        if (!mConnection.loveEggSetting(number)) {
                            tvScreen.append("Failed to set love egg\n");
                            loveEggResult.setText("跳蛋设置结果：设置失败");
                        } else {
                            loveEggResult.setText("跳蛋设置结果：" + number.toString());
                        }
                    } else {
                        loveEggResult.setText("跳蛋设置结果：设备未连接");
                    }
                } catch (Exception e) {
                    loveEggResult.setText("跳蛋设置结果：请输入完整参数");
                }
            }
        });

        baseSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int baseMode;
                int baseFrequency;
                byte[] number = new byte[4];

                try {
                    baseMode = Integer.parseInt(baseModeSetting.getText().toString());
                    baseFrequency = Integer.parseInt(baseFreqSetting.getText().toString());

                    if (baseMode > 255) {
                        baseMode = 255;
                        baseModeSetting.setText(Integer.toString(baseMode));
                    }
                    if (baseFrequency > 255) {
                        baseFrequency = 255;
                        baseFreqSetting.setText(Integer.toString(baseFrequency));
                    }

                    number[0] = (byte) baseMode;
                    number[1] = (byte) baseFrequency;
                    number[2] = 0;
                    number[3] = 0;

                    if (mConnection != null) {
                        if (!mConnection.baseSetting(number)) {
                            tvScreen.append("Base setting...\n");
                            tvScreen.append("Base setting failed\n");
                            baseSettingResult.setText("底座设置结果：失败");
                        } else {
                            baseSettingResult.setText("底座设置结果：模式-" + baseMode + " / 频率-" + baseFrequency);
                        }
                    } else {
                        baseSettingResult.setText("底座设置结果：设备未连接");
                    }

                } catch (Exception e) {
                    baseSettingResult.setText("底座设置结果：请输入完整参数");
                }
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
        }, 100000);

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
    public void onPaused(uAbstractProtocolConnection connection) {
        if (mConnection != null) {
            tvScreen.append("Device is paused\n");
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

                        if (!mConnection.getDeviceInformation()) {
                            tvScreen.append("Failed to get device information\n");
                        }
                    }
                }
            }, 100);
        }
    }

    @Override
    public void onGetDeviceInformation(uAbstractProtocolConnection connection, String info) {
        if (mConnection != null) {
            deviceInformation.setText("设备信息：" + info.substring(0, 0) + "-" + info.substring(2, 6)
                    + "-" + info.substring(8, 8));
            //FIXME, do something if device information doesn't match
            //...
        }
    }

    @Override
    public void onConnected(uAbstractProtocolConnection connection, byte[] ack) {
        if (mConnection != null) {
            if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                connectState.setText("设备状态：已连接");
                tvScreen.append("Connecting protocal successfully\n");

                TimerTask task1 = new TimerTask() {
                    @Override
                    public void run() {
                        mConnection.sendKeepAlive();
                    }
                };

                TimerTask task2 = new TimerTask() {
                    @Override
                    public void run() {
                        mConnection.getDeviceSoc();
                    }
                };

                TimerTask task3 = new TimerTask() {
                    @Override
                    public void run() {
                        mConnection.getDeviceStatus();
                    }
                };

                TimerTask task4 = new TimerTask() {
                    @Override
                    public void run() {
                        mConnection.getDeviceAction();
                    }
                };

                mTimer = new Timer(true);

                mTimer.schedule(task1, 100, 1000);
                mTimer.schedule(task2, 120, 30000);
                mTimer.schedule(task3, 140, 100);
                mTimer.schedule(task4, 160, 2000);
            } else {
                tvScreen.append("Failed to connect protocal\n");
            }
        }
    }

    @Override
    public void onDisConnected(uAbstractProtocolConnection connection, byte[] ack) {
        if (mConnection != null) {
            if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                connectState.setText("设备状态：已断开");

                mTimer.cancel();
                tvScreen.append("Protocol disconnected\n");
            } else {
                tvScreen.append("Failed to disconnect protocal\n");
            }
        }
    }

    @Override
    public void onDeviceEnable(uAbstractProtocolConnection connection, byte[] ack) {
        if (mConnection != null) {
            if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                enableResult.append("   成功");
                tvScreen.append("Enable device successfully\n");
            } else {
                enableResult.append("   失败");
                tvScreen.append("Enable device failed\n");
            }
        }
    }

    @Override
    public void onLoveEggSetting(uAbstractProtocolConnection connection, byte[] ack) {
        if (mConnection != null) {
            if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                loveEggResult.append("    成功");
                tvScreen.append("Love egg setting successfully\n");
            } else {
                loveEggResult.append("   失败");
                tvScreen.append("Love egg setting failed");
            }
        }
    }

    @Override
    public void onBaseSetting(uAbstractProtocolConnection connection, byte[] ack) {
        if (mConnection != null) {
            if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                baseSettingResult.append("    成功");
                tvScreen.append("Base setting successfully\n");
            } else {
                baseSettingResult.append("    失败");
                tvScreen.append("Base setting failed\n");
            }
        }
    }

    @Override
    public void onGetDeviceSoc(uAbstractProtocolConnection connection, byte[] data) {
        if (mConnection != null) {
            socInquire.setText("电池的电量" + data[0] + "    接收数据包" + data[1] + "    长度错误包\n" + data[2]
                    + "校验错误包" + data[3] + "    控制错误包" + data[4] + "    操作错误包" + data[5]);
        }
    }

    @Override
    public void onGetDeviceStatus(uAbstractProtocolConnection connection, byte[] data) {
        if (mConnection != null) {
            stateInquire.setText("设备状态" + Integer.toBinaryString(data[0])
                    + "    底座状态" + Integer.toBinaryString(data[1]));
        }
    }

    @Override
    public void onGetDeviceAction(uAbstractProtocolConnection connection, byte[] data) {
        if (mConnection != null) {
            actionInquire.setText("累计时间" + (data[0] * 256 + data[1]) + "    瞬时位置" + (data[2] >> 4)
                    + "    瞬时方向" + (data[2] & 0x0F) + "\n" + "总次数" + (data[4] * 256 + data[5])
                    + "    总长度" + (data[6] * 256 + data[7]) + "    瞬时深度" + data[3]);
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
    public void onIncorrectPassword(String address) {
        super.onIncorrectPassword(address);
        if (mAdapter != null) {
            tvScreen.append("Incorrect password\n");
        }
    }
}
