package com.gmail.vanyadubyk.freeride.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.gmail.vanyadubyk.freeride.activity.map.MapsActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this@SplashActivity, MapsActivity::class.java))
        Handler().postDelayed({ finish() }, 2000)

    }
}
