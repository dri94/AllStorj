package tech.devezin.allstorj.data.sources

import io.storj.Project
import io.storj.Scope
import io.storj.Uplink
import io.storj.UplinkOption
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

open class BaseSource {
    companion object {
        internal lateinit var scope: Scope
        internal lateinit var project: Project
        private lateinit var ioDispatcher: CoroutineDispatcher

        fun init(dispatcher: CoroutineDispatcher) {
            ioDispatcher = dispatcher
        }
        fun initStorj(cacheDir: String, localSource: LocalSource) {
            Scope.parse(localSource.getScopeString()).also {
                scope = it
                project = Uplink(UplinkOption.tempDir(cacheDir)).openProject(it)
            }
        }

        suspend fun <Success, Failure> executeCallOnDispatcher(block: suspend () -> Result<Success, Failure>): Result<Success, Failure> {
            val deferred = CompletableDeferred<Result<Success, Failure>>()
            withContext(ioDispatcher) {
                deferred.complete(block())
            }
            return deferred.await()
        }
    }
}


