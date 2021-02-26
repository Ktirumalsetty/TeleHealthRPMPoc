package com.primehealthcare.tele

import android.app.Application
import android.util.Log
import com.ihealth.communication.manager.iHealthDevicesManager

/**
 * Created by KondalRao Tirumalasetty on 12/1/2020.
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        /*
* Initializes the iHealth devices manager. Can discovery available iHealth devices nearby
* and connect these devices through iHealthDevicesManager. */
        iHealthDevicesManager.getInstance().init(this, Log.VERBOSE, Log.WARN);


    }

}