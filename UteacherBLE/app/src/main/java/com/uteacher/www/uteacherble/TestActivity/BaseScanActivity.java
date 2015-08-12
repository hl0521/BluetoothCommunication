package com.uteacher.www.uteacherble.TestActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uteacher.www.uteacherble.R;
import com.uteacher.www.uteacherble.uBluetoothActivity.uBluetoothAdapterActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by cartman on 15/6/5.
 */
public abstract class BaseScanActivity extends uBluetoothAdapterActivity implements AdapterView.OnItemClickListener {

    private ListView lvDeviceList;
    private ArrayAdapter<String> lvAdapter;
    private ArrayList<String> deviceList = new ArrayList<>();

    private ProgressBar pbScanning;
    private Button scanControl;


    protected void startScan() {
        if (getScanner().startScan(20000, Pattern.compile("Tv221u-.*"), null, BaseScanActivity.this)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbScanning.setVisibility(View.VISIBLE);
                    scanControl.setText("Scanning...");
                    scanControl.setEnabled(false);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbScanning.setVisibility(View.INVISIBLE);
                    scanControl.setText("Scan Failed");
                    scanControl.setEnabled(true);
                }
            });
        }
    }

    protected void stopScan() {
        getScanner().stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceList.clear();
                lvAdapter.notifyDataSetChanged();
            }
        });

        startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopScan();
    }

    protected abstract int getContentViewResID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResID());

        lvDeviceList = (ListView) findViewById(R.id.id_device_list);
        lvAdapter = new ArrayAdapter<>(this, R.layout.component_blescanner_list, deviceList);
        lvDeviceList.setAdapter(lvAdapter);
        lvDeviceList.setOnItemClickListener(this);


        pbScanning = (ProgressBar) findViewById(R.id.id_scanning_progress);
        scanControl = (Button) findViewById(R.id.id_scan_control);
        scanControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceList.clear();
                        lvAdapter.notifyDataSetChanged();
                    }
                });
                startScan();
            }
        });
    }


    @Override
    public void onScan(final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceList.add(device.getAddress());
                lvAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onScanStop() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbScanning.setVisibility(View.INVISIBLE);
                scanControl.setText("Start Scan");
                scanControl.setEnabled(true);
            }
        });
    }

    protected abstract void onDeviceItemClick(BluetoothDevice device);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = getScanner().getScannedDevice(deviceList.get(position));
        if (device != null) {
            onDeviceItemClick(device);
        }
    }

    @Override
    protected void onBluetoothNotEnable() {
        finish();
    }

    @Override
    protected void onBluetoothNotSupported() {
        Toast.makeText(this, "Bluetooth not supported.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
