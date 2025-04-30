package br.com.coisasdecasa

import android.content.Context
import android.content.SharedPreferences

object AppCache {
    private const val PREF_NAME = "app_prefs"
    private const val ONBOARDING_KEY = "onboarding_shown"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isOnboardingShown(context: Context): Boolean {
        return getPrefs(context).getBoolean(ONBOARDING_KEY, false)
    }

    fun setOnboardingShown(context: Context) {
        getPrefs(context).edit().putBoolean(ONBOARDING_KEY, true).apply()
    }
}
