package com.primehealthcare.tele

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ihealth.communication.manager.DiscoveryTypeEnum
import com.ihealth.communication.manager.iHealthDevicesCallback
import com.ihealth.communication.manager.iHealthDevicesManager
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG = "MainActivity"
        const val BLUETOOTH_ENABLED = 1
    }

    private val progressDialog: ProgressDialog? = null
    var bluetoothReady = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
//        iHealthDevicesManager.getInstance().init(this.applicationContext)

        /*
* Register callback to the manager. This method will return a callback Id.
*/

        checkBluetooth()
        if (bluetoothReady){
            Log.d(TAG, "Bluetooth Ready")
            val callbackId = iHealthDevicesManager.getInstance().registerClientCallback(
                iHealthDevicesCallback
            )
        }


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {

            if (sdkAuthentication()) {
                iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.ABPM)
            } else {
                Log.d(TAG, "sdkAuthentication False")
            }


        })
    }

    private fun checkBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            progressDialog?.setMessage("Your device does not support Bluetooth!")
            progressDialog?.show()
        } else if (!bluetoothAdapter.isEnabled) {
            // Bluetooth is not enable
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, BLUETOOTH_ENABLED)
        } else {
            bluetoothReady = true
        }
    }

    private val iHealthDevicesCallback: iHealthDevicesCallback = object : iHealthDevicesCallback() {

        override fun onScanDevice(mac: String?, deviceType: String?, rssi: Int) {
            super.onScanDevice(mac, deviceType, rssi)
            Log.i(TAG, "onScanDevice" + mac + "-" + deviceType);

        }


        override fun onScanFinish() {
            super.onScanFinish()
            Log.i(TAG, "onScanFinish")


        }

        override fun onDeviceConnectionStateChange(
            mac: String?,
            deviceType: String?,
            status: Int,
            errorID: Int
        ) {
            super.onDeviceConnectionStateChange(mac, deviceType, status, errorID)
            Log.i(TAG, "onDeviceConnectionStateChange")

        }
    }

    private fun sdkAuthentication():Boolean{
        try {
            val inputStream: InputStream = this.assets.open("com_primehealthcare_tele_android.pem");
            val size: Int = inputStream.available();
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val isPass: Boolean = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            Log.d(TAG, "isPass: " + isPass);
            return isPass
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return false

    }

}