package com.ibrahimrecepserpici.buildvariantmapsdemo

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.telephony.CellLocation.requestLocationUpdate
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity(), LocationHelper.GlobalLocationCallback {


    companion object {


        const val REQUEST_CHECK_SETTINGS = 999
    }

    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationHelper = LocationHelper(activity = this, fragment = null, globalLocationCallback = this, requestCode = REQUEST_CHECK_SETTINGS)
    }

    // Result handled after user enables gps
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                requestLocationUpdate()
            }
        }
    }

    // Start location request after permission granted
    fun getCurrentLocation() {
        locationHelper.requestLocationUpdatesWithSettingsCheck()
    }

    // GlobalLocationCallback methods
    override fun onLocationResult(location: Location) {
        var currentLocation = location
        // Do whatever u want with location information
//        progressDialog.setVisibility(View.GONE)
    }

    override fun onLocationFailed() {
//        progressDialog.setVisibility(View.GONE)
        // Do whatever u want on error, show snackbar maybe
    }

    override fun onLocationRequest() {
        // Start progressDialog till location data fetch operation ends
//        progressDialog.setVisibility(View.VISIBLE)
    }
}