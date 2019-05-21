package com.gmail.vanyadubik.freeride.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.gmail.vanyadubik.freeride.activity.map.MapsActivity
import com.gmail.vanyadubik.freeride.common.Consts
import com.gmail.vanyadubik.freeride.utils.SharedStorage

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(TextUtils.isEmpty(SharedStorage.getString(this, Consts.TOKEN, ""))){
            SharedStorage.setString(this, Consts.TOKEN,
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
        }

        startActivity(Intent(this@SplashActivity, MapsActivity::class.java))
        Handler().postDelayed({ finish() }, 2000)

    }
}
