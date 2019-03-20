package com.gmail.vanyadubik.freeride.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.geocoder.*
import com.gmail.vanyadubik.freeride.geocoder.api.AddressBuilder
import com.gmail.vanyadubik.freeride.geocoder.api.NetworkClient
import com.gmail.vanyadubik.freeride.geocoder.places.GooglePlacesDataSource
import com.gmail.vanyadubik.freeride.location.CountryLocaleRect
import com.gmail.vanyadubik.freeride.location.LocationPicker
import com.gmail.vanyadubik.freeride.model.LocationPoint
import com.gmail.vanyadubik.freeride.tracker.TrackEvents
import com.gmail.vanyadubik.freeride.ui.HorizontalDottedProgress
import com.gmail.vanyadubik.freeride.utils.AnimUtils
import com.gmail.vanyadubik.freeride.utils.PermissionUtils
import com.gmail.vanyadubik.freeride.utils.ScreenUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import java.util.*

import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.toolbar_panel.*
import kotlinx.android.synthetic.main.bottom_sheet.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMapClickListener, GeocoderViewInterface {

    val LATITUDE = "latitude"
    val LONGITUDE = "longitude"
    val ZIPCODE = "zipcode"
    val ADDRESS = "address"
    val LOCATION_ADDRESS = "location_address"
    val TRANSITION_BUNDLE = "transition_bundle"
    val LAYOUTS_TO_HIDE = "layouts_to_hide"
    val SEARCH_ZONE = "search_zone"
    val BACK_PRESSED_RETURN_OK = "back_pressed_return_ok"
    val ENABLE_SATELLITE_VIEW = "enable_satellite_view"
    val ENABLE_LOCATION_PERMISSION_REQUEST = "enable_location_permission_request"
    val ENABLE_GOOGLE_PLACES = "enable_google_places"
    val POIS_LIST = "pois_list"
    val LEKU_POI = "leku_poi"
    private val GEOLOC_API_KEY = "geoloc_api_key"
    private val LOCATION_KEY = "location_key"
    private val LAST_LOCATION_QUERY = "last_location_query"
    private val OPTIONS_HIDE_STREET = "street"
    private val OPTIONS_HIDE_CITY = "city"
    private val OPTIONS_HIDE_ZIPCODE = "zipcode"
    private val REQUEST_PLACE_PICKER = 6655
    private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    private val DEFAULT_ZOOM = 16
    private val WIDER_ZOOM = 6
    private val MIN_CHARACTERS = 2
    private val DEBOUNCE_TIME = 400

    private lateinit var searchOption: MenuItem


    private lateinit var map: GoogleMap
    private lateinit var googleApiClient: GoogleApiClient
    private var currentLocation: Location? = null
    private var currentLocationPoint: LocationPoint? = null
    private lateinit var geocoderPresenter: GeocoderPresenter

    private lateinit var adapter: ArrayAdapter<String>

    private val locationList = ArrayList<Address>()
    private var locationNameList: List<String> = ArrayList()
    private var hasWiderZoom = false
    private lateinit var bundle: Bundle
    private lateinit var selectedAddress: Address
    private var isSearchContainerVisible = false
    private var isRouteContainerVisible = false
    private val isLocationInformedFromBundle = false
    private val isStreetVisible = true
    private val isCityVisible = true
    private val isZipCodeVisible = true
    private val shouldReturnOkOnBackPressed = false
    private val enableSatelliteView = true
    private val enableLocationPermissionRequest = true
    private val isGooglePlacesEnabled = false
    private lateinit var searchZone: String
    private lateinit var poisList: List<LocationPoint>
    private lateinit var lekuPoisMarkersMap: MutableMap<String, LocationPoint>
    private lateinit var currentMarker: Marker
    private lateinit var textWatcher: TextWatcher
    private lateinit var apiInteractor: GoogleGeocoderDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setUpToolBar()
        setUpMainVariables()
        setUpResultsList()
        //updateValuesFromBundle(savedInstanceState);
        checkLocationPermission()
        setUpSearchView()
        setUpMapIfNeeded()
        setUpFloatingButtons()
        buildGoogleApiClient()
        track(TrackEvents.didLoadLocationPicker)

//        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) findViewById(R.id.bottom_sheet));
//        mBehavior.setPeekHeight(findViewById(R.id.map).getHeight());
//        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//        BottomSheetDialog bottomSheerDialog = new BottomSheetDialog(this);
//        View parentView = getLayoutInflater().inflate(R.layout.toolbar_search,null);
//        bottomSheerDialog.setContentView(parentView);
//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) findViewById(R.id.bottom_sheet));
//        bottomSheetBehavior.setPeekHeight(int);
//
//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics());

//        View bottomSheet = findViewById(R.id.map);
//        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
//        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                // React to state change
//            }
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                // React to dragging events
//            }
//        });
//
//        bottomSheerDialog.show();

//        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                BottomSheetDialog d = (BottomSheetDialog) dialog;
//                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
//                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
//                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//                bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
//                coordinatorLayout.getParent().requestLayout();
//            }
//        });
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
        geocoderPresenter.setUI(this)
    }

    override fun onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect()
        }
        geocoderPresenter.stop()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        setUpMapIfNeeded()
    }

    override fun onDestroy() {
        searchET.removeTextChangedListener(textWatcher)
        googleApiClient.unregisterConnectionCallbacks(this)
        super.onDestroy()
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
        track(TrackEvents.CANCEL)
    }


    private fun checkLocationPermission() {
        if (enableLocationPermissionRequest && PermissionUtils.shouldRequestLocationStoragePermission(applicationContext)) {
            PermissionUtils.requestLocationPermission(this)
        }
    }

    protected fun track(event: TrackEvents) {
        LocationPicker.getTracker().onEventTracked(event)
    }

    private fun setUpMainVariables() {
        val placesDataSource = GooglePlacesDataSource(Places.getGeoDataClient(this, null))
        val geocoder = Geocoder(this, Locale.getDefault())
        apiInteractor = GoogleGeocoderDataSource(NetworkClient(), AddressBuilder())
        val geocoderRepository = GeocoderRepository(AndroidGeocoderDataSource(geocoder), apiInteractor)
        geocoderPresenter =
            GeocoderPresenter(ReactiveLocationProvider(applicationContext), geocoderRepository, placesDataSource)
        geocoderPresenter.setUI(this)
        progressBar.hideAnimation()
        //        locationInfoLayout = findViewById(R.id.location_info);
        //        longitude = findViewById(R.id.longitude);
        //        latitude = findViewById(R.id.latitude);
        //        street = findViewById(R.id.street);
        //        coordinates = findViewById(R.id.coordinates);
        //        city = findViewById(R.id.city);
        //        zipCode = findViewById(R.id.zipCode);

        locationNameList = ArrayList<String>()
    }

    private fun setUpMapIfNeeded() {
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    private fun setUpToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.map_search_toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        clearSearchImage.setOnClickListener {
            searchET.setText("")
        }
    }

    private fun setUpResultsList() {
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locationNameList)
        listResult.adapter = adapter
        listResult.setOnItemClickListener { _, _, i, _ ->
            setNewLocation(locationList[i])
            changeListResultVisibility(View.GONE)
            ScreenUtils.closeKeyboard(this)
        }
    }

    private fun setUpSearchView() {
        searchET.setOnEditorActionListener { v, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                retrieveLocationFrom(v.text.toString())
                ScreenUtils.closeKeyboard(this)
                handled = true
            }
            handled
        }
        textWatcher = getSearchTextWatcher()
        searchET.addTextChangedListener(textWatcher)
    }

    private fun getSearchTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
                if ("" == charSequence.toString()) {
                    adapter.clear()
                    adapter.notifyDataSetChanged()
                    showLocationInfoLayout()
                    clearSearchImage.visibility = View.INVISIBLE

                } else {
                    if (charSequence.length > MIN_CHARACTERS) {
                        retrieveLocationWithDebounceTimeFrom(charSequence.toString())
                    }
                    clearSearchImage.visibility = View.VISIBLE

                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setUpFloatingButtons() {
        btnMyLocation.setOnClickListener {
            geocoderPresenter.getLastKnownLocation()
            track(TrackEvents.didLocalizeMe)
        }

        btnSatellite.setOnClickListener {
            map.setMapType(if (map.getMapType() == MAP_TYPE_SATELLITE) MAP_TYPE_NORMAL else MAP_TYPE_SATELLITE)
            btnSatellite.setImageResource(
                if (map.getMapType() == MAP_TYPE_SATELLITE) R.drawable.ic_satellite_off else R.drawable.ic_satellite_on
            )
        }
        btnSatellite.visibility = if (enableSatelliteView) View.VISIBLE else View.GONE

        btnTraffic.setOnClickListener { view ->
            if (map != null && btnTraffic != null) {
                map.isTrafficEnabled = !map.isTrafficEnabled
                btnTraffic.setImageResource(
                    if (map.isTrafficEnabled()) R.drawable.ic_traffic_off else R.drawable.ic_traffic_on
                )
            }
        }
        btnTraffic.visibility = if (enableSatelliteView) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.loc_picker_menu, menu)
        searchOption = menu.findItem(R.id.action_voice)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_voice) {
            if (selectedAddress == null) {
                showSearchContainer()
            } else {
                hideRouteContainer()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionUtils.isLocationPermissionGranted(applicationContext)) {
            geocoderPresenter.getLastKnownLocation()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PLACE_PICKER -> if (resultCode == Activity.RESULT_OK) {
                val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                retrieveLocationFrom(matches[0])
            }
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onMapClick(latLng: LatLng) {

    }

    override fun onMapLongClick(latLng: LatLng) {

    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        setDefaultMapSettings()
        setCurrentPositionLocation()
        setPoint()

    }

    private fun startVoiceRecognitionActivity() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_search_promp))
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            getString(R.string.voice_search_extra_language)
        )

        if (checkPlayServices()) {
            try {
                startActivityForResult(intent, REQUEST_PLACE_PICKER)
            } catch (e: ActivityNotFoundException) {
                track(TrackEvents.startVoiceRecognitionActivityFailed)
            }

        }
    }

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(applicationContext)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, CONNECTION_FAILURE_RESOLUTION_REQUEST).show()
            }
            return false
        }
        return true
    }

    private fun showLocationInfoLayout() {
        changeLocationInfoLayoutVisibility(View.VISIBLE)
    }

    private fun changeListResultVisibility(visibility: Int) {
        listResult.visibility = visibility
    }

    private fun changeLocationInfoLayoutVisibility(visibility: Int) {
        //  locationInfoLayout.setVisibility(visibility);
    }

    private fun setPoint() {
        if (poisList != null && !poisList.isEmpty()) {
            lekuPoisMarkersMap = HashMap<String, LocationPoint>()
            for (locationPoint in poisList) {
                val location = locationPoint.getLocation()
                if (location != null && locationPoint.getTitle() != null) {
                    val marker = addPoiMarker(
                        LatLng(location!!.getLatitude(), location!!.getLongitude()),
                        locationPoint.getTitle(), locationPoint.getAddress()
                    )
                    lekuPoisMarkersMap.put(marker.id, locationPoint)
                }
            }

            map.setOnMarkerClickListener({ marker ->
                val locationPoint = lekuPoisMarkersMap.get(marker.getId())
                if (locationPoint != null) {
                    //setLocationInfo(locationPoint);
                    centerToPoint(locationPoint)
                    track(TrackEvents.simpleDidLocalizeByLekuPoi)
                }
                true
            })
        }
    }

    private fun centerToPoint(locationPoint: LocationPoint) {
        if (map != null) {
            val location = locationPoint.location
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude)).zoom(getDefaultZoom().toFloat()).build()
            hasWiderZoom = false
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        val googleApiClientBuilder = GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)

        if (isGooglePlacesEnabled) {
            googleApiClientBuilder.addApi(Places.GEO_DATA_API)
        }

        googleApiClient = googleApiClientBuilder.build()
        googleApiClient.connect()
    }

    private fun addMarker(latLng: LatLng): Marker {
        return map.addMarker(MarkerOptions().position(latLng).draggable(true))
    }

    private fun addPoiMarker(latLng: LatLng, title: String, address: String): Marker {
        return map.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title)
                .snippet(address)
        )
    }

    private fun setNewLocation(address: Address) {
        this.selectedAddress = address
        if (currentLocation == null) {
            currentLocation = Location(getString(R.string.network_resource))
        }

        currentLocation?.setLatitude(address.latitude)
        currentLocation?.setLongitude(address.longitude)
        setNewMapMarker(LatLng(address.latitude, address.longitude))
        //setLocationInfo(address);
        searchET.setText("")
    }

    private fun setNewMapMarker(latLng: LatLng) {
        if (map != null) {
            if (currentMarker != null) {
                currentMarker.remove()
            }
            val cameraPosition = CameraPosition.Builder().target(latLng)
                .zoom(getDefaultZoom().toFloat())
                .build()
            hasWiderZoom = false
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            currentMarker = addMarker(latLng)
            map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) {

                }

                override fun onMarkerDrag(marker: Marker) {}

                override fun onMarkerDragEnd(marker: Marker) {
                    if (currentLocation == null) {
                        currentLocation = Location(getString(R.string.network_resource))
                    }
                    currentLocationPoint = null
                    currentLocation?.setLongitude(marker.position.longitude)
                    currentLocation?.setLatitude(marker.position.latitude)
                    setCurrentPositionLocation()
                }
            })
        }
    }

    private fun getDefaultZoom(): Int {
        val zoom: Int
        if (hasWiderZoom) {
            zoom = WIDER_ZOOM
        } else {
            zoom = DEFAULT_ZOOM
        }
        return zoom
    }

    private fun retrieveLocationFrom(query: String) {
        if (searchZone != null && !searchZone.isEmpty()) {
            retrieveLocationFromZone(query, searchZone)
        } else {
            retrieveLocationFromDefaultZone(query)
        }
    }

    private fun retrieveLocationWithDebounceTimeFrom(query: String) {
        if (searchZone != null && !searchZone.isEmpty()) {
            retrieveDebouncedLocationFromZone(query, searchZone, DEBOUNCE_TIME)
        } else {
            retrieveDebouncedLocationFromDefaultZone(query, DEBOUNCE_TIME)
        }
    }

    private fun retrieveLocationFromDefaultZone(query: String) {
        if (CountryLocaleRect.getDefaultLowerLeft() != null) {
            geocoderPresenter.getFromLocationName(
                query, CountryLocaleRect.getDefaultLowerLeft(),
                CountryLocaleRect.getDefaultUpperRight()
            )
        } else {
            geocoderPresenter.getFromLocationName(query)
        }
    }

    private fun retrieveLocationFromZone(query: String, zoneKey: String) {
        val locale = Locale(zoneKey)
        if (CountryLocaleRect.getLowerLeftFromZone(locale) != null) {
            geocoderPresenter.getFromLocationName(
                query, CountryLocaleRect.getLowerLeftFromZone(locale),
                CountryLocaleRect.getUpperRightFromZone(locale)
            )
        } else {
            geocoderPresenter.getFromLocationName(query)
        }
    }

    private fun retrieveDebouncedLocationFromDefaultZone(query: String, debounceTime: Int) {
        if (CountryLocaleRect.getDefaultLowerLeft() != null) {
            geocoderPresenter.getDebouncedFromLocationName(
                query, CountryLocaleRect.getDefaultLowerLeft(),
                CountryLocaleRect.getDefaultUpperRight(), debounceTime
            )
        } else {
            geocoderPresenter.getDebouncedFromLocationName(query, debounceTime)
        }
    }

    private fun retrieveDebouncedLocationFromZone(query: String, zoneKey: String, debounceTime: Int) {
        val locale = Locale(zoneKey)
        if (CountryLocaleRect.getLowerLeftFromZone(locale) != null) {
            geocoderPresenter.getDebouncedFromLocationName(
                query, CountryLocaleRect.getLowerLeftFromZone(locale),
                CountryLocaleRect.getUpperRightFromZone(locale), debounceTime
            )
        } else {
            geocoderPresenter.getDebouncedFromLocationName(query, debounceTime)
        }
    }

    private fun setCoordinatesInfo(latLng: LatLng) {
//        latitude.text = String.format("%s: %s", getString(R.string.latitude), latLng.latitude)
//        longitude.text = String.format("%s: %s", getString(R.string.longitude), latLng.longitude)
        //      showCoordinatesLayout();
    }

//    private void setLocationInfo(Address address) {
//        street.setText(address.getAddressLine(0));
//        //    city.setText(isStreetEqualsCity(address) ? "" : address.getLocality());
//        zipCode.setText(address.getPostalCode());
//        //   showAddressLayout();
//    }

//    private void setLocationInfo(LocationPoint poi) {
//        this.currentLocationPoint = poi;
//        street.setText(poi.getTitle());
//        city.setText(poi.getAddress());
//        zipCode.setText(null);
//        // showAddressLayout();
//    }

    private fun setDefaultMapSettings() {
        if (map != null) {
            map.setMapType(MAP_TYPE_NORMAL)
            map.setOnMapLongClickListener(this)
            map.setOnMapClickListener(this)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.setMyLocationEnabled(true)
            map.getUiSettings().setCompassEnabled(false)
            map.getUiSettings().setMyLocationButtonEnabled(false)
            map.getUiSettings().setMapToolbarEnabled(false)
        }
    }

    private fun setUpDefaultMapLocation() {
        if (currentLocation != null) {
            setCurrentPositionLocation()
        } else {
            retrieveLocationFrom(Locale.getDefault().displayCountry)
            hasWiderZoom = true
        }
    }

    private fun setCurrentPositionLocation() {
        if (currentLocation != null) {
            setNewMapMarker(LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
            geocoderPresenter.getInfoFromLocation(
                LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
                )
            )
        }
    }


    override fun willLoadLocation() {
        progressBar.showAnimation()
        changeListResultVisibility(View.GONE)
    }

    override fun showLocations(addresses: List<Address>) {

    }

    override fun showDebouncedLocations(addresses: List<Address>) {

    }

    override fun didLoadLocation() {

    }

    override fun showLoadLocationError() {

    }

    override fun showLastLocation(location: Location) {

    }

    override fun didGetLastLocation() {

    }

    override fun showLocationInfo(addresses: List<Address>) {

    }

    override fun willGetLocationInfo(latLng: LatLng) {

    }

    override fun didGetLocationInfo() {

    }

    override fun showGetLocationInfoError() {

    }


    fun showSearchContainer() {
        AnimUtils.collapse(searchContainer)
        isSearchContainerVisible = true
        if (searchOption != null) {
            searchOption.isVisible = false
        }
    }

    fun hideSearchContainer() {
        AnimUtils.expand(searchContainer)
        isSearchContainerVisible = false
        if (searchOption != null) {
            searchOption.isVisible = true
            searchOption.setIcon(R.drawable.ic_search)
        }
    }

    fun showRouteContainer() {
        ScreenUtils.slideViewDown(this, routeContainer)
        isRouteContainerVisible = true
        if (searchOption != null) {
            searchOption.setIcon(R.drawable.ic_clear)
        }
    }

    fun hideRouteContainer() {
        ScreenUtils.slideViewUp(this, routeContainer)
        isRouteContainerVisible = false
        if (searchOption != null) {
            searchOption.setIcon(R.drawable.ic_search)
        }
    }
}
