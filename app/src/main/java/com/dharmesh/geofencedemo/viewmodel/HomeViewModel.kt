package com.dharmesh.geofencedemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel(){
    private val _geofenceStatus = MutableLiveData<String?>()
    val geofenceStatus: LiveData<String?> = _geofenceStatus

    fun updateGeofenceStatus(transitionType: String?) {
        // here send status to api or any other storage
        _geofenceStatus.value = transitionType
    }

}