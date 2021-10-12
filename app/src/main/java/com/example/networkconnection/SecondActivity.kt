package com.example.networkconnection

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Network

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.wifi.ScanResult
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiConfiguration
import java.lang.Exception


class SecondActivity : AppCompatActivity() {

    private var manager: ConnectivityManager? = null
    var txt_connection: TextView? = null
    private var snackBar: Snackbar? = null
    var constraintLayout: ConstraintLayout?=null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        txt_connection=findViewById(R.id.tv_connection)
        constraintLayout=findViewById(R.id.cl_layout)


        manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager!!.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager!!.unregisterNetworkCallback(networkCallback)
    }


    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            val duration = Snackbar.LENGTH_SHORT
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = manager!!.activeNetworkInfo
            // to check the speed
           // if (manager!!.isActiveNetworkMetered) {
            if (manager!!.activeNetworkInfo!!.typeName.contains("WIFI")){

                showSnackbar(constraintLayout,"Connected to WIFI",duration)

                val wifi = getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager
                val networkList = wifi.scanResults
                val wi = wifi.connectionInfo
                //[type: WIFI[], state: CONNECTED/CONNECTED, reason: (unspecified), extra: "Suchi", failover: false, available: true, roaming: false]
                val currentSSID = wi.ssid
                //SSID: , BSSID: 02:00:00:00:00:00, MAC: 02:00:00:00:00:00, Supplicant state: COMPLETED, RSSI: -35, Link speed: 150Mbps, Frequency: 5745MHz, Net ID: 8, Metered hint: true, score: 60
                if (networkList != null) {
                    for (network in networkList) {
                        //check if current connected SSID
                        if (currentSSID == network.SSID) {
                            //get capabilities of current connection
                            val capabilities = network.capabilities
                            Log.i("vvv-secure", network.SSID + " capabilities : " + capabilities)
                            if (capabilities.contains("WPA")) {
                                //do something
                                Log.i("vvv-","WPA")

                            } else if (capabilities.contains("WEP")) {
                                //do something
                                Log.i("vvv-","WEP")

                            }
                            else{
                                Log.i("vvv-","open")
                            }
                        }
                    }
                }

            }
            else{
                Log.i("vvv-speed", netInfo!!.getType().toString())
                Log.i("vvv-speed", netInfo!!.getSubtype().toString())
            //    isConnectionFast(netInfo!!.getType(),netInfo!!.getSubtype())
                showSnackbar(constraintLayout,"Connected to LTE/MOBILE",duration)


            }

            Log.i("vvv", "connected to " + if (manager!!.activeNetworkInfo!!.typeName.contains("WIFI")) "WIFI"
            else "MOBILE")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            val duration = Snackbar.LENGTH_SHORT
            showSnackbar(constraintLayout,"No Internet Connection",duration)

            Log.i("vvv", "losing active connection")
        }
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

    @SuppressLint("MissingPermission")
    fun connectWiFi(scanResult: ScanResult) {
        try {

            Log.v(
                "rht",
                "Item clicked, SSID " + scanResult.SSID + " Security : " + scanResult.capabilities
            )
            val networkSSID = scanResult.SSID
            val networkPass = "12345678"
            val conf = WifiConfiguration()
            conf.SSID =
                "\"" + networkSSID + "\"" // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED
            conf.priority = 40
            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP")
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                val pattern1 = Regex("^[0-9a-fA-F]+\$\"")
                if (networkPass.matches(pattern1)) {
                    conf.wepKeys[0] = networkPass
                } else {
                    conf.wepKeys[0] = "\"" + networkPass + "\""
                }
                conf.wepTxKeyIndex = 0
            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA")
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                conf.preSharedKey = "\"" + networkPass + "\""
            } else {
                Log.v("rht", "Configuring OPEN network")
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                conf.allowedAuthAlgorithms.clear()
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            }
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val networkId = wifiManager.addNetwork(conf)
            Log.v("rht", "Add result $networkId")
            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + networkSSID + "\"") {
                    Log.v("rht", "WifiConfiguration SSID " + i.SSID)
                    val isDisconnected = wifiManager.disconnect()
                    Log.v("rht", "isDisconnected : $isDisconnected")
                    val isEnabled = wifiManager.enableNetwork(i.networkId, true)
                    Log.v("rht", "isEnabled : $isEnabled")
                    val isReconnected = wifiManager.reconnect()
                    Log.v("rht", "isReconnected : $isReconnected")
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}