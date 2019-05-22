package com.gmail.vanyadubyk.freeride.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.gmail.vanyadubyk.freeride.R
import com.gmail.vanyadubyk.freeride.common.Consts.TAGLOG_SYNC
import kotlinx.android.synthetic.main.search_result_item.view.*
import retrofit2.HttpException

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

    fun getConnectExeption(mContext : Context, e: Throwable, errorDefault: String): String {

        try {
            when ((e as HttpException).code()) {
                400 -> return mContext.getString(R.string.error_400)
                401 -> return mContext.getString(R.string.error_401)
                404 -> return mContext.getString(R.string.error_404)
                500 -> return mContext.getString(R.string.error_500)
                else -> {
                    return errorDefault
                }
            }
        } catch (exep: Exception) {
            Log.e(TAGLOG_SYNC, exep.message)
        }

        return errorDefault
    }

    fun checkIsIP(adress: String): Boolean {
        val p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
        val m = p.matcher(adress)
        return m.find()
    }
}
