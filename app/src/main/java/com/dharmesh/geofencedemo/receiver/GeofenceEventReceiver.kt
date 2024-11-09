package com.dharmesh.geofencedemo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceEventReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: Geofence event received")
        // here we will parse the geofencing event from the intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent?.hasError() == true) {
            Log.d(TAG, "Error while receiving geofence event")
            return
        }

        // Retrieve geofence IDs and transition type, then broadcast
        val geofenceIds =
            geofencingEvent?.triggeringGeofences?.joinToString(",") { it.requestId }
                ?: ""
        val transitionType = when (geofencingEvent?.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
            else -> "UNKNOWN"
        }

        // Send a local broadcast with geofence event data
        Intent(GEOFENCE_EVENT_ACTION).apply {
            putExtra("transitionType", transitionType)
            putExtra("geofenceIds", geofenceIds)
            LocalBroadcastManager.getInstance(context).sendBroadcast(this)
        }
    }

    companion object {
        private const val TAG = "GeofenceEventReceiver"
        const val GEOFENCE_EVENT_ACTION = "com.dharmesh.geofencedemo.GEOFENCE_EVENT"
    }

}