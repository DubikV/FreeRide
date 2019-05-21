package com.gmail.vanyadubik.freeride.activity.map


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.activity.login.LoginActivity
import com.gmail.vanyadubik.freeride.adapter.ResultSearchAdapter
import com.gmail.vanyadubik.freeride.adapter.ReviewListAdapter
import com.gmail.vanyadubik.freeride.common.Consts.EVALUATION_AVAILABLE
import com.gmail.vanyadubik.freeride.common.Consts.EVALUATION_INACCESSIBLE
import com.gmail.vanyadubik.freeride.common.Consts.EVALUATION_TROUBLESOME
import com.gmail.vanyadubik.freeride.model.dto.NewReviewRequest
import com.gmail.vanyadubik.freeride.model.dto.Poi
import com.gmail.vanyadubik.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubik.freeride.model.dto.PoiReview
import com.gmail.vanyadubik.freeride.utils.ActivityUtils
import com.gmail.vanyadubik.freeride.utils.AnimUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.map_bottom_panel.*
import kotlinx.android.synthetic.main.toolbar_panel.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class MapsActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, OnMapReadyCallback,
    GPSTrackerPresenter.LocationListener, GoogleMap.OnCameraIdleListener, MapsMVPContract.View {

    companion object {
        const val GPS_REQUEST_CODE = 999
        const val LOGIN_REQUEST = 998
    }
    private val LOCATION_PERMISSIONS_REQUEST = 5
    private val MAP_ZOOM_DEFAULT = 16.5F

    private lateinit var mMap: GoogleMap
    private lateinit var searchOption: MenuItem
    private lateinit var mapsRepository: MapsRepository
    private lateinit var gpsTrackerPresenter: GPSTrackerPresenter
    private lateinit var adapterResult: ResultSearchAdapter
    private lateinit var poiBottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>
    private lateinit var reviewListAdapter: ReviewListAdapter

    private var currentLocation: Location? = null
    private var selectedPoi: Poi? = null
    private var isSearchContainerVisible = false
    private var isRouteContainerVisible = false

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
                poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }else{
                showSearchContainer()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSIONS_REQUEST) {
//            mMap.isMyLocationEnabled = true
//            mMap.uiSettings.isMapToolbarEnabled = false
//            mMap.uiSettings.isZoomControlsEnabled = false
//            mMap.uiSettings.isMyLocationButtonEnabled = false
//        }

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        val permission = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.ACCESS_FINE_LOCATION)

        if (checkRequiredLocationPermission()) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMapToolbarEnabled = false
            mMap.uiSettings.isZoomControlsEnabled = false
            mMap.uiSettings.isMyLocationButtonEnabled = false
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSIONS_REQUEST
            )
        }

        mMap.animateCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM_DEFAULT))
        mMap.setOnCameraIdleListener(this)

    }

    override fun onLocationFound(location: Location?) {
        gpsTrackerPresenter.stopLocationUpdates()
        currentLocation = location
        mMap.clear()
        animateToCurrentLocation()
    }


    private fun animateToCurrentLocation() {
        if (currentLocation != null) {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        currentLocation!!.latitude,
                        currentLocation!!.longitude
                    ), MAP_ZOOM_DEFAULT
                )
            )
        }
    }

    override fun checkRequiredLocationPermission(): Boolean {
        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this, getString(R.string.google_map_permission),
                GPSTrackerPresenter.RUN_TIME_PERMISSION_CODE, *perms
            )
            return false
        } else {
            return true
        }
    }


    override fun onCameraIdle() {

//        if (currentLocation == null) {
//            currentLocation = Location("")
//        }
//        currentLocation!!.latitude = mMap.cameraPosition.target.latitude
//        currentLocation!!.longitude = mMap.cameraPosition.target.longitude
//
//        val markerLocation = Location("")
//        markerLocation.latitude = mGoogleMap.cameraPosition.target.latitude
//        markerLocation.longitude = mGoogleMap.cameraPosition.target.longitude
//        startIntentService(markerLocation)

    }

    private fun clearCameraMoveListener() {
        mMap.setOnCameraMoveListener(null)
    }


    override fun locationError(msg: String) {
        ActivityUtils.showShortToast(this, getString(R.string.error_location))
    }

    override fun onStart() {
        super.onStart()
        gpsTrackerPresenter.onStart()
    }

    override fun onDestroy() {
        gpsTrackerPresenter.onPause()
        super.onDestroy()
    }

    private fun onMyLocationClick() {
        clearCameraMoveListener()
        gpsTrackerPresenter.onStart()
    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        gpsTrackerPresenter.onStart()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSIONS_REQUEST
        )
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
        if(poiBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
            poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        super.onBackPressed()
    }


    override fun onShowPoiList(list: List<Poi>) {
        progressBar.hideAnimation()
        adapterResult.setList(list)
    }

    override fun onErrorApi(textError: String) {
        progressBar.hideAnimation()
        ActivityUtils.showMessage(this, "", null, textError)
    }

    override fun onStartLoadSearch() {
        progressBar.showAnimation()
    }

    override fun onStartLoadDetail() {
        poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        showContainer(false)
    }

    override fun onShowPoiDetail(poiDetailed: PoiDetailed) {
        showRouteContainer()
        showPoiDetailContainer(poiDetailed)
        selectedPoi?.id?.let { mapsRepository.getReviews(it, 0, 20) }
        reviewListAdapter.clear()
    }

    override fun onAddReview() {
        ActivityUtils.showMessage(this,
                getString(R.string.successfully),
                resources.getDrawable(R.drawable.ic_check_white),
                getString(R.string.rating_added))
    }

    override fun onShowListReviews(list: List<PoiReview>) {
        if(list.isNotEmpty() || reviewListAdapter.itemCount>0){
            listReview.visibility = View.VISIBLE
            listReviewEmpty.visibility = View.GONE
            reviewListAdapter.addist(list.toMutableList())
        }else{
            listReview.visibility = View.GONE
            listReviewEmpty.visibility = View.VISIBLE
        }
        listReviewProgressBar.visibility = View.GONE
    }

    override fun onStartLoadListReview() {
        listReviewEmpty.visibility = View.GONE
        listReviewProgressBar.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                LOGIN_REQUEST -> {}
                GPS_REQUEST_CODE -> {
                    gpsTrackerPresenter.onStart()
                }
            }
        }
    }

    private fun initStartData() {

        progressBar.hideAnimation()

        gpsTrackerPresenter = GPSTrackerPresenter(this, this)

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

        poiBottomSheetBehavior = BottomSheetBehavior.from<CoordinatorLayout>(bottomSheet)
        poiBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        reviewListAdapter = ReviewListAdapter(this, object : ReviewListAdapter.ClickListener{
            override fun onItemClick(position: Int) {
            }

            override fun onClickUpperText(position: Int) {
            }

        })
        listReview.layoutManager = LinearLayoutManager(this)
        listReview.adapter = reviewListAdapter
    }

    private fun initClicklisteners() {
        logoImage.setOnClickListener {
            startActivityForResult(Intent(this, LoginActivity::class.java), LOGIN_REQUEST)
        }
        clearSearchImage.setOnClickListener {
            progressBar.hideAnimation()
            adapterResult.clear()
            if(searchET.text.isEmpty()){
                hideSearchContainer();
            }else{
                searchET.text.clear()
            }
            poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        btnTraffic.setOnClickListener {
            if (mMap != null && btnTraffic != null) {
                mMap.isTrafficEnabled = !mMap.isTrafficEnabled
                btnTraffic.setImageResource(
                        if (mMap.isTrafficEnabled) R.drawable.ic_traffic_off else R.drawable.ic_traffic_on)
            }
        }

        btnMyLocation.setOnClickListener {

            onMyLocationClick()
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
        ActivityUtils.hideKeyboard(this, searchET)
    }

    private fun showContainer(show: Boolean) {
        if (show) {
            bottomSheetContent.visibility = View.VISIBLE
            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
        } else {
            bottomSheetContent.visibility = View.GONE
            shimmerViewContainer.startShimmer()
            shimmerViewContainer.visibility = View.VISIBLE
        }
    }

    private fun showPoiDetailContainer(poiDet : PoiDetailed){

        when (poiDet.accessible) {
            EVALUATION_AVAILABLE -> {
                statusPoi.setTextColor(resources.getColor(R.color.weirdGreen))
                statusPoi.text = getString(R.string.available) + " " + getString(R.string.object_text)
            }
            EVALUATION_TROUBLESOME -> {
                statusPoi.setTextColor(resources.getColor(R.color.orangeYellow))
                statusPoi.text = getString(R.string.troublesome) + " " + getString(R.string.object_text)
            }
            EVALUATION_INACCESSIBLE -> {
                statusPoi.setTextColor(resources.getColor(R.color.coral))
                statusPoi.text = getString(R.string.inaccessible) + " " + getString(R.string.object_text)
            }
        }
        ratingsPoi.text = if(poiDet.reviews>0) getString(R.string.ratings) + " " + poiDet.reviews else getString(R.string.ratings_not_have)
        namePoi.text = poiDet.name
        typePoi.text = poiDet.types?.joinToString(separator = ", ")
        addressPoi.text = poiDet.address
        workinghoursPoi.text = poiDet.workinghours?.joinToString(separator = ", \n")

        showContainer(true)

    }

    private fun showAddReview(v: View) {

//        var popupWindow: PopupWindow? = null

        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val selectedView = layoutInflater.inflate(R.layout.layout_review, null)


//        if (popupWindow != null) {
//            if (popupWindow.isShowing()) {
//                popupWindow.dismiss()
//            }
//        }

        var popupWindow = PopupWindow(selectedView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        val available = selectedView.findViewById<LinearLayout>(R.id.available)
        available.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            showCommentReview(v, EVALUATION_AVAILABLE)
        }

        val troublesome = selectedView.findViewById<LinearLayout>(R.id.troublesome)
        troublesome.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            showCommentReview(v, EVALUATION_TROUBLESOME)
        }

        val inaccessible = selectedView.findViewById<LinearLayout>(R.id.inaccessible)
        inaccessible.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            showCommentReview(v, EVALUATION_INACCESSIBLE)
        }

        val rateLater = selectedView.findViewById<TextView>(R.id.rateLater)
        rateLater.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
        }

        popupWindow.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTrans)))
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.update()
        popupWindow.setOnDismissListener {
            if (popupWindow != null) {
                if (popupWindow.isShowing) {
                    AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
                }
            }
        }


        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        AnimUtils.enterAnimation(v, selectedView)

    }

    private fun showCommentReview(v: View, status: Int) {

        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val selectedView = layoutInflater.inflate(R.layout.layout_review_comment, null)

        var popupWindow = PopupWindow(selectedView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        popupWindow.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTrans)))
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.update()
        popupWindow.setOnDismissListener {
            if (popupWindow != null) {
                if (popupWindow.isShowing) {
                    AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
                }
            }
        }

        val comment = selectedView.findViewById<TextView>(R.id.comment)

        val rateCommentLater = selectedView.findViewById<TextView>(R.id.rateCommentLater)
        rateCommentLater.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            selectedPoi?.id?.let {
                it1 -> mapsRepository.addReview(it1, NewReviewRequest(status, comment.text.toString(), Calendar.getInstance().timeInMillis))
            }
        }

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        AnimUtils.enterAnimation(v, selectedView)

    }

}
