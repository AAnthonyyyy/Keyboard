package com.hgm.keyboard

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * @author：  HGM
 * @date：  2023-06-19 20:38
 */
class App : Application() {


      companion object {
            @SuppressLint("StaticFieldLeak")
            lateinit var context: Context
      }

      override fun onCreate() {
            super.onCreate()
            context = applicationContext
      }

}