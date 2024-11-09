package com.dharmesh.geofencedemo.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dharmesh.geofencedemo.R
import com.dharmesh.geofencedemo.databinding.ActivityMainBinding
import com.dharmesh.geofencedemo.utils.Constants.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
import com.dharmesh.geofencedemo.utils.Constants.GEOFENCE_ID
import com.dharmesh.geofencedemo.utils.Constants.GEOFENCE_RADIUS
import com.dharmesh.geofencedemo.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.dharmesh.geofencedemo.utils.GeofenceManager
import com.dharmesh.geofencedemo.utils.SnackBarStatus
import com.dharmesh.geofencedemo.utils.showSnackBar
import com.dharmesh.geofencedemo.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mBinding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var mMap: GoogleMap
    private lateinit var geofenceManager: GeofenceManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { updateLocationOnMap(it) }
        }
    }
    private lateinit var locationRequest: LocationRequest
    private var currentLocationMarker: Marker? = null


    private val geofenceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val transitionType = intent.getStringExtra("transitionType")
            val geofenceIds = intent.getStringExtra("geofenceIds")
            // Use the received data as needed
            Log.d(TAG, "Geofence transition: $transitionType, Geofences: $geofenceIds")
            mBinding.root.showSnackBar("Transition: $transitionType", SnackBarStatus.Success)
            homeViewModel.updateGeofenceStatus(transitionType)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initMap()
        setupGeofencing()
    }

    private fun initMap() {
        // get the SupportMapFragment and map will be ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)

        // Here create location request Builder
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

    }

    private fun setupGeofencing() {
        geofenceManager = GeofenceManager(this)
        // Register the local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
            geofenceReceiver, IntentFilter("com.dharmesh.geofencedemo.GEOFENCE_EVENT")
        )
    }

    private fun updateLocationOnMap(location: Location) {
        Log.d(TAG, "updateLocationOnMap: ${location.latitude}, ${location.longitude}")
        val currentLatLng = LatLng(location.latitude, location.longitude)
        currentLocationMarker?.remove()
        currentLocationMarker =
            mMap.addMarker(
                MarkerOptions().position(currentLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    .title(getString(R.string.current_location))
            )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Here checking if location permission is granted or not
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                locationPermission,
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.setOnMapLongClickListener { latLong ->
            Log.d("Map's latLong", "${latLong.latitude} , ${latLong.longitude}")
            onHandleMapLongClick(latLong)
        }
        mMap.isTrafficEnabled = true
        mMap.isMyLocationEnabled = true
        startLocationUpdates()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE || requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Here permission has been granted, starting location updates
                startLocationUpdates()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    startLocationUpdates()
                } else {
                    showSettingsDialog()
                }
                Log.d("onRequestPermissionsResults", "Permission has been denied")

            }
        }
    }

    private fun showSettingsDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.go_to_settings))
        alertDialogBuilder.setMessage(getString(R.string.lets_go_to_the_setting_screen))
        alertDialogBuilder.setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
            goToSettings()
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun goToSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                locationPermission,
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    backgroundLocationPermission,
                    BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                )
                return
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun onHandleMapLongClick(latLng: LatLng) {
        mMap.clear()
        addGeofence(latLng)
        addMarkerAndCircle(latLng)
    }

    private fun addMarkerAndCircle(latLng: LatLng) {
        mMap.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        mMap.addCircle(
            CircleOptions().center(latLng).radius(GEOFENCE_RADIUS.toDouble()).strokeWidth(3f)
                .strokeColor(ContextCompat.getColor(this, R.color.marker_circle_border_color))
                .fillColor(ContextCompat.getColor(this, R.color.marker_circle_color))
        )
    }


    private fun addGeofence(latLng: LatLng) {
        val geofence = geofenceManager.getGeofence(
            GEOFENCE_ID,
            latLng,
            GEOFENCE_RADIUS,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest = geofenceManager.createGeofencingRequest(geofence)
        val pendingIntent = geofenceManager.createPendingIntent()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                mBinding.root.showSnackBar(
                    getString(R.string.geofence_has_added),
                    SnackBarStatus.Success
                )
            }
            .addOnFailureListener { e ->
                val errorMessage = geofenceManager.getErrorString(e)
                mBinding.root.showSnackBar(errorMessage, SnackBarStatus.Failure)
            }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver to prevent memory leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofenceReceiver)
    }


    companion object {
        private const val TAG = "MainActivity"
        private val locationPermission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        @RequiresApi(Build.VERSION_CODES.Q)
        private val backgroundLocationPermission =
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }


}