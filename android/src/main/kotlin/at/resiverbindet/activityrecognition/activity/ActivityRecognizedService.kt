/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

package at.resiverbindet.activityrecognition.activity

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import at.resiverbindet.activityrecognition.Codec
import at.resiverbindet.activityrecognition.Constants
import com.google.android.gms.location.ActivityRecognitionResult

class ActivityRecognizedService(name: String = "ActivityRecognizedService") : IntentService(name) {

    val TAG = "ActivityRecognizedServi"

    override fun onHandleIntent(intent: Intent) {
        Log.d(TAG, "onHandleIntent: received activity")
        val result = ActivityRecognitionResult.extractResult(intent)

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result.probableActivities as ArrayList


        val mostProbableActivity = detectedActivities.maxBy { it.confidence }
        Log.d(TAG, "onHandleIntent: mostProbableActivity: $mostProbableActivity")
        Log.d(TAG, "onHandleIntent: processname: ${applicationContext.applicationInfo.processName}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "resume: getDefaultSharedPreferences: ${PreferenceManager.getDefaultSharedPreferencesName(applicationContext)}")
        }

        val preferences =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        //PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val persist = preferences.edit()
            .putString(
                Constants.KEY_DETECTED_ACTIVITIES,
                Codec.encodeResult(listOf(mostProbableActivity!!))
            )
            .commit()
        Log.d(TAG, "onHandleIntent: perstited: $persist")
    }
}