package com.acs.accelerate

import android.content.Context
import android.net.*
import android.util.Log

class InternetUtils {

    companion object {
        private const val TAG = "InternetUtils"

        fun isConnected(context: Context, onDone: (Boolean, Boolean) -> Unit) {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var isWifiConn: Boolean = false
            var isMobileConn: Boolean = false
            connMgr.allNetworks.forEach { network ->
                connMgr.getNetworkInfo(network).apply {
                    if (this?.type == ConnectivityManager.TYPE_WIFI) {
                        isWifiConn = isWifiConn or isConnected
                    }
                    if (this?.type == ConnectivityManager.TYPE_MOBILE) {
                        isMobileConn = isMobileConn or isConnected
                    }
                }
            }

            onDone(isWifiConn, isMobileConn)
        }

        fun isConnected(context: Context): Boolean {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
            return networkInfo?.isConnected == true
        }

        fun listen(
            context: Context,
            onCapabilitiesChanged: ((NetworkCapabilities, Network?) -> Unit)? = null,
            onLinkPropertiesChanged: ((LinkProperties, Network?) -> Unit)? = null,
            onConnectivityChanged: (Boolean, Network?) -> Unit
        ) {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connMgr.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    onConnectivityChanged(true, network)
                }

                override fun onLost(network: Network) {
                    onConnectivityChanged(false, network)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    if (onCapabilitiesChanged != null) {
                        onCapabilitiesChanged(networkCapabilities, network)
                    }
                }

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    if (onLinkPropertiesChanged != null) {
                        onLinkPropertiesChanged(linkProperties, network)
                    }
                }
            })
        }
    }
}


/*
Usage:
    private fun testInternet() {
        val status =  InternetUtils.isConnected(this)
        Log.e("MainActivity", "Internet availability: $status")

        InternetUtils.isConnected(this) { isWifi, isMobile ->
            Log.e("MainActivity", "Internet availability- wifi: $isWifi, mobile: $isMobile")
        }

        InternetUtils.listen(this) { isConnected, network ->
            Log.e("MainActivity", "Internet - isConnected: $isConnected")
        }
    }
 */