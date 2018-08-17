/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

package at.resiverbindet.activityrecognition.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import at.resiverbindet.activityrecognition.Constants
import com.google.android.gms.location.ActivityRecognition


class ActivityClient(val context: Context) {

    private val activityRecognitionClient = ActivityRecognition.getClient(context)

    private val TAG = "ActivityClient"


    fun setPeriodic(
            requestCode: Int,
            intervalMillis: Long = Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
            callbackHandle: Long) {
        requestActivityUpdates(
                requestCode,
                intervalMillis,
                callbackHandle)
    }


    @SuppressLint("MissingPermission")
    private fun requestActivityUpdates(requestCode: Int,
                                       intervalMillis: Long,
                                       callbackHandle: Long) {
        Log.d(TAG, "requestActivityUpdates: start")
        val pendingIntent = getActivityDetectionPendingIntent(callbackHandle)
        val task = activityRecognitionClient.requestActivityUpdates(
                intervalMillis,
                pendingIntent
        )

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
            Log.d(TAG, "requestActivityUpdates: Activity Updates removed successfully!")
        }

        task.addOnFailureListener {
            Log.d(TAG, "requestActivityUpdates: Failed to remove Activity updates: " + it.message)
        }
    }

    private fun getActivityDetectionPendingIntent(callbackHandle: Long = 0): PendingIntent {
        val intent = Intent(context, ActivityRecognizedService::class.java)
        intent.putExtra("callbackHandle", callbackHandle);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}