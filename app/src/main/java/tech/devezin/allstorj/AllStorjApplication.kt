package tech.devezin.allstorj

import android.app.Application
import kotlinx.coroutines.Dispatchers
import tech.devezin.allstorj.data.sources.BaseSource
import tech.devezin.allstorj.data.sources.LocalSource
import tech.devezin.allstorj.data.sources.LocalSourceImpl

class AllStorjApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocalSource.initialize(this)
        BaseSource.init(Dispatchers.IO)
    }
}
