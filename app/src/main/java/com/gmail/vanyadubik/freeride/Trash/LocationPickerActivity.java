package com.gmail.vanyadubik.freeride.Trash;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.*;
import com.gmail.vanyadubik.freeride.R;
import com.gmail.vanyadubik.freeride.Trash.geocoder.GeocoderPresenter;
import com.gmail.vanyadubik.freeride.Trash.geocoder.GoogleGeocoderDataSource;
import com.gmail.vanyadubik.freeride.Trash.model.LocationPoint;
import com.gmail.vanyadubik.freeride.ui.HorizontalDottedProgress;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationPickerActivity extends AppCompatActivity{


    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ZIPCODE = "zipcode";
    public static final String ADDRESS = "address";
    public static final String LOCATION_ADDRESS = "location_address";
    public static final String TRANSITION_BUNDLE = "transition_bundle";
    public static final String LAYOUTS_TO_HIDE = "layouts_to_hide";
    public static final String SEARCH_ZONE = "search_zone";
    public static final String BACK_PRESSED_RETURN_OK = "back_pressed_return_ok";
    public static final String ENABLE_SATELLITE_VIEW = "enable_satellite_view";
    public static final String ENABLE_LOCATION_PERMISSION_REQUEST = "enable_location_permission_request";
    public static final String ENABLE_GOOGLE_PLACES = "enable_google_places";
    public static final String POIS_LIST = "pois_list";
    public static final String LEKU_POI = "leku_poi";
    private static final String GEOLOC_API_KEY = "geoloc_api_key";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_LOCATION_QUERY = "last_location_query";
    private static final String OPTIONS_HIDE_STREET = "street";
    private static final String OPTIONS_HIDE_CITY = "city";
    private static final String OPTIONS_HIDE_ZIPCODE = "zipcode";
    private static final int REQUEST_PLACE_PICKER = 6655;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int DEFAULT_ZOOM = 16;
    private static final int WIDER_ZOOM = 6;
    private static final int MIN_CHARACTERS = 2;
    private static final int DEBOUNCE_TIME = 400;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private LocationPoint currentLocationPoint;
    private GeocoderPresenter geocoderPresenter;

    private ArrayAdapter<String> adapter;
    private EditText searchView;
    private RelativeLayout searchContainer;
    private ImageButton searchButton;
    private ImageView clearSearchButton;
    private ListView listResult;
    private LinearLayout routeContainer;
    private Button evaluateBtn;
    private Button routeBtn;
    private TextView coordinates;
    private TextView longitude;
    private TextView latitude;
    private TextView city;
    private TextView zipCode;
    private FrameLayout locationInfoLayout;
    private HorizontalDottedProgress progressBar;
    private MenuItem searchOption;

    private final List<Address> locationList = new ArrayList<>();
    private List<String> locationNameList = new ArrayList<>();
    private boolean hasWiderZoom = false;
    private Bundle bundle = new Bundle();
    private Address selectedAddress;
    private boolean isSearchContainerVisible = false;
    private boolean isRouteContainerVisible = false;
    private boolean isLocationInformedFromBundle = false;
    private boolean isStreetVisible = true;
    private boolean isCityVisible = true;
    private boolean isZipCodeVisible = true;
    private boolean shouldReturnOkOnBackPressed = false;
    private boolean enableSatelliteView = true;
    private boolean enableLocationPermissionRequest = true;
    private boolean isGooglePlacesEnabled = false;
    private String searchZone;
    private List<LocationPoint> poisList;
    private Map<String, LocationPoint> lekuPoisMarkersMap;
    private Marker currentMarker;
    private TextWatcher textWatcher;
    private GoogleGeocoderDataSource apiInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
//        setUpToolBar();
//        setUpMainVariables();
//        setUpResultsList();
//        //updateValuesFromBundle(savedInstanceState);
//        checkLocationPermission();
//        setUpSearchView();
//        setUpMapIfNeeded();
//        setUpFloatingButtons();
//        buildGoogleApiClient();
//        track(TrackEvents.didLoadLocationPicker);

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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        googleApiClient.connect();
//        geocoderPresenter.setUI(this);
//    }
//
//    @Override
//    protected void onStop() {
//        if (googleApiClient.isConnected()) {
//            googleApiClient.disconnect();
//        }
//        geocoderPresenter.stop();
//        super.onStop();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        setUpMapIfNeeded();
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (searchView != null && textWatcher != null) {
//            searchView.removeTextChangedListener(textWatcher);
//        }
//        if (googleApiClient != null) {
//            googleApiClient.unregisterConnectionCallbacks(this);
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(isSearchContainerVisible){
//            hideSearchContainer();
//            return;
//        }
//        if(isRouteContainerVisible){
//            hideRouteContainer();
//            return;
//        }
//        super.onBackPressed();
//        track(TrackEvents.CANCEL);
//    }
//
//
//    private void checkLocationPermission() {
//        if (enableLocationPermissionRequest && PermissionUtils.INSTANCE.shouldRequestLocationStoragePermission(getApplicationContext())) {
//            PermissionUtils.INSTANCE.requestLocationPermission(this);
//        }
//    }
//
//    protected void track(TrackEvents event) {
//        LocationPicker.getTracker().onEventTracked(event);
//    }
//
//    private void setUpMainVariables() {
//        GooglePlacesDataSource placesDataSource = new GooglePlacesDataSource(Places.getGeoDataClient(this, null));
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        apiInteractor = new GoogleGeocoderDataSource(new NetworkClient(), new AddressBuilder());
//        GeocoderRepository geocoderRepository = new GeocoderRepository(new AndroidGeocoderDataSource(geocoder), apiInteractor);
//        geocoderPresenter = new GeocoderPresenter(new ReactiveLocationProvider(getApplicationContext()), geocoderRepository, placesDataSource);
//        geocoderPresenter.setUI(this);
//        progressBar = findViewById(R.id.progressBar);
//        progressBar.hideAnimation();
////        locationInfoLayout = findViewById(R.id.location_info);
////        longitude = findViewById(R.id.longitude);
////        latitude = findViewById(R.id.latitude);
////        street = findViewById(R.id.street);
////        coordinates = findViewById(R.id.coordinates);
////        city = findViewById(R.id.city);
////        zipCode = findViewById(R.id.zipCode);
//
//        locationNameList = new ArrayList<>();
//    }
//
//    private void setUpMapIfNeeded() {
//        if (map == null) {
//            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
//        }
//    }
//
//    private void setUpToolBar() {
//        Toolbar toolbar = findViewById(R.id.map_search_toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
//        searchContainer = (RelativeLayout) findViewById(R.id.searchContainer);
////        searchButton = (ImageButton) findViewById(R.id.search_button);
////        searchButton.setOnClickListener(view -> {
////            if (searchView.getText().toString().isEmpty()) {
////                startVoiceRecognitionActivity();
////            } else {
////                retrieveLocationFrom(searchView.getText().toString());
////                ScreenUtils.closeKeyboard(this);
////            }
////        });
//        clearSearchButton = findViewById(R.id.clearSearchButton);
//        clearSearchButton.setOnClickListener(view -> {
//            if (searchView != null) {
//                searchView.setText("");
//            }
//        });
//
//        routeContainer = (LinearLayout) findViewById(R.id.route_container);
//        evaluateBtn = (Button) findViewById(R.id.evaluate_button);
//        routeBtn = (Button) findViewById(R.id.route_button);
//    }
//
//    private void setUpResultsList() {
//        listResult = findViewById(R.id.listResult);
//        adapter =
//                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locationNameList);
//        listResult.setAdapter(adapter);
//        listResult.setOnItemClickListener((adapterView, view, i, l) -> {
//            setNewLocation(locationList.get(i));
//            changeListResultVisibility(View.GONE);
//            ScreenUtils.closeKeyboard(this);
//        });
//    }
//
//    private void setUpSearchView() {
//        searchView = findViewById(R.id.searchET);
//        searchView.setOnEditorActionListener((v, actionId, event) -> {
//            boolean handled = false;
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                retrieveLocationFrom(v.getText().toString());
//                ScreenUtils.closeKeyboard(this);
//                handled = true;
//            }
//            return handled;
//        });
//        textWatcher = getSearchTextWatcher();
//        searchView.addTextChangedListener(textWatcher);
//    }
//
//    private TextWatcher getSearchTextWatcher() {
//        return new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
//                if ("".equals(charSequence.toString())) {
//                    adapter.clear();
//                    adapter.notifyDataSetChanged();
//                    showLocationInfoLayout();
//                    if (clearSearchButton != null) {
//                        clearSearchButton.setVisibility(View.INVISIBLE);
//                    }
//                    if (searchButton != null) {
//                        searchButton.setImageResource(R.drawable.ic_mic);
//                    }
//                } else {
//                    if (charSequence.length() > MIN_CHARACTERS) {
//                        retrieveLocationWithDebounceTimeFrom(charSequence.toString());
//                    }
//                    if (clearSearchButton != null) {
//                        clearSearchButton.setVisibility(View.VISIBLE);
//                    }
//                    if (searchButton != null) {
//                        searchButton.setImageResource(R.drawable.ic_search);
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        };
//    }
//
//    private void setUpFloatingButtons() {
//        FloatingActionButton btnMyLocation = findViewById(R.id.btnMyLocation);
//        btnMyLocation.setOnClickListener(v -> {
//            geocoderPresenter.getLastKnownLocation();
//            track(TrackEvents.didLocalizeMe);
//        });
//
//        FloatingActionButton btnSatellite = findViewById(R.id.btnSatellite);
//        btnSatellite.setOnClickListener(view -> {
//            if (map != null && btnSatellite != null) {
//                map.setMapType(map.getMapType() == MAP_TYPE_SATELLITE ? MAP_TYPE_NORMAL : MAP_TYPE_SATELLITE);
//                btnSatellite.setImageResource(
//                        map.getMapType() == MAP_TYPE_SATELLITE ? R.drawable.ic_satellite_off : R.drawable.ic_satellite_on);
//            }
//        });
//        btnSatellite.setVisibility(enableSatelliteView ? View.VISIBLE : View.GONE);
//
//        FloatingActionButton btnTraffic = findViewById(R.id.btnTraffic);
//        btnTraffic.setOnClickListener(view -> {
//            if (map != null && btnTraffic != null) {
//                map.setTrafficEnabled(!map.isTrafficEnabled());
//                btnTraffic.setImageResource(
//                        map.isTrafficEnabled() ? R.drawable.ic_traffic_off : R.drawable.ic_traffic_on);
//            }
//        });
//        btnTraffic.setVisibility(enableSatelliteView ? View.VISIBLE : View.GONE);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.loc_picker_menu, menu);
//        searchOption = menu.findItem(R.id.action_voice);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_voice) {
//            if(selectedAddress==null){
//                showSearchContainer();
//            }else{
//                hideRouteContainer();
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (PermissionUtils.INSTANCE.isLocationPermissionGranted(getApplicationContext())) {
//            geocoderPresenter.getLastKnownLocation();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_PLACE_PICKER:
//                if (resultCode == Activity.RESULT_OK) {
//                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    searchView = findViewById(R.id.searchET);
//                    retrieveLocationFrom(matches.get(0));
//                }
//                break;
//            default:
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onMapClick(LatLng latLng) {
//
//    }
//
//    @Override
//    public void onMapLongClick(LatLng latLng) {
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        if (map == null) {
//            map = googleMap;
//            setDefaultMapSettings();
//            setCurrentPositionLocation();
//            setPoint();
//        }
//
//    }
//
//    private void startVoiceRecognitionActivity() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_search_promp));
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//                getString(R.string.voice_search_extra_language));
//
//        if (checkPlayServices()) {
//            try {
//                startActivityForResult(intent, REQUEST_PLACE_PICKER);
//            } catch (ActivityNotFoundException e) {
//                track(TrackEvents.startVoiceRecognitionActivityFailed);
//            }
//        }
//    }
//
//    private boolean checkPlayServices() {
//        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        int result = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
//        if (result != ConnectionResult.SUCCESS) {
//            if (googleAPI.isUserResolvableError(result)) {
//                googleAPI.getErrorDialog(this, result, CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    private void showLocationInfoLayout() {
//        changeLocationInfoLayoutVisibility(View.VISIBLE);
//    }
//
//    private void changeListResultVisibility(int visibility) {
//        listResult.setVisibility(visibility);
//    }
//
//    private void changeLocationInfoLayoutVisibility(int visibility) {
//        //  locationInfoLayout.setVisibility(visibility);
//    }
//
//    private void setPoint() {
//        if (poisList != null && !poisList.isEmpty()) {
//            lekuPoisMarkersMap = new HashMap<>();
//            for (LocationPoint locationPoint : poisList) {
//                Location location = locationPoint.getLocation();
//                if (location != null && locationPoint.getTitle() != null) {
//                    Marker marker = addPoiMarker(new LatLng(location.getLatitude(), location.getLongitude()),
//                            locationPoint.getTitle(), locationPoint.getAddress());
//                    lekuPoisMarkersMap.put(marker.getId(), locationPoint);
//                }
//            }
//
//            map.setOnMarkerClickListener(marker -> {
//                LocationPoint locationPoint = lekuPoisMarkersMap.get(marker.getId());
//                if (locationPoint != null) {
//                    //setLocationInfo(locationPoint);
//                    centerToPoint(locationPoint);
//                    track(TrackEvents.simpleDidLocalizeByLekuPoi);
//                }
//                return true;
//            });
//        }
//    }
//
//    private void centerToPoint(LocationPoint locationPoint) {
//        if (map != null) {
//            Location location = locationPoint.getLocation();
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(getDefaultZoom()).build();
//            hasWiderZoom = false;
//            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }
//    }
//
//    private synchronized void buildGoogleApiClient() {
//        GoogleApiClient.Builder googleApiClientBuilder = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API);
//
//        if (isGooglePlacesEnabled) {
//            googleApiClientBuilder.addApi(Places.GEO_DATA_API);
//        }
//
//        googleApiClient = googleApiClientBuilder.build();
//        googleApiClient.connect();
//    }
//
//    private Marker addMarker(LatLng latLng) {
//        return map.addMarker(new MarkerOptions().position(latLng).draggable(true));
//    }
//
//    private Marker addPoiMarker(LatLng latLng, String title, String address) {
//        return map.addMarker(new MarkerOptions().position(latLng)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                .title(title)
//                .snippet(address));
//    }
//
//    private void setNewLocation(Address address) {
//        this.selectedAddress = address;
//        if (currentLocation == null) {
//            currentLocation = new Location(getString(R.string.network_resource));
//        }
//
//        currentLocation.setLatitude(address.getLatitude());
//        currentLocation.setLongitude(address.getLongitude());
//        setNewMapMarker(new LatLng(address.getLatitude(), address.getLongitude()));
//        //setLocationInfo(address);
//        searchView.setText("");
//    }
//
//    private void setNewMapMarker(LatLng latLng) {
//        if (map != null) {
//            if (currentMarker != null) {
//                currentMarker.remove();
//            }
//            CameraPosition cameraPosition =
//                    new CameraPosition.Builder().target(latLng)
//                            .zoom(getDefaultZoom())
//                            .build();
//            hasWiderZoom = false;
//            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            currentMarker = addMarker(latLng);
//            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//                @Override
//                public void onMarkerDragStart(Marker marker) {
//
//                }
//
//                @Override
//                public void onMarkerDrag(Marker marker) {
//                }
//
//                @Override
//                public void onMarkerDragEnd(Marker marker) {
//                    if (currentLocation == null) {
//                        currentLocation = new Location(getString(R.string.network_resource));
//                    }
//                    currentLocationPoint = null;
//                    currentLocation.setLongitude(marker.getPosition().longitude);
//                    currentLocation.setLatitude(marker.getPosition().latitude);
//                    setCurrentPositionLocation();
//                }
//            });
//        }
//    }
//
//    private int getDefaultZoom() {
//        int zoom;
//        if (hasWiderZoom) {
//            zoom = WIDER_ZOOM;
//        } else {
//            zoom = DEFAULT_ZOOM;
//        }
//        return zoom;
//    }
//
//    private void retrieveLocationFrom(String query) {
//        if (searchZone != null && !searchZone.isEmpty()) {
//            retrieveLocationFromZone(query, searchZone);
//        } else {
//            retrieveLocationFromDefaultZone(query);
//        }
//    }
//
//    private void retrieveLocationWithDebounceTimeFrom(String query) {
//        if (searchZone != null && !searchZone.isEmpty()) {
//            retrieveDebouncedLocationFromZone(query, searchZone, DEBOUNCE_TIME);
//        } else {
//            retrieveDebouncedLocationFromDefaultZone(query, DEBOUNCE_TIME);
//        }
//    }
//
//    private void retrieveLocationFromDefaultZone(String query) {
//        if (CountryLocaleRect.getDefaultLowerLeft() != null) {
//            geocoderPresenter.getFromLocationName(query, CountryLocaleRect.getDefaultLowerLeft(),
//                    CountryLocaleRect.getDefaultUpperRight());
//        } else {
//            geocoderPresenter.getFromLocationName(query);
//        }
//    }
//
//    private void retrieveLocationFromZone(String query, String zoneKey) {
//        Locale locale = new Locale(zoneKey);
//        if (CountryLocaleRect.getLowerLeftFromZone(locale) != null) {
//            geocoderPresenter.getFromLocationName(query, CountryLocaleRect.getLowerLeftFromZone(locale),
//                    CountryLocaleRect.getUpperRightFromZone(locale));
//        } else {
//            geocoderPresenter.getFromLocationName(query);
//        }
//    }
//
//    private void retrieveDebouncedLocationFromDefaultZone(String query, int debounceTime) {
//        if (CountryLocaleRect.getDefaultLowerLeft() != null) {
//            geocoderPresenter.getDebouncedFromLocationName(query, CountryLocaleRect.getDefaultLowerLeft(),
//                    CountryLocaleRect.getDefaultUpperRight(), debounceTime);
//        } else {
//            geocoderPresenter.getDebouncedFromLocationName(query, debounceTime);
//        }
//    }
//
//    private void retrieveDebouncedLocationFromZone(String query, String zoneKey, int debounceTime) {
//        Locale locale = new Locale(zoneKey);
//        if (CountryLocaleRect.getLowerLeftFromZone(locale) != null) {
//            geocoderPresenter.getDebouncedFromLocationName(query, CountryLocaleRect.getLowerLeftFromZone(locale),
//                    CountryLocaleRect.getUpperRightFromZone(locale), debounceTime);
//        } else {
//            geocoderPresenter.getDebouncedFromLocationName(query, debounceTime);
//        }
//    }
//
//    private void setCoordinatesInfo(LatLng latLng) {
//        this.latitude.setText(String.format("%s: %s", getString(R.string.latitude), latLng.latitude));
//        this.longitude.setText(String.format("%s: %s", getString(R.string.longitude), latLng.longitude));
//        //      showCoordinatesLayout();
//    }
//
////    private void setLocationInfo(Address address) {
////        street.setText(address.getAddressLine(0));
////        //    city.setText(isStreetEqualsCity(address) ? "" : address.getLocality());
////        zipCode.setText(address.getPostalCode());
////        //   showAddressLayout();
////    }
//
////    private void setLocationInfo(LocationPoint poi) {
////        this.currentLocationPoint = poi;
////        street.setText(poi.getTitle());
////        city.setText(poi.getAddress());
////        zipCode.setText(null);
////        // showAddressLayout();
////    }
//
//    private void setDefaultMapSettings() {
//        if (map != null) {
//            map.setMapType(MAP_TYPE_NORMAL);
//            map.setOnMapLongClickListener(this);
//            map.setOnMapClickListener(this);
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            map.setMyLocationEnabled(true);
//            map.getUiSettings().setCompassEnabled(false);
//            map.getUiSettings().setMyLocationButtonEnabled(false);
//            map.getUiSettings().setMapToolbarEnabled(false);
//        }
//    }
//
//    private void setUpDefaultMapLocation() {
//        if (currentLocation != null) {
//            setCurrentPositionLocation();
//        } else {
//            searchView = findViewById(R.id.search);
//            retrieveLocationFrom(Locale.getDefault().getDisplayCountry());
//            hasWiderZoom = true;
//        }
//    }
//
//    private void setCurrentPositionLocation() {
//        if (currentLocation != null) {
//            setNewMapMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
//            geocoderPresenter.getInfoFromLocation(new LatLng(currentLocation.getLatitude(),
//                    currentLocation.getLongitude()));
//        }
//    }
//
//
//    @Override
//    public void willLoadLocation() {
//        progressBar.showAnimation();
//        changeListResultVisibility(View.GONE);
//    }
//
//    @Override
//    public void showLocations(List<Address> addresses) {
//
//    }
//
//    @Override
//    public void showDebouncedLocations(List<Address> addresses) {
//
//    }
//
//    @Override
//    public void didLoadLocation() {
//
//    }
//
//    @Override
//    public void showLoadLocationError() {
//
//    }
//
//    @Override
//    public void showLastLocation(Location location) {
//
//    }
//
//    @Override
//    public void didGetLastLocation() {
//
//    }
//
//    @Override
//    public void showLocationInfo(List<Address> addresses) {
//
//    }
//
//    @Override
//    public void willGetLocationInfo(LatLng latLng) {
//
//    }
//
//    @Override
//    public void didGetLocationInfo() {
//
//    }
//
//    @Override
//    public void showGetLocationInfoError() {
//
//    }
//
//
//    public void showSearchContainer(){
//        ScreenUtils.slideViewDown(this, searchContainer);
//        isSearchContainerVisible = true;
//        if(searchOption!=null) {
//            searchOption.setVisible(false);
//        }
//    }
//
//    public void hideSearchContainer(){
//        ScreenUtils.slideViewUp(this, searchContainer);
//        isSearchContainerVisible = false;
//        if(searchOption!=null) {
//            searchOption.setVisible(true);
//            searchOption.setIcon(R.drawable.ic_search);
//        }
//    }
//
//    public void showRouteContainer(){
//        ScreenUtils.slideViewDown(this, routeContainer);
//        isRouteContainerVisible = true;
//        if(searchOption!=null) {
//            searchOption.setIcon(R.drawable.ic_clear);
//        }
//    }
//
//    public void hideRouteContainer(){
//        ScreenUtils.slideViewUp(this, routeContainer);
//        isRouteContainerVisible = false;
//        if(searchOption!=null) {
//            searchOption.setIcon(R.drawable.ic_search);
//        }
//    }
}
