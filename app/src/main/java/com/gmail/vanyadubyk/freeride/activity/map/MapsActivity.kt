package com.gmail.vanyadubyk.freeride.activity.map


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.gmail.vanyadubyk.freeride.R
import com.gmail.vanyadubyk.freeride.activity.login.LoginActivity
import com.gmail.vanyadubyk.freeride.adapter.ResultSearchAdapter
import com.gmail.vanyadubyk.freeride.adapter.ReviewListAdapter
import com.gmail.vanyadubyk.freeride.common.Consts
import com.gmail.vanyadubyk.freeride.common.Consts.EVALUATION_AVAILABLE
import com.gmail.vanyadubyk.freeride.common.Consts.EVALUATION_INACCESSIBLE
import com.gmail.vanyadubyk.freeride.common.Consts.EVALUATION_TROUBLESOME
import com.gmail.vanyadubyk.freeride.model.dto.NewReviewRequest
import com.gmail.vanyadubyk.freeride.model.dto.Poi
import com.gmail.vanyadubyk.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubyk.freeride.model.dto.PoiReview
import com.gmail.vanyadubyk.freeride.utils.ActivityUtils
import com.gmail.vanyadubyk.freeride.utils.AnimUtils
import com.gmail.vanyadubyk.freeride.utils.DrawableUtils
import com.gmail.vanyadubyk.freeride.utils.SharedStorage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.map_bottom_panel.*
import kotlinx.android.synthetic.main.toolbar_panel.*
import org.xml.sax.Parser
import pub.devrel.easypermissions.EasyPermissions
import java.net.URL
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
    private var foundedLocation: LatLng? = null
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
                mMap?.clear()
                onMyLocationClick()
                foundedLocation = null
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

        btnMyLocationContainer.visibility = View.VISIBLE
        btnTrafficContainer.visibility = View.VISIBLE

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
                LOGIN_REQUEST -> {
                    initDataToView()
                }
                GPS_REQUEST_CODE -> {
                    gpsTrackerPresenter.onStart()
                }
            }
        }
    }

    private fun initStartData() {

        progressBar.hideAnimation()

        initDataToView()

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

        adapterResult = ResultSearchAdapter(
            this,
            object : ResultSearchAdapter.ClickListener {
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

        reviewListAdapter = ReviewListAdapter(
            this,
            object : ReviewListAdapter.ClickListener {
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
                hideSearchContainer()
                mMap?.clear()
                onMyLocationClick()
                foundedLocation = null
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

        routeBtn.setOnClickListener {
            showNavigationRoute()
        }

    }
    private fun initDataToView(){
        val imagePath = SharedStorage.getString(this, Consts.AVATAR_URL, "")
        if(!TextUtils.isEmpty(imagePath)) {
            Picasso.get()
                .load(imagePath)
                .resizeDimen(R.dimen.guest_logo_mini_size, R.dimen.guest_logo_mini_size)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(logoImage)
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
        searchOption.setIcon(R.drawable.ic_clear_white)
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
        searchOption.setIcon(R.drawable.ic_clear_white)
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

        var bitmapMarker = BitmapDescriptorFactory.fromBitmap(
            DrawableUtils.resizeMapIcons(this, "ic_location_poi",120,120))

        when (poiDet.accessible.toInt()) {
            EVALUATION_AVAILABLE -> {
                statusPoi.setTextColor(resources.getColor(R.color.weirdGreen))
                statusPoi.text = getString(R.string.available) + " " + getString(R.string.object_text)
                bitmapMarker = BitmapDescriptorFactory.fromBitmap(
                    DrawableUtils.resizeMapIcons(this, "ic_location_available",120,120))
            }
            EVALUATION_TROUBLESOME -> {
                statusPoi.setTextColor(resources.getColor(R.color.orangeYellow))
                statusPoi.text = getString(R.string.troublesome) + " " + getString(R.string.object_text)
                bitmapMarker = BitmapDescriptorFactory.fromBitmap(
                    DrawableUtils.resizeMapIcons(this, "ic_location_trouble",150,150))
            }
            EVALUATION_INACCESSIBLE -> {
                statusPoi.setTextColor(resources.getColor(R.color.coral))
                statusPoi.text = getString(R.string.inaccessible) + " " + getString(R.string.object_text)
                bitmapMarker = BitmapDescriptorFactory.fromBitmap(
                    DrawableUtils.resizeMapIcons(this, "ic_location_inaccessible",150,150))
            }
        }

        ratingsPoi.text = if(poiDet.reviews>0) getString(R.string.ratings) + " " + poiDet.reviews else getString(R.string.ratings_not_have)
        namePoi.text = poiDet.name
        typePoi.text = poiDet.types?.joinToString(separator = ", ")
        addressPoi.text = poiDet.address
        workinghoursPoi.text = poiDet.workinghours?.joinToString(separator = ", \n")

        showContainer(true)

        mMap?.clear()
        val location = poiDet.location
        if(poiDet.location!=null) {
            foundedLocation = LatLng(location?.lat!!, location?.lng!!)
            val markerOptions = MarkerOptions()
            markerOptions.position(foundedLocation!!)
            markerOptions.title(poiDet.name)

            val marker: Marker?  = mMap?.addMarker(markerOptions)
            marker?.setIcon(bitmapMarker)

            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(foundedLocation, MAP_ZOOM_DEFAULT))

//            if(currentLocation!=null) {
//                val url = gpsTrackerPresenter.getRouteURL(
//                    LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!),
//                    latLng
//                )
//            }
        }



//        async {
//
//            val result = URL(url).readText()
//            uiThread {
//                val parser: Parser = Parser()
//                val stringBuilder: StringBuilder = StringBuilder(result)
//                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
//                // get to the correct element in JsonObject
//                val routes = json.array<JsonObject>("routes")
//                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
//                // For every element in the JsonArray, decode the polyline string and pass all points to a List
//                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)  }
//                // Add  points to polyline and bounds
//                options.add(sydney)
//                LatLongB.include(sydney)
//                for (point in polypts)  {
//                    options.add(point)
//                    LatLongB.include(point)
//                }
//                options.add(opera)
//                LatLongB.include(opera)
//                // build bounds
//                val bounds = LatLongB.build()
//                // add polyline to the map
//                mMap!!.addPolyline(options)
//                // show map with route centered
//                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
//            }
//        }

    }

    private fun showNavigationRoute(){
        val intent = Intent(Intent.ACTION_VIEW,
//        Uri.parse("+http://maps.google.com/maps?saddr="
//                + currentLocation?.latitude + "," + currentLocation?.longitude + "&daddr="
//                + foundedLocation?.latitude + "," + foundedLocation?.longitude))

        Uri.parse("http://maps.google.com/maps?"
                + "saddr="+ currentLocation?.latitude + "," + currentLocation?.longitude
                + "&daddr=" + foundedLocation?.latitude + "," + foundedLocation?.longitude))

        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity")
        startActivity(intent)
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
                it1 -> mapsRepository.addReview(it1,
                NewReviewRequest(
                    status,
                    comment.text.toString(),
                    Calendar.getInstance().timeInMillis
                )
            )
            }
        }

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        AnimUtils.enterAnimation(v, selectedView)

    }



}
