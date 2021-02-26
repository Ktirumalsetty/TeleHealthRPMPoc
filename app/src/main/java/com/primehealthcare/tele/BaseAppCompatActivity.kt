package com.primehealthcare.tele

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar


/**
 * Created by KondalRao Tirumalasetty on 8/6/2019.
 */
abstract class BaseAppCompatActivity<DB: ViewDataBinding> :AppCompatActivity(){

   // protected var TAG: String? = null

    open val BASE_TAG ="BaseAppCompatActivity"

    protected lateinit var binding:DB

    private var mSnackBar: Snackbar? = null

//    var connectivityReceiver:ConnectivityReceiver = ConnectivityReceiver()

//    private var connectivityManager: ConnectivityManager?=null

    abstract fun getLayoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BASE_TAG,"onCreate")
     //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
      //  TAG = BaseAppCompatActivity::class.qualifiedName
        bindView(getLayoutRes())
//        connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//        connectivityReceiver = ConnectivityReceiver()


    }

    override fun onStart() {
        super.onStart()

    }

    inline fun <reified T> T.TAG(): String = T::class.java.simpleName

    protected fun bindView(layoutId: Int) {
        binding = DataBindingUtil.setContentView(this, layoutId)

    }

    override fun onPause() {
        super.onPause()
        Log.d(BASE_TAG,"onPause")

    }
    override fun onResume() {
        super.onResume()
        Log.d(BASE_TAG,"onResume")
//        registerReceiver(connectivityReceiver,  IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

//        ConnectivityReceiver.connectivityReceiverListener = this

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)

    }

//    fun getComponent():TelehealthApplication {
//
//        return (application as TelehealthApplication).myAppComponent
//    }

//    override fun onNetworkConnectionChanged(isConnected: Boolean) {
//        Log.d(BASE_TAG, "onNetworkConnectionChanged")
//        showMessage(isConnected)
//    }

    protected fun showMessage(isConnected: Boolean) {
        if (!isConnected) {

            val messageToUser = "You are offline now."

            mSnackBar = Snackbar.make(
                findViewById(android.R.id.content),
//                findViewById(com.google.android.material.R.id.snackbar_text),
                messageToUser,
                Snackbar.LENGTH_INDEFINITE
            ) //Assume "rootLayout" as the root layout of every activity.

            mSnackBar?.show()
            Toast.makeText(this, messageToUser, Toast.LENGTH_LONG).show()
        } else {
            val messageToUser = "You are online now."
            if (mSnackBar !=null && mSnackBar!!.isShown){
                val snackBarText = mSnackBar!!.getView().findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                snackBarText.setText(messageToUser)
                snackBarText.setTextColor(ContextCompat.getColor(this,android.R.color.white))
                snackBarText.setBackgroundColor(ContextCompat.getColor(this,android.R.color.holo_green_dark));

                Handler().postDelayed({

                    mSnackBar?.dismiss()
                },2000)
                Toast.makeText(this, messageToUser, Toast.LENGTH_LONG).show()
            }


        }
    }

    protected fun showNoNetworkMsg(){
        Toast.makeText(this, "No Network..", Toast.LENGTH_LONG).show()
    }

    protected fun showErrorRespMsg(){
        Toast.makeText(this, "Something went wrong..", Toast.LENGTH_LONG).show()
    }

    fun showProgressBar(view:FrameLayout){
        view.visibility = View.VISIBLE
    }

    fun hideProgressBar(view:FrameLayout){
        view.visibility = View.VISIBLE
    }

//    override fun onUserLeaveHint() {
//        super.onUserLeaveHint()
//        Log.d(BASE_TAG,"onUserLeaveHint")
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            // only for Q and newer versions
//            SignalRHubCon.stopSignalR()
//        }
//
//    }
}