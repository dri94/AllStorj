package tech.devezin.allstorj.data.sources

import io.storj.Project
import io.storj.Scope
import io.storj.Uplink
import io.storj.UplinkOption

open class BaseSource {
    companion object {
        private lateinit var scope: Scope
        internal lateinit var project: Project
        fun initSources(cacheDir: String, localSource: LocalSource) {
            Scope.parse(localSource.getScopeString()).also {
                scope = it
                project = Uplink(UplinkOption.tempDir(cacheDir)).openProject(it)
            }
        }
    }
}
