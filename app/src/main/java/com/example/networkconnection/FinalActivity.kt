package com.example.networkconnection

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar

class FinalActivity : AppCompatActivity() {

    private var manager: ConnectivityManager? = null
    var txt_connection: TextView? = null
    private var snackBar: Snackbar? = null
    var constraintLayout: ConstraintLayout?=null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)


        txt_connection=findViewById(R.id.tv_connection)
        constraintLayout=findViewById(R.id.cl_layout)

        manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager!!.registerDefaultNetworkCallback(networkCallback)

    }

    override fun onDestroy() {
        super.onDestroy()
        manager!!.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        @SuppressLint("NewApi")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            val duration = Snackbar.LENGTH_SHORT
            val netInfo = manager!!.activeNetworkInfo

            if (manager!!.activeNetworkInfo!!.typeName.contains("WIFI")){

                showSnackbar(constraintLayout,"Connected to WIFI",duration)
                val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                val wi = wifi.connectionInfo
                val currentSSID = wi.ssid
                scanWifi(applicationContext,currentSSID)
            }
            else{
                Log.i("WIFI-speed", netInfo!!.getType().toString())
                Log.i("WIFI-speed", netInfo!!.getSubtype().toString())
                //    isConnectionFast(netInfo!!.getType(),netInfo!!.getSubtype())

                showSnackbar(constraintLayout,"Connected to MOBILE",duration)

            }

            Log.i("WIFI", "connected to " + if (manager!!.activeNetworkInfo!!.typeName.contains("WIFI")) "WIFI"
            else "MOBILE")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            val duration = Snackbar.LENGTH_SHORT
            showSnackbar(constraintLayout,"No Internet Connection",duration)

            Log.i("WIFI- Network Lost", "losing active connection")
        }

    }

    fun scanWifi(context: Context, networkSSID: String) {

        Log.i("WIFI Starts...", "ScanWifi starts")

        val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
        val scanList = wifiManager.scanResults

        for (i in scanList) {

            if (i.SSID != null) {

                Log.i("WIFI - SSID List", "SSID: " + i.SSID)
                Log.i("WIFI - SSID First", "SSID: " + i.SSID[0].toString())

                Log.i("WIFI - SSID List", "SSID: " + i.capabilities[0])
                val duration = Snackbar.LENGTH_SHORT


                if(i.capabilities[0]!!.toString().contains("WAP")||i.capabilities[0]!!.toString().contains("WEP")){

                    showSnackbar(constraintLayout,"Open Network",duration)

                    //forcelogout

                }

                else{

                    showSnackbar(constraintLayout,"Secure Network",duration)

                }


            }
            if (i.SSID != null && i.SSID.equals(networkSSID)) {
                Log.e("WIFI - SSID", "Found SSID: " + i.SSID)
            }
        }
        Log.i("WIFI", "SSID $networkSSID Not Found")

    }

    fun showSnackbar(view: View?, message: String?, duration: Int) {

        snackBar= Snackbar.make(view!!, message!!, duration)
        snackBar!!.show()

    }


    fun isConnectionFast(type: Int, subType: Int): Boolean {
        return if (type == ConnectivityManager.TYPE_WIFI) {
            true
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
                TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
                TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
                TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
                TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
                TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
                TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
                TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
                TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
                TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
                TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
                else -> false
            }
        } else {
            false
        }
    }
}