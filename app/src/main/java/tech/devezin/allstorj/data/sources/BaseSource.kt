package tech.devezin.allstorj.data.sources

import android.content.Context
import io.storj.Project
import io.storj.Scope
import io.storj.Uplink
import io.storj.UplinkOption

open class BaseSource {
    companion object {
        internal lateinit var storj: Project
        fun initSources(cacheDir: String) {
            Scope.parse(LocalSourceImpl().getScopeString()).also {
                storj = Uplink(UplinkOption.tempDir(cacheDir)).openProject(it)
            }
        }
    }
}
