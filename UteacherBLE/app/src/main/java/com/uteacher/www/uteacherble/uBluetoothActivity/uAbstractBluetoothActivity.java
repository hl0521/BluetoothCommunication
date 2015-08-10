package com.uteacher.www.uteacherble.uBluetoothActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.uteacher.www.uteacherble.uBluetooth.uBleScanner;
import com.uteacher.www.uteacherble.uBluetooth.uBleScannerInterface;
import com.uteacher.www.uteacherble.uBluetooth.uBleService;
import com.uteacher.www.uteacherble.uBluetooth.uBleServiceInterface;
import com.uteacher.www.uteacherble.uBluetooth.uBluetoothFactory;

import java.util.regex.Pattern;


/**
 * Created by cartman on 15/5/27.
 */
public abstract class uAbstractBluetoothActivity extends Activity implements uBleScannerInterface.uBleScannerCallback {

    private final static String TAG = uAbstractBluetoothActivity.class.getSimpleName();

    private uBleScanner mScanner;
    private uBleService mService;
    private BluetoothAdapter mBluetoothAdapter;

    private final int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    protected abstract void onBluetoothNotEnable();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Bluetooth not enalbed.");
            onBluetoothNotEnable();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // if we don't register with a callback, we should use service.connect(address, callback) later.
        mService.register(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScanner.stopScan();
        mService.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.unbind();
    }

    protected abstract void onBluetoothNotSupported();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG, "Bluetooth not supported.");
            onBluetoothNotSupported();
            return;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth not supported.");
            onBluetoothNotSupported();
            return;
        }

        mScanner = uBluetoothFactory.getScannerInstance(mBluetoothAdapter);
        mService = uBluetoothFactory.getServiceInstance(mBluetoothAdapter, this);
        if (!mService.bind()) {
            Log.d(TAG, "Service bind failed");
            onServiceBindFailed(mService);
            return;
        }
    }

    protected abstract void onServiceBindFailed(uBleService service);


    protected uBleScanner getScanner() {
        return mScanner;
    }

    protected uBleService getService() {
        return mService;
    }

    protected BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

}
