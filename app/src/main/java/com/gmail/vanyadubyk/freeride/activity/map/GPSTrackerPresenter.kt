package com.gmail.vanyadubyk.freeride.activity.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import pub.devrel.easypermissions.EasyPermissions

class GPSTrackerPresenter(private val mActivity: Activity, private val mListener: LocationListener) {

    private var mLocationCallback: LocationCallback? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null

    init {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity)
            mFusedLocationClient!!.lastLocation
                .addOnSuccessListener(mActivity) { location ->
                    if (location != null) {
                        mListener.onLocationFound(location)
                    }
                }
            createLocationRequest()
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    for (location in locationResult!!.locations) {
                        mListener.onLocationFound(location)
                    }
                }

            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        checkGpsSetting()
    }


    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.interval = UPDATE_INTERVAL.toLong()
        mLocationRequest!!.fastestInterval = FASTEST_INTERVAL.toLong()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.smallestDisplacement = DISPLACEMENT.toFloat()
    }


    fun onStart() {
        if (!checkRequiredLocationPermission()) {
            getLastLocation()
        } else {
            startLocationUpdates()
        }
    }

    fun stopLocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
    }

    fun onPause() {
        stopLocationUpdates()
    }

    private fun checkRequiredLocationPermission(): Boolean {
        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (!EasyPermissions.hasPermissions(mActivity, *perms)) {
            mListener.checkRequiredLocationPermission()
            return false
        } else {
            return true
        }
    }

  private  fun checkGpsSetting() {

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val task = LocationServices.getSettingsClient(mActivity).checkLocationSettings(builder.build())

        task.addOnCompleteListener { result ->
            try {
                result.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{
                        val resolvable = exception as ResolvableApiException

                        resolvable.startResolutionForResult(mActivity, MapsActivity.GPS_REQUEST_CODE)

                    }
                    // Location settings are not satisfied. But could be fixed by showing the
                    // user a dialog.
                    // Cast to a resolvable exception.
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        //empty
                    }
                }
                // Show the dialog by calling startResolutionForResult(),
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
            }
        }
    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(mActivity) { task ->
                if (task.isSuccessful && task.result != null) {
                    mListener.onLocationFound(task.result)
                } else {
                    Log.w("", "no_location_detected", task.exception)
                }
            }
    }


    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback!!, null)
    }

    fun getRouteURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }



    interface LocationListener {
        fun onLocationFound(location: Location?)

        fun locationError(msg: String)

        fun checkRequiredLocationPermission(): Boolean
    }

    companion object {
        private const val UPDATE_INTERVAL = 5000 // 1 min
        private const val FASTEST_INTERVAL = 1000 // 1 min
        private const val DISPLACEMENT = 0.5 // 0.5 km
        const  val RUN_TIME_PERMISSION_CODE = 999
    }
}
