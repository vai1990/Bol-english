package com.example.ai

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"

// App Check contract keys defined by the Firebase SDK and runtime harness.
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

// Tracks whether a registered debug token was explicitly supplied via launch Intent extra.
// This distinguishes AI Studio / test-harness debug runs from sideloaded app-debug.apk installs
// on physical Android phones (which have BuildConfig.DEBUG == true but no injected debug token).
private const val APP_CHECK_META_PREFS = "app_check_init_meta"
private const val HAS_INTENT_DEBUG_TOKEN_KEY = "has_intent_debug_token"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        // 1. Ensure FirebaseApp is initialized
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val firebaseApp = FirebaseApp.getInstance()

        if (BuildConfig.DEBUG) {
            val persistenceKey = firebaseApp.persistenceKey
            val debugStorePrefs = context.getSharedPreferences(
                "$DEBUG_STORE_PREFIX$persistenceKey",
                Context.MODE_PRIVATE
            )
            val metaPrefs = context.getSharedPreferences(
                APP_CHECK_META_PREFS,
                Context.MODE_PRIVATE
            )

            // 2. Debug: Read debug token from launch Intent extra if supplied
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)?.trim()

            if (!debugToken.isNullOrEmpty()) {
                // CRITICAL: synchronous commit() so secret is persisted before
                // DebugAppCheckProviderFactory reads it.
                debugStorePrefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                metaPrefs.edit()
                    .putBoolean(HAS_INTENT_DEBUG_TOKEN_KEY, true)
                    .commit()
                Log.d(TAG, "App Check debug secret configured from Intent extra.")

                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                Log.d(TAG, "DebugAppCheckProviderFactory installed (intent token).")
                return
            }

            // Activity recreation / relaunch after an earlier Intent-supplied debug token
            val hadIntentDebugToken = metaPrefs.getBoolean(HAS_INTENT_DEBUG_TOKEN_KEY, false)
            val existingDebugSecret = debugStorePrefs.getString(DEBUG_SECRET_KEY, null)
            if (hadIntentDebugToken && !existingDebugSecret.isNullOrBlank()) {
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                Log.d(TAG, "DebugAppCheckProviderFactory installed (persisted intent token).")
                return
            }

            // Sideloaded debug APK on a physical phone (no Intent debug token):
            // Use reCAPTCHA Enterprise attestation with the provisioned site key instead of
            // letting DebugAppCheckProviderFactory generate an unregistered local UUID token.
            Log.i(TAG, "No Intent debug token present; falling back to reCAPTCHA Enterprise App Check.")
            if (!installRecaptchaProvider(context)) {
                Log.w(TAG, "recaptcha_site_key unavailable; falling back to DebugAppCheckProviderFactory.")
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
            }
        } else {
            // 3. Release: Use reCAPTCHA Enterprise attestation
            installRecaptchaProvider(context)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

private fun installRecaptchaProvider(context: Context): Boolean {
    val resId = context.resources.getIdentifier(
        RECAPTCHA_SITE_KEY_RES,
        "string",
        context.packageName
    )
    if (resId == 0) {
        Log.e(TAG, "recaptcha_site_key string resource not found in package.")
        return false
    }
    val siteKey = context.getString(resId).trim()
    if (siteKey.isEmpty()) {
        Log.e(TAG, "recaptcha_site_key resource string is blank.")
        return false
    }
    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
    )
    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
    return true
}
