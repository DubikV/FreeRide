package com.gmail.vanyadubik.freeride.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.utils.AnimUtils
import com.gmail.vanyadubik.freeride.utils.PermissionUtils.requestPermission
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment


import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.toolbar_panel.*
import kotlinx.android.synthetic.main.bottom_sheet.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var map: GoogleMap
    private var isSearchContainerVisible = false
    private var isRouteContainerVisible = false
    private lateinit var searchOption: MenuItem


    private lateinit var selectedAddress: Address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        setSupportActionBar(toolbar)
        initClicklisteners()
        initStartData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.loc_picker_menu, menu)
        searchOption = menu.findItem(R.id.action_search)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {
         //   if (selectedAddress == null) {
                showSearchContainer()
//            } else {
//                hideRouteContainer()
//            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            map?.isMyLocationEnabled = true
            map?.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    override fun onBackPressed() {
        if (isSearchContainerVisible) {
            hideSearchContainer()
            return
        }
        if (isRouteContainerVisible) {
            hideRouteContainer()
            return
        }
        super.onBackPressed()
    }

    private fun initStartData() {

        progressBar.hideAnimation()

        searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0?.length!! >3){
                    progressBar.showAnimation()
                }
            }
        })
    }

    private fun initClicklisteners() {
        clearSearchImage.setOnClickListener {
            if(searchET.text.isEmpty()){
                hideSearchContainer();
            }else{
                searchET.text.clear()
            }
        }
    }

    private fun showSearchContainer() {
        AnimUtils.expand(searchContainer)
        isSearchContainerVisible = true
        if (searchOption != null) {
            searchOption.isVisible = false
        }
    }

    private fun hideSearchContainer() {
        AnimUtils.collapse(searchContainer)
        isSearchContainerVisible = false
        if (searchOption != null) {
            searchOption.isVisible = true
            searchOption.setIcon(R.drawable.ic_search)
        }
    }

    private fun hideRouteContainer() {
        AnimUtils.collapse(routeContainer)
        isRouteContainerVisible = false
        if (searchOption != null) {
            searchOption.setIcon(R.drawable.ic_search)
        }
    }

}
