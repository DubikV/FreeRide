package com.gmail.vanyadubik.freeride.activity.login


import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.activity.map.MapsMVPContract
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.toolbar_panel.*

class LoginActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_menu)

    }

}
