package com.gmail.vanyadubik.freeride.activity


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.adapter.ResultSearchAdapter
import com.gmail.vanyadubik.freeride.common.Consts.TOKEN
import com.gmail.vanyadubik.freeride.model.dto.Poi
import com.gmail.vanyadubik.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubik.freeride.utils.ActivityUtils
import com.gmail.vanyadubik.freeride.utils.AnimUtils
import com.gmail.vanyadubik.freeride.utils.SharedStorage
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.toolbar_panel.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsMVPContract.View{

    private lateinit var map: GoogleMap
    private var isSearchContainerVisible = false
    private var isRouteContainerVisible = false
    private lateinit var searchOption: MenuItem


    private lateinit var mapsRepository: MapsRepository
    private lateinit var adapterResult: ResultSearchAdapter
    private var selectedPoi: Poi? = null

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
            if(selectedPoi!=null){
                selectedPoi = null
                hideRouteContainer()
                logoImage.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                searchResult.text = ""
            }else{
                showSearchContainer()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false
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

    override fun onShowPoiList(list: List<Poi>) {
        progressBar.hideAnimation()
        adapterResult.setList(list)
    }

    override fun onErrorApi(textError: String) {
        progressBar.hideAnimation()
    }

    override fun onStartLoad() {
        progressBar.showAnimation()
    }

    override fun onShowPoiDetail(poiDetailed: PoiDetailed) {
        progressBar.hideAnimation()
        showRouteContainer()
    }

    private fun initStartData() {

        progressBar.hideAnimation()

        mapsRepository = MapsRepository(this, this)

        searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0?.length!! >3){
                    mapsRepository.getPoiByName(p0.toString())
                }
            }
        })

        if(SharedStorage.getString(this, TOKEN, "")?.isEmpty()!!){
            SharedStorage.setString(this, TOKEN, UUID.randomUUID().toString())
        }

        adapterResult = ResultSearchAdapter(this, object : ResultSearchAdapter.ClickListener{
            override fun onItemClick(position: Int) {
                selectedPoi = adapterResult.getSelectedItem(position)
                onSelectedSearchPoi()
            }

            override fun onClickUpperText(position: Int) {
                searchET.setText(adapterResult.getSelectedItem(position)?.name)
            }

        })
        searchResultRV.layoutManager = LinearLayoutManager(this)
        searchResultRV.adapter = adapterResult
    }

    private fun initClicklisteners() {
        clearSearchImage.setOnClickListener {
            adapterResult.clear()
            if(searchET.text.isEmpty()){
                hideSearchContainer();
            }else{
                searchET.text.clear()
            }
        }

        btnTraffic.setOnClickListener {
            
        }

        btnMyLocation.setOnClickListener {

        }

        reviewBtn.setOnClickListener {
            showAddReview(reviewBtn)
        }
    }

    private fun showSearchContainer() {
        AnimUtils.expand(searchContainer)
        isSearchContainerVisible = true
        searchOption.isVisible = false
    }

    private fun hideSearchContainer() {
        AnimUtils.collapse(searchContainer)
        isSearchContainerVisible = false
        searchOption.isVisible = true
        searchOption.setIcon(R.drawable.ic_search)
    }

    private fun showRouteContainer() {
        AnimUtils.expand(routeContainer)
        isRouteContainerVisible = true
        searchOption.setIcon(R.drawable.ic_clear)
    }

    private fun hideRouteContainer() {
        AnimUtils.collapse(routeContainer)
        isRouteContainerVisible = false
        searchOption.setIcon(R.drawable.ic_search)
    }

    private fun onSelectedSearchPoi() {
        if (isSearchContainerVisible) {
            hideSearchContainer()
        }
        selectedPoi?.id?.let { mapsRepository.getPoiDetail(it) }
        adapterResult.clear()

        logoImage.visibility = View.GONE
        progressBar.visibility = View.GONE
        searchResult.text = selectedPoi?.name
        searchET.setText("")
        searchOption.setIcon(R.drawable.ic_clear)
        showRouteContainer()
    }

    private fun showAddReview(v: View) {

        var popupWindow: PopupWindow? = null

        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val selectedView = layoutInflater.inflate(R.layout.layout_review, null)


        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss()
            }
        }

        popupWindow = PopupWindow(selectedView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        popupWindow.setBackgroundDrawable(ColorDrawable(getResources().getColor(R.color.colorSearchBackground)))
        popupWindow.setFocusable(true)
        popupWindow.setOutsideTouchable(true)
        popupWindow.update()
        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            if (popupWindow != null) {
                if (popupWindow.isShowing()) {
                    AnimUtils.exitAnimation(v, Runnable { popupWindow.dismiss() })
                }
            }
        })


        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        AnimUtils.enterAnimation(v, selectedView)


//        val builder = AlertDialog.Builder(this, R.style.WhiteDialogTheme)
//        val viewInflated = LayoutInflater.from(this).inflate(R.layout.layout_review, null)
//
//        builder.setView(viewInflated)
//
//
//
//        builder.setCancelable(true)
//        val alertQuestion: AlertDialog = builder.create()
//
//        alertQuestion.show()
       // alertQuestion!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

}
