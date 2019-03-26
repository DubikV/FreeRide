package com.gmail.vanyadubik.freeride.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo

import java.util.regex.Matcher
import java.util.regex.Pattern

object NetworkUtils {

    fun checkEthernet(context: Context): Boolean {

        val conMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = conMgr.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnected
    }

    fun WIFISwitch(context: Context): Boolean {

        val conMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        return activeNetwork != null && activeNetwork.isConnected
    }

    fun checkUSBconnection(context: Context): Boolean {

        val intent = context.registerReceiver(null, IntentFilter("android.hardware.usb.action.USB_STATE"))
        return intent!!.extras!!.getBoolean("connected")

    }

    fun checkIsIP(adress: String): Boolean {
        val p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
        val m = p.matcher(adress)
        return m.find()
    }
}
