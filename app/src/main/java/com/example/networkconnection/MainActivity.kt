package com.example.networkconnection

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.lang.NullPointerException

class MainActivity : AppCompatActivity(),ConnectivityReceiver.ConnectivityReceiverListener {

    var txt_connection:TextView? = null
    private var snackBar: Snackbar? = null
    var constraintLayout:ConstraintLayout?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt_connection=findViewById(R.id.tv_connection)
        constraintLayout=findViewById(R.id.cl_layout)

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }


    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }
    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            val message = "You are in Offline"
            val duration = Snackbar.LENGTH_SHORT
            showSnackbar(constraintLayout, message, duration)
        } else {
            snackBar!!.dismiss()
        }
    }

    fun showSnackbar(view: View?, message: String?, duration: Int) {

        snackBar=Snackbar.make(view!!, message!!, duration)
        snackBar!!.show();

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        TODO("Not yet implemented")
        showNetworkMessage(isConnected)

    }

}