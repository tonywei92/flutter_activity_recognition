/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

package at.resiverbindet.activityrecognition.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import at.resiverbindet.activityrecognition.Constants
import com.google.android.gms.location.ActivityRecognition


class ActivityClient(private val activity: Activity) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val activityRecognitionClient = ActivityRecognition.getClient(activity)
    private var activityUpdatesCallback: ((String) -> Unit)? = null
    //private var lastActivity: String? = null

    private var isPaused = true

    private val TAG = "ActivityClient"

    fun resume() {
        Log.d(TAG, "resume: start")
        Log.d(TAG, "resume: isPaused: $isPaused")
        if (!isPaused) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "resume: getDefaultSharedPreferences: ${PreferenceManager.getDefaultSharedPreferencesName(activity.applicationContext)}")
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)

        preferences.edit().clear().apply()
        preferences.registerOnSharedPreferenceChangeListener(this)
        isPaused = false

        Log.d(TAG, "resume: requestActivityUpdates")
        requestActivityUpdates()
    }

    fun pause() {
        if (isPaused) return

        val preferences =
            activity.applicationContext.getSharedPreferences("activity_recognition", MODE_PRIVATE)
        preferences.unregisterOnSharedPreferenceChangeListener(this)

        removeActivityUpdates()
    }

    fun registerActivityUpdateCallback(callback: (String) -> Unit) {
        //check(activityUpdatesCallback == null, { "trying to register a 2nd location updates callback" })
        activityUpdatesCallback = callback
    }

    fun deregisterLocationUpdatesCallback() {
        //check(activityUpdatesCallback != null, { "trying to deregister a non-existent location updates callback" })
        activityUpdatesCallback = null
    }

    @SuppressLint("MissingPermission")
    private fun requestActivityUpdates() {
        Log.d(TAG, "requestActivityUpdates: start")
        val task = activityRecognitionClient.requestActivityUpdates(
            Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
            getActivityDetectionPendingIntent()
        )
/*
        val transitions = ArrayList<ActivityTransition>()

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )


        val task = activityRecognitionClient.requestActivityTransitionUpdates(
            ActivityTransitionRequest(transitions),
            getActivityDetectionPendingIntent()
        )*/

        task.addOnSuccessListener {
            Log.d(TAG, "requestActivityUpdates: Activity Updates enabled successfully!")
        }

        task.addOnFailureListener {
            Log.d(TAG, "requestActivityUpdates: Failed to enable Activity updates: " + it.message)
        }
    }

    @SuppressLint("MissingPermission")
    private fun removeActivityUpdates() {
        val task = activityRecognitionClient.removeActivityUpdates(
            getActivityDetectionPendingIntent()
        )

        task.addOnSuccessListener {
            Log.d(TAG, "requestActivityUpdates: Activity Updates enabled successfully!")
        }

        task.addOnFailureListener {
            Log.d(TAG, "requestActivityUpdates: Failed to enable Activity updates: " + it.message)
        }
    }

    private fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(activity, ActivityRecognizedService::class.java)


        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.d(TAG, "onSharedPreferenceChanged: start")
        if (key == Constants.KEY_DETECTED_ACTIVITIES) {
            Log.d(TAG, "onSharedPreferenceChanged: correct key")
            val result = sharedPreferences
                .getString(Constants.KEY_DETECTED_ACTIVITIES, "")
            Log.d(TAG, "onSharedPreferenceChanged: result: $result")
            activityUpdatesCallback?.invoke(result)
        }
    }
}