package tech.devezin.allstorj.data.sources

import android.content.Context
import android.content.SharedPreferences

interface LocalSource {
    fun saveScope(scopeString: String) : Boolean
    fun getScopeString(): String?
}

class LocalSourceImpl: LocalSource {
    override fun saveScope(scopeString: String): Boolean {
        return prefs.edit().putString(PREF_SCOPE, scopeString).commit()
    }

    override fun getScopeString(): String? {
        return prefs.getString(PREF_SCOPE, null)
    }

    companion object {
        private const val prefsName = "local_prefs"
        private const val PREF_SCOPE = "scope"
        private lateinit var prefs: SharedPreferences
        fun initialize(context: Context) {
            prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        }
    }
}
