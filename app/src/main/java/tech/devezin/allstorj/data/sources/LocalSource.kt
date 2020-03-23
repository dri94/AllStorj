package tech.devezin.allstorj.data.sources

import android.content.Context
import android.content.SharedPreferences

interface LocalSource {
    fun saveScope(scopeString: String) : Boolean
    fun getScopeString(): String
}

class LocalSourceImpl: LocalSource {
    override fun saveScope(scopeString: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getScopeString(): String {
        TODO("Not yet implemented")
    }

    companion object {
        private const val prefsName = "local_prefs"
        private lateinit var prefs: SharedPreferences
        fun initialize(context: Context) {
            prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        }
    }
}
