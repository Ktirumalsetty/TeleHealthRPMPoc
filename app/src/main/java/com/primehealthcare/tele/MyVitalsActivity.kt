package com.primehealthcare.tele

import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ihealth.communication.control.BtmControl
import com.ihealth.communication.control.HsProfile
import com.ihealth.communication.control.TS28BControl
import com.ihealth.communication.manager.DiscoveryTypeEnum
import com.ihealth.communication.manager.iHealthDevicesCallback
import com.ihealth.communication.manager.iHealthDevicesManager
import com.primehealthcare.tele.databinding.ActivityMyVitalsBinding
import org.json.JSONObject
import java.io.InputStream


class MyVitalsActivity : BaseAppCompatActivity<ActivityMyVitalsBinding>() {

    companion object{
        const val TAG = "MyVitalsActivity"
        const val THERMOMETER = "TS28B"
    }
    var mDeviceName:String =""
    var deviceType:String =""
    var deviceMac:String =""
    var mClientCallbackId : Int = 0
    private var mTS28BControl: TS28BControl? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Vitals"
        if(sdkAuthentication()){
            binding.tvAuthentication.text = "SDK Authentication Success"
            binding.btnTempStartDisc.isEnabled = true
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
                if (!isBluetoothEnabled()){
                    DialogUtils.displayDialog(this, "Bluetooth disabled.Please enable and restart")
                }
            }else{
                DialogUtils.displayDialog(this, "Bluetooth Not available.")
                binding.btnTempStartDisc.isEnabled = false
                binding.btnTempStartDisc.isEnabled = false
                binding.tvReadingResult.visibility = View.GONE
                return
            }

            /*
         * Register callback to the manager. This method will return a callback Id.
         */
//            iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback)

        }else{
            binding.tvAuthentication.text = "SDK Authentication Failure"
            binding.btnTempStartDisc.isEnabled = false
            binding.btnTempStartDisc.isEnabled = false
            binding.tvReadingResult.visibility = View.GONE

        }

        binding.btnTempStartDisc.setOnClickListener(View.OnClickListener {
            mDeviceName = "TS28B"
            mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback)

            startDiscovery()
        })

        binding.btnConnect.setOnClickListener(View.OnClickListener {
//            mDeviceName = "TS28B"
            Log.d(TAG, "Device Type $deviceType MAC: $deviceMac")
            connectDevice(deviceType, deviceMac, "")
        })

        binding.btnMeasure.setOnClickListener(View.OnClickListener {
//            mDeviceName = "TS28B"
            mTS28BControl?.getMeasurement()
        })

    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_my_vitals
    }

    private fun sdkAuthentication():Boolean{
        try {
            val inputStream: InputStream = this.assets.open("com_primehealthcare_tele_android.pem");
//            val inputStream: InputStream = this.assets.open("com_demo_sdk_android.pem.pem")
            val size: Int = inputStream.available();
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val isPass: Boolean = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            Log.d(TAG, "isPass: $isPass");
            return isPass
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return false

    }

    fun startDiscovery(){
        if (mDeviceName == "KN550BT") {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("BP550BT"))
        } else if (mDeviceName == "FDIR-V3") {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("FDIR_V3"))
        } else if (mDeviceName == "ECGUSB") {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("ECG3USB"))
        } else if (mDeviceName.contains("PO3")) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("PO3"))
        } else {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum(mDeviceName))
        }
    }

    private fun getDiscoveryTypeEnum(deviceName: String): DiscoveryTypeEnum? {
        for (type in DiscoveryTypeEnum.values()) {
            if (deviceName == type.name) {
                return type
            }
        }
        return null
    }

    fun isBluetoothEnabled(): Boolean {
        try {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return mBluetoothAdapter.isEnabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun connectDevice(type: String, mac: String, userName: String) {
        val req: Boolean = if (type == iHealthDevicesManager.TYPE_FDIR_V3) {
            iHealthDevicesManager.getInstance().connectTherm(
                    userName, mac, type,
                    BtmControl.TEMPERATURE_UNIT_C.toInt(), BtmControl.MEASURING_TARGET_BODY.toInt(),
                    BtmControl.FUNCTION_TARGET_OFFLINE.toInt(), 0, 1, 0
            )
        } else {
            iHealthDevicesManager.getInstance().connectDevice(userName, mac, type)
        }
        if (!req) {
            Toast.makeText(
                    this,
                    "Haven’t permission to connect this device or the mac is not valid",
                    Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private val miHealthDevicesCallback: iHealthDevicesCallback = object : iHealthDevicesCallback() {

        override fun onScanDevice(
                mac: String?,
                deviceType: String?,
                rssi: Int,
                manufactorData: MutableMap<String, Any>?
        ) {
//            super.onScanDevice(mac, deviceType, rssi, manufactorData)
            Log.i(TAG, "onScanDevice - mac:$mac - deviceType:$deviceType - rssi:$rssi - manufactorData:$manufactorData")
            val bundle = Bundle()
            bundle.putString("mac", mac)
            bundle.putString("type", deviceType)
            bundle.putInt("rssi", rssi)
            deviceMac = mac!!
            this@MyVitalsActivity.deviceType = deviceType!!
            this@MyVitalsActivity.runOnUiThread(Runnable {
                binding.tvResult.visibility = View.VISIBLE
                binding.tvResult.text = "startDiscovery() ---current device type :" + deviceType + " Mac:" + mac
            })


//            val msg = Message()
//            msg.what = HANDLER_SCAN
//            msg.data = bundle
//            myHandler.sendMessage(msg)
//            connectDevice(deviceType!!, mac!!, "")
            //Device additional information wireless MAC suffix table

            //Device additional information wireless MAC suffix table
            if (manufactorData != null) {
                Log.d(
                        TAG,
                        "onScanDevice mac suffix = " + manufactorData.get(HsProfile.SCALE_WIFI_MAC_SUFFIX)
                )
            }
        }

        override fun onDeviceConnectionStateChange(
                mac: String?,
                deviceType: String?,
                status: Int,
                errorID: Int,
                manufactorData: MutableMap<Any?, Any?>?
        ) {
            Log.d(TAG, "onDeviceConnectionStateChange()")
            Log.e(
                    TAG,
                    "mac:$mac deviceType:$deviceType status:$status errorid:$errorID -manufactorData:$manufactorData"
            )
            val bundle = Bundle()
            bundle.putString("mac", mac)
            bundle.putString("type", deviceType)
            val msg = Message()

            when (status) {
                iHealthDevicesManager.DEVICE_STATE_CONNECTED -> {
//                    msg.what = HANDLER_CONNECTED
                    iHealthDevicesManager.getInstance().stopDiscovery()
                    this@MyVitalsActivity.runOnUiThread(Runnable {
                        binding.btnMeasure.visibility = View.VISIBLE
                    })
                    iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId)
                    binding.btnTempStartDisc.isEnabled = false
                    when (deviceType) {
                        iHealthDevicesManager.TYPE_TS28B -> {
                            val mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthResultCallback)
                            iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_TS28B)
                            mTS28BControl = iHealthDevicesManager.getInstance().getTS28BControl(mac) as TS28BControl

                        }
                    }

                }
                iHealthDevicesManager.DEVICE_STATE_DISCONNECTED -> {
//                    msg.what = HANDLER_DISCONNECT
                    DialogUtils.displayDialog(this@MyVitalsActivity, "Device has been Disconected")
                    this@MyVitalsActivity.runOnUiThread(Runnable {
                        binding.tvResult.text = "Device has been Disconected"
                        binding.btnTempStartDisc.isEnabled = true
                    })


                }

                iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL -> {
//                    msg.what = HANDLER_CONNECT_FAIL
                    DialogUtils.displayDialog(this@MyVitalsActivity, "Device Connection failed")
                    this@MyVitalsActivity.runOnUiThread(Runnable {
                        binding.tvResult.text = "Device Connection failed"
                        binding.btnTempStartDisc.isEnabled = true
                    })


                }
                iHealthDevicesManager.DEVICE_STATE_RECONNECTING -> {
//                    msg.what = HANDLER_RECONNECT
                    DialogUtils.showToast(applicationContext, "Device reconnecting")
                    this@MyVitalsActivity.runOnUiThread(Runnable {
                        binding.tvResult.text = "Device reconnecting"

                    })


                }
            }
//            msg.data = bundle
//            myHandler.sendMessage(msg)
        }

        override fun onScanError(reason: String?, latency: Long) {
            super.onScanError(reason, latency)
            Log.e(TAG, "Reason $reason please wait for $latency ms")
//            if (mLoadingDialog != null) {
//                mLoadingDialog.dismiss()
//            }
        }

        override fun onScanFinish() {
            super.onScanFinish()
            Log.d(TAG, "onScanFinish()")
            DialogUtils.showSnackBarShort(this@MyVitalsActivity, "Scan Finished")

        }

        override fun onDeviceNotify(mac: String?, deviceType: String?, action: String?, message: String?) {
            super.onDeviceNotify(mac, deviceType, action, message)
            Log.d(TAG, "onDeviceNotify()")

            Log.i(TAG, "mac: $mac")
            Log.i(TAG, "deviceType: $deviceType")
            Log.i(TAG, "action: $action")
            Log.i(TAG, "message: $message")

            binding.tvReadingResult.visibility= View.VISIBLE
            binding.tvReadingResult.text= message

        }

        override fun onUserStatus(username: String?, userStatus: Int) {
            super.onUserStatus(username, userStatus)
            Log.i(TAG, "username: $username")
            Log.i(TAG, "userState: $userStatus")
        }

    }

    private val miHealthResultCallback: iHealthDevicesCallback = object : iHealthDevicesCallback() {

        override fun onDeviceConnectionStateChange(
                mac: String?,
                deviceType: String?,
                status: Int,
                errorID: Int,
                manufactorData: MutableMap<Any?, Any?>?
        ) {
            super.onDeviceConnectionStateChange(mac, deviceType, status, errorID, manufactorData)
            Log.e(
                    TAG,
                    "mac:$mac deviceType:$deviceType status:$status errorid:$errorID -manufactorData:$manufactorData"
            )
            val bundle = Bundle()
            bundle.putString("mac", mac)
            bundle.putString("type", deviceType)
            val msg = Message()

            when (status) {
                iHealthDevicesManager.DEVICE_STATE_DISCONNECTED -> {
//                    msg.what = HANDLER_DISCONNECT
                    DialogUtils.displayDialog(this@MyVitalsActivity, "Device has been Disconected")
                    this@MyVitalsActivity.runOnUiThread(Runnable {
                        binding.tvResult.text = "Device has been Disconected"
                        binding.btnTempStartDisc.isEnabled = true
                    })

                }

                iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL -> {
//                    msg.what = HANDLER_CONNECT_FAIL
                    DialogUtils.displayDialog(this@MyVitalsActivity, "Device Connection failed")
                    this@MyVitalsActivity.runOnUiThread(Runnable {
                        binding.tvResult.text = "Device Connection failed"
                        binding.btnTempStartDisc.isEnabled = true
                    })


                }
                iHealthDevicesManager.DEVICE_STATE_RECONNECTING -> {
//                    msg.what = HANDLER_RECONNECT
                    DialogUtils.showToast(applicationContext, "Device reconnecting")
                    this@MyVitalsActivity.runOnUiThread(Runnable {
                        binding.tvResult.text = "Device reconnecting"

                    })


                }
            }
//            msg.data = bundle
//            myHandler.sendMessage(msg)
        }

        override fun onScanError(reason: String?, latency: Long) {
            super.onScanError(reason, latency)
            Log.e(TAG, "Reason $reason please wait for $latency ms")
//            if (mLoadingDialog != null) {
//                mLoadingDialog.dismiss()
//            }
        }


        override fun onDeviceNotify(mac: String?, deviceType: String?, action: String?, message: String?) {
            super.onDeviceNotify(mac, deviceType, action, message)
            Log.d(TAG, "onDeviceNotify()")

            Log.i(TAG, "mac: $mac")
            Log.i(TAG, "deviceType: $deviceType")
            Log.i(TAG, "action: $action")
            Log.i(TAG, "message: $message")

            binding.tvReadingResult.visibility= View.VISIBLE
            binding.tvReadingResult.text= "Result: "+parseResult(message!!)

        }

        override fun onUserStatus(username: String?, userStatus: Int) {
            super.onUserStatus(username, userStatus)
            Log.i(TAG, "username: $username")
            Log.i(TAG, "userState: $userStatus")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId)

    }

    fun parseResult(result:String):String{
        try {
            val tempResultJson = JSONObject(result)
            return when (mDeviceName) {
                   THERMOMETER ->  tempResultJson.optString("result")
                else -> "Invalid Device"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error parsing result"
        }
    }



}