package com.dharmesh.geofencedemo.app

import android.app.Application
import com.dharmesh.geofencedemo.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidContext(this@MyApplication)
            val list = mutableListOf<Module>()
            list.add(getViewModelModule)
            modules(list) // Add your Koin modules here
        }
    }


    private val getViewModelModule =
        module {
            viewModelOf(::HomeViewModel)
        }

}