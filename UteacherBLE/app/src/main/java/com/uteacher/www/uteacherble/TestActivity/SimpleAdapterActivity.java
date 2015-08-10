package com.uteacher.www.uteacherble.TestActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uteacher.www.uteacherble.R;
import com.uteacher.www.uteacherble.TestUtil.StringUtil;
import com.uteacher.www.uteacherble.TestUtil.TestDataFactory;
import com.uteacher.www.uteacherble.uBluetoothActivity.uBluetoothAdapterActivity;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceAdapter;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceAttributeFactory;
import com.uteacher.www.uteacherble.uDeviceAdapter.uBleDeviceInterface;
import com.uteacher.www.uteacherble.uDeviceAdapter.uDeviceAdapterInterface;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class SimpleAdapterActivity extends BaseScanActivity {

    private uBleDeviceAdapter mAdapter = null;
    private Handler mHandler;

    private TextView tvScreen;

    @Override
    protected void onStop() {
        super.onStop();

        if (mAdapter != null) {
            mAdapter.disconnect();
            mAdapter = null;
        }
    }

    @Override
    protected int getContentViewResID() {
        return R.layout.activity_simple_adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        tvScreen = (TextView) findViewById(R.id.id_screen);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_adapter, menu);
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

    // Following callbacks are already in Main Thread
    @Override
    public void onConnected(String address, uDeviceAdapterInterface.STATUS status) {
        super.onConnected(address, status);
        tvScreen.append("Device connected\n");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    tvScreen.append("Getting device name\n");
                    if (!mAdapter.getDeviceName()) {
                        tvScreen.append("Failed to get device name\n");
                    }
                }

            }
        }, 100);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.disconnect();
                    mAdapter = null;
                    tvScreen.append("Device disconnected\n");
                }
            }
        }, 35000);

    }

    @Override
    public void onDisconnected(String address, uDeviceAdapterInterface.STATUS status) {
        super.onDisconnected(address, status);
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

            String password = uBleDeviceAttributeFactory.getAttribute(
                    uBleDeviceAttributeFactory.ATTRIBUTE.CANCEL_PASSWORD);
            tvScreen.append("Submitting password " + password + "\n");
            if (!mAdapter.submitPassword(password)) {
                tvScreen.append("Failed to submit password\n");
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
    public void onReceiveData(String address, byte[] data) {
        super.onReceiveData(address, data);

        if (mAdapter != null) {
            tvScreen.append("Received data " + StringUtil.byte2String(data) + "\n");

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dataIndex += 1;
                    testData(dataIndex);
                }
            }, 100);

        }

    }

    private int dataIndex;



    private void testData(int index) {
        byte[] data = TestDataFactory.getRandomTestData(index);

        if (data != null) {
            tvScreen.append("Sending data " + StringUtil.byte2String(data) + "\n");
            if (!mAdapter.send(data)) {
                tvScreen.append("Failed to send data\n");
            }
        } else {
            tvScreen.append("No more data to send\n");
        }

    }


    @Override
    public void onPasswordVerified(String address) {
        super.onPasswordVerified(address);
        if (mAdapter != null) {
            tvScreen.append("Password verified\n");

            dataIndex = 0;
            testData(dataIndex);
        }
    }

    @Override
    public void onIncorrectPassword(String address) {
        super.onIncorrectPassword(address);
        if (mAdapter != null) {
            tvScreen.append("Incorrect password\n");

            dataIndex = 0;
            testData(dataIndex);
        }
    }


    @Override
    protected void onDeviceItemClick(BluetoothDevice device) {
        if (mAdapter != null) {
            Toast.makeText(SimpleAdapterActivity.this, " Another device is in testing. ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            mAdapter = newDeviceAdapter(device);
            Toast.makeText(SimpleAdapterActivity.this, "Start device " + device.getAddress(),
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
}
