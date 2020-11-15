package ru.skytechdev.flutter_pdf_box_example

import io.flutter.app.FlutterApplication
    import android.content.Context
    import androidx.multidex.MultiDex
    
    class App : FlutterApplication() {
    
        override fun attachBaseContext(base: Context) {
            super.attachBaseContext(base)
            MultiDex.install(this)
        }
    
    }