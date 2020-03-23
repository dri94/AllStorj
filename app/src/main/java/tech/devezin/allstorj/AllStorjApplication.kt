package tech.devezin.allstorj

import android.app.Application
import tech.devezin.allstorj.data.sources.BaseSource
import tech.devezin.allstorj.data.sources.LocalSourceImpl

class AllStorjApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocalSourceImpl.initialize(this)
    }
}
