package com.acs.accelerate.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Stores primitive type of data to file by using unique key's
 */
open class Preferences constructor(context: Context, name: String? = null) {

    private val preferences: SharedPreferences

    init {
        var fileName = name
        if (fileName == null) {
            fileName = context.packageName + "." + javaClass :: getSimpleName
        }
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    companion object {
        val TAG = javaClass :: getSimpleName

        @Volatile
        private var INSTANCE: Preferences? = null

        /**
         * Get singleton instance of preferences
         */
        fun getInstance(context: Context): Preferences {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }

            synchronized(this) {
                val instance = Preferences(context)
                INSTANCE = instance
                return instance
            }
        }
    }

    fun contains(key: String) = preferences.contains(key)

    fun clear() = preferences.edit().clear().apply()

    fun commit(block: (SharedPreferences.Editor) -> Unit) {
        preferences.edit {
            block(this)
            this.commit()
        }
    }

    fun apply(block: (SharedPreferences.Editor) -> Unit) {
        preferences.edit {
            this.apply(block)
        }
    }

    fun<T> put(key: String, value: T) {
        val editor = preferences.edit()
        when(value) {
            is Int -> editor.putInt(key, value as Int)
            is Long -> editor.putLong(key, value as Long)
            is Float -> editor.putFloat(key, value as Float)
            is Boolean -> editor.putBoolean(key, value as Boolean)
            else -> editor.putString(key, value as String)
        }
        editor.apply()
    }

    fun<T> get(key: String, defaultValue: T) = when(defaultValue) {
        is Int -> preferences.getInt(key, defaultValue as Int)
        is Long -> preferences.getLong(key, defaultValue as Long)
        is Float -> preferences.getFloat(key, defaultValue as Float)
        is Boolean -> preferences.getBoolean(key, defaultValue as Boolean)
        else -> preferences.getString(key, defaultValue as String?)
    } as T

}

class AppPreferences(context: Context): Preferences(context) {
    // Session token
    var token: String
        set(value) = put(KEY_TOKEN, value)
        get() = get(KEY_TOKEN, "")

    companion object {
        const val KEY_TOKEN = "token"
    }
}

class Test {
    init {
        val context: Context? = null

        // Usage 1
        val appPreferences = AppPreferences(context!!)
        appPreferences.token

        // Usage 2
        val preferences = Preferences.getInstance(context)
        preferences.put("key", "")
        val value: String = preferences.get("key", "")
    }
}