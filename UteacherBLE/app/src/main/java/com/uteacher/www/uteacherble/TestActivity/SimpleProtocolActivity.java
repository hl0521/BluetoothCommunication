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

    private static final String TAG = uAbstractProtocolConnection.class.getSimpleName();

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

    private Button btnActionView;

    private Timer mTimer;
    private TimerTask task1;
    private TimerTask task2;
    private TimerTask task3;
    private TimerTask task4;

    private Timer bluetoothTimer;
    private TimerTask bluetoothTimerTask;

    private uAbstractProtocolStack mProtocolStack;
    private uAbstractProtocolConnection mConnection;

    private ChartDialog chartDialog;

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

        btnActionView = (Button) findViewById(R.id.action_view);
        btnActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chartDialog == null) {
                    chartDialog = ChartDialog.newInstance("","");
                }
                chartDialog.show(getFragmentManager(), "");
            }
        });

        // Initial output
        socInquire.setText("电池的电量xxx    接收数据包xxx    长度错误包xxx\n" +
                "校验错误包xxx    控制错误包xxx    操作错误包xxx");

        stateInquire.setText("设备状态xxxx-xxxx    底座状态xxxx-0000");

        actionInquire.setText("累计时间xxxxx    瞬时位置xxxxxx\n" +
                "总次数xxxxx    总长度xxxxx    瞬时深度x");

        connectControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (bluetoothTimer == null) {
                        bluetoothTimer = new Timer(true);
                    }

                    if (bluetoothTimerTask == null) {
                        bluetoothTimerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if (mConnection != null) {
                                    mConnection.getQueue().sendData();
                                }
                            }
                        };

                        bluetoothTimer.schedule(bluetoothTimerTask, 10, 50);
                    }

                    if (mConnection != null) {
                        tvScreen.append("连接设备：连接中...\n");
                        if (!mConnection.startConnection()) {
                            tvScreen.append("连接设备：数据发送失败\n");
                        }
                    } else {
                        connectState.setText("设备状态：未初始化");
                    }
                } else {
                    if (mConnection != null) {
                        tvScreen.append("断开设备：断开中...\n");
                        if (!mConnection.stopConnection()) {
                            tvScreen.append("断开设备：数据发送失败\n");
                        }
                    } else {
                        connectState.setText("设备状态：未初始化");
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

                String str = Integer.toBinaryString(number[0] + 256);

                if (mConnection != null) {
                    tvScreen.append("设备模块使能设置中...\n");
                    if (!mConnection.deviceEnable(number)) {
                        enableResult.setText("设备使能设置结果：数据发送失败");
                        tvScreen.append("设备模块使能设置：数据发送失败\n");
                    } else {
                        enableResult.setText("设备使能设置结果：" + str.substring(str.length() - 8, str.length()));
                    }
                } else {
                    enableResult.setText("设备使能设置结果：未初始化");
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
                        tvScreen.append("跳蛋设置中...\n");
                        if (!mConnection.loveEggSetting(number)) {
                            tvScreen.append("跳蛋设置：数据发送失败\n");
                            loveEggResult.setText("跳蛋设置结果：数据发送失败");
                        } else {
                            loveEggResult.setText("跳蛋设置结果：" + loveEggM1 + "  " + loveEggT1
                                    + "  " + loveEggM2 + "  " + loveEggT2);
                        }
                    } else {
                        loveEggResult.setText("跳蛋设置结果：未初始化");
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
                        tvScreen.append("底座设置中...\n");
                        if (!mConnection.baseSetting(number)) {
                            tvScreen.append("底座设置：数据发送失败\n");
                            baseSettingResult.setText("底座设置结果：数据发送失败");
                        } else {
                            baseSettingResult.setText("底座设置结果：模式-" + baseMode + " / 频率-" + baseFrequency);
                        }
                    } else {
                        baseSettingResult.setText("底座设置结果：未初始化");
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

            if (bluetoothTimer == null) {
                bluetoothTimer = new Timer(true);
            }

            if (bluetoothTimerTask == null) {
                bluetoothTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (mConnection != null) {
                            mConnection.getQueue().sendData();
                        }
                    }
                };
            }

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
        }, 1000);

//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mConnection != null) {
//                    mConnection.stopConnection();
//                }
//
//                if (mAdapter != null) {
//                    mAdapter.disconnect();
//                    mAdapter = null;
//                    tvScreen.append("Device disconnected\n");
//                }
//            }
//        }, 200000);

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
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mAdapter.getDeviceName()) {
                        tvScreen.append("Failed to get device name\n");
                    }
                }
            }, 200);
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

                        tvScreen.append("获取设备信息中...\n");
                        if (!mConnection.getDeviceInformation()) {
                            tvScreen.append("获取设备信息：数据发送失败\n");
                        }
                    }
                }
            }, 100);

            // Start the bluetooth Timer
            if ((bluetoothTimer != null) && (bluetoothTimerTask != null)) {
                bluetoothTimer.schedule(bluetoothTimerTask, 120, 50);
            }
        }
    }

    @Override
    public void onGetDeviceInformation(uAbstractProtocolConnection connection, final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    deviceInformation.setText("设备信息： " + info.substring(0, 2) + " - " + info.substring(2, 8)
                            + " - " + info.substring(8, 10));
                    tvScreen.append("设备信息获取成功\n");
                    //FIXME, do something if device information doesn't match
                    //...
                }
            }
        });
    }

    @Override
    public void onConnected(uAbstractProtocolConnection connection, final byte[] ack) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                        connectState.setText("设备状态：已连接");
                        tvScreen.append("连接设备：成功\n");

                        if (task1 == null) {
                            task1 = new TimerTask() {
                                @Override
                                public void run() {
                                    if (mConnection != null) {
                                        mConnection.sendKeepAlive();
                                    }
                                }
                            };
                        }

                        if (task2 == null) {
                            task2 = new TimerTask() {
                                @Override
                                public void run() {
                                    if (mConnection != null) {
                                        mConnection.getDeviceSoc();
                                    }
                                }
                            };
                        }

                        if (task3 == null) {
                            task3 = new TimerTask() {
                                @Override
                                public void run() {
                                    if (mConnection != null) {
                                        mConnection.getDeviceStatus();
                                    }
                                }
                            };
                        }

                        if (task4 == null) {
                            task4 = new TimerTask() {
                                @Override
                                public void run() {
                                    if (mConnection != null) {
                                        mConnection.getDeviceAction();
                                    }
                                }
                            };
                        }

                        if (mTimer == null) {
                            mTimer = new Timer(true);
                        }

                        mTimer.schedule(task1, 100, 1000);
                        mTimer.schedule(task2, 130, 10000);
                        mTimer.schedule(task3, 160, 1000);
                        mTimer.schedule(task4, 190, 100);
                        tvScreen.append("定时任务已启动\n");
                    } else {
                        tvScreen.append("连接设备：失败\n");
                    }
                }
            }
        });
    }

    @Override
    public void onDisConnected(uAbstractProtocolConnection connection, final byte[] ack) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                        connectState.setText("设备状态：已断开");
                        tvScreen.append("断开设备：成功\n");

                        if (mTimer != null) {
                            if (task1 != null) {
                                task1.cancel();
                                task1 = null;
                            }
                            if (task2 != null) {
                                task2.cancel();
                                task2 = null;
                            }
                            if (task3 != null) {
                                task3.cancel();
                                task3 = null;
                            }
                            if (task4 != null) {
                                task4.cancel();
                                task4 = null;
                            }
                            mTimer.cancel();
                            mTimer = null;
                            tvScreen.append("定时任务已取消\n");
                        }

                        if (bluetoothTimer != null) {
                            if (bluetoothTimerTask != null) {
                                bluetoothTimerTask.cancel();
                                bluetoothTimerTask = null;
                            }
                            bluetoothTimer.cancel();
                            bluetoothTimer = null;
                        }
                    } else {
                        tvScreen.append("断开设备：失败\n");
                    }
                }
            }
        });
    }

    @Override
    public void onDeviceEnable(uAbstractProtocolConnection connection, final byte[] ack) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                        enableResult.append("   成功");
                        tvScreen.append("设备模块使能设置成功\n");
                    } else {
                        enableResult.append("   失败");
                        tvScreen.append("设备模块使能设置失败\n");
                    }
                }
            }
        });
    }

    @Override
    public void onLoveEggSetting(uAbstractProtocolConnection connection, final byte[] ack) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                        loveEggResult.append("    成功");
                        tvScreen.append("跳蛋设置成功\n");
                    } else {
                        loveEggResult.append("   失败");
                        tvScreen.append("跳蛋设置失败\n");
                    }
                }
            }
        });
    }

    @Override
    public void onBaseSetting(uAbstractProtocolConnection connection, final byte[] ack) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    if (ack[0] == mProtocolStack.getErrorCode(uProtocolStackInterface.ERROR.ERROR_OK)) {
                        baseSettingResult.append("    成功");
                        tvScreen.append("底座设置成功\n");
                    } else {
                        baseSettingResult.append("    失败");
                        tvScreen.append("底座设置失败\n");
                    }
                }
            }
        });
    }

    @Override
    public void onGetDeviceSoc(uAbstractProtocolConnection connection, final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    int temp1 = (data[0] & 0x0FF) + 1000;
                    int temp2 = (data[1] & 0x0FF) + 1000;
                    int temp3 = (data[2] & 0x0FF) + 1000;
                    int temp4 = (data[3] & 0x0FF) + 1000;
                    int temp5 = (data[4] & 0x0FF) + 1000;
                    int temp6 = (data[5] & 0x0FF) + 1000;
                    String str1 = Integer.toString(temp1);
                    String str2 = Integer.toString(temp2);
                    String str3 = Integer.toString(temp3);
                    String str4 = Integer.toString(temp4);
                    String str5 = Integer.toString(temp5);
                    String str6 = Integer.toString(temp6);
                    socInquire.setText("电池的电量" + str1.substring(str1.length() - 3, str1.length())
                            + "    接收数据包" + str2.substring(str2.length() - 3, str2.length())
                            + "    长度错误包" + str3.substring(str3.length() - 3, str3.length())
                            + "\n校验错误包" + str4.substring(str4.length() - 3, str4.length())
                            + "    控制错误包" + str5.substring(str5.length() - 3, str5.length())
                            + "    操作错误包" + str6.substring(str6.length() - 3, str6.length()));
                }
            }
        });
    }

    @Override
    public void onGetDeviceStatus(uAbstractProtocolConnection connection, final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    int temp1 = (data[0] & 0x0FF) + 256;
                    int temp2 = (data[1] & 0x0FF) + 256;
                    String str1 = Integer.toBinaryString(temp1);
                    String str2 = Integer.toBinaryString(temp2);
                    stateInquire.setText("设备状态" + str1.substring(str1.length() - 8, str1.length())
                            + "    底座状态" + str2.substring(str2.length() - 8, str2.length()));
                }
            }
        });
    }

    @Override
    public void onGetDeviceAction(uAbstractProtocolConnection connection, final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    int temp1 = (data[0] & 0x0FF) * 256 + (data[1] & 0x0FF) + 100000;
                    int temp2 = (data[4] & 0x0FF) * 256 + (data[5] & 0x0FF) + 100000;
                    int temp3 = (data[6] & 0x0FF) * 256 + (data[7] & 0x0FF) + 100000;
                    int temp4 = (data[2] & 0x0FF) + 256;
                    String str1 = Integer.toString(temp1);
                    String str2 = Integer.toString(temp2);
                    String str3 = Integer.toString(temp3);
                    String str4 = Integer.toBinaryString(temp4);

                    // "instantPosition 是 瞬时位置"
                    int instantPosition = 0;
                    byte temp5 = data[2];
                    for (int i=0;i<6;i++) {
                        int b = temp5 & (1<<i);
                        if (b != 0) {
                            instantPosition = instantPosition + 1;
                        } else {
                            break;
                        }
                    }

                    actionInquire.setText("累计时间" + str1.substring(str1.length() - 5, str1.length())
                            + "    瞬时位置" + str4.substring(str4.length() - 6, str4.length()) + "\n"
                            + "总次数" + str2.substring(str2.length() - 5, str2.length())
                            + "   总长度" + str3.substring(str3.length() - 5, str3.length())
                            + "    瞬时深度" + data[3]);

                    if (chartDialog != null) {
                        chartDialog.updateData(instantPosition, (int)data[3]);
                    }
                }
            }
        });
    }

    @Override
    public void onKeepAliveTimeout(uAbstractProtocolConnection connection) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    tvScreen.append("Keep alive timeout\n");
                }
            }
        });
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
