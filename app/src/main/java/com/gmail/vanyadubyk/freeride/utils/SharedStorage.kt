package com.gmail.vanyadubyk.freeride.utils

import android.content.Context
import android.content.SharedPreferences.Editor

object SharedStorage {
    val APP_PREFS = "FreeRidePrefs"

    fun getString(context: Context, key: String, defValue: String?): String? {
        var result: String? = null
        try {
            result = context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).getString(key, defValue)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        return result
    }

    fun getBoolean(context: Context, key: String, defValue: Boolean): Boolean {
        return context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).getBoolean(key, defValue)
    }

    fun getInteger(context: Context, key: String, defValue: Int): Int {
        return context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).getInt(key, defValue)
    }

    fun getLong(context: Context, key: String, defValue: Long): Long {
        return context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).getLong(key, defValue)
    }

    fun getDouble(context: Context, key: String, defValue: Double): Double {
        return java.lang.Double.longBitsToDouble(getLong(context, key, java.lang.Double.doubleToLongBits(defValue)))
    }

    fun setString(context: Context, key: String, value: String?) {
        val editor = context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        val editor = context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun setInteger(context: Context, key: String, value: Int) {
        val editor = context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun setLong(context: Context, key: String, value: Long) {
        val editor = context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun setDouble(context: Context, key: String, value: Double) {
        val editor = context.getSharedPreferences(APP_PREFS, Context.MODE_MULTI_PROCESS).edit()
        editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
        editor.commit()
    }
}
