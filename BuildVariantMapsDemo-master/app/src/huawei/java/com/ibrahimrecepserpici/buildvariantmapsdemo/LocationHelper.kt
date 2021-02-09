package com.ibrahimrecepserpici.buildvariantmapsdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*

class LocationHelper @JvmOverloads constructor(
    private val activity: Activity,
    private val fragment: Fragment? = null,
    private val globalLocationCallback: GlobalLocationCallback,
    private val requestCode: Int = DEFAULT_REQUEST_CODE
) : LocationCallback() {

    companion object {
        const val DEFAULT_REQUEST_CODE = 9999
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)
    private val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)
    private val locationRequest = LocationRequest()

    init {
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun fusedLocationClient() = fusedLocationClient

    @SuppressLint("MissingPermission")
    fun requestLocationUpdatesWithSettingsCheck() {
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    this,
                    Looper.getMainLooper()
                )
                globalLocationCallback.onLocationRequest()
            }
            .addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        // In order to trigger onActivityResult on Fragment we need to call startIntentSenderForResult method
//                        fragment?.let {
//                            fragment.startIntentSenderForResult(
//                                locationSettingFailureException.resolution.intentSender,
//                                requestCode,
//                                null,
//                                0,
//                                0,
//                                0,
//                                null
//                            )
//                            return@addOnFailureListener
//                        }

                        it.startResolutionForResult(
                            activity,
                            requestCode
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            this,
            Looper.getMainLooper()
        )
        globalLocationCallback.onLocationRequest()
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        locationResult?.let { locationResultData ->
            for (location in locationResultData.locations) {
                if (location != null) {
                    globalLocationCallback.onLocationResult(location)
                    fusedLocationClient.removeLocationUpdates(this)
                    break
                }
            }
        }
    }

    override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
        locationAvailability?.let {
            val isLocationAvailable = it.isLocationAvailable
            if (!isLocationAvailable) {
                globalLocationCallback.onLocationFailed()
            }
        }
    }

    interface GlobalLocationCallback {
        fun onLocationResult(location: Location)
        fun onLocationFailed()
        fun onLocationRequest()
    }
}