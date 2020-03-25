package tech.devezin.allstorj.data.sources

import android.content.Context
import android.content.SharedPreferences
import tech.devezin.allstorj.data.sources.LocalSource.Companion.PREF_SCOPE
import tech.devezin.allstorj.data.sources.LocalSource.Companion.prefs

interface LocalSource {
    fun saveScope(scopeString: String) : Boolean
    fun getScopeString(): String?
    companion object {
         const val prefsName = "local_prefs"
        const val PREF_SCOPE = "scope"
        internal lateinit var prefs: SharedPreferences
        fun initialize(context: Context) {
            prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        }
    }
}

class LocalSourceImpl: LocalSource {
    override fun saveScope(scopeString: String): Boolean {
        return prefs.edit().putString(PREF_SCOPE, scopeString).commit()
    }

    override fun getScopeString(): String? {
        return prefs.getString(PREF_SCOPE, null)
    }
}
