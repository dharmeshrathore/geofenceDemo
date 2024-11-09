package com.dharmesh.geofencedemo.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dharmesh.geofencedemo.receiver.GeofenceEventReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

class GeofenceManager(private val context: Context) {

    fun getGeofence(id: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000) // Adjusted delay for dwell events
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    /**
     * Builds a GeofencingRequest with an initial trigger and the provided geofence.
     */
    fun createGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    /**
     * Provides a PendingIntent for geofence transition events.
     */
    fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceEventReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * Logs and returns a readable error message.
     */
    fun getErrorString(exception: Exception): String {
        val message = "Error: ${exception.message}"
        Log.e("GeofenceManager", message)
        return message
    }
}
