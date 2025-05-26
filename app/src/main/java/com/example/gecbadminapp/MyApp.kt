package com.example.gecbadminapp

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = "dmjori0fh"
        config["api_key"] = "499363839199682"
        config["api_secret"] = "VmF6LZ_iHopmxAbY9"
        MediaManager.init(this, config)
    }
}
