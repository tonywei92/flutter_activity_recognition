/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

package at.resiverbindet.activityrecognition

import android.util.Log
import at.resiverbindet.activityrecognition.activity.ActivityClient
import at.resiverbindet.activityrecognition.activity.ActivityRecognizedService
import io.flutter.plugin.common.JSONMethodCodec
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.json.JSONArray
import org.json.JSONException


class ActivityChannel(private val activityClient: ActivityClient) :
        MethodChannel.MethodCallHandler {

    private val TAG: String = "ActivityChannel"

    fun register(plugin: ActivityRecognitionPlugin) {
        Log.d("ActivityChannel", "register")
        val methodChannel = MethodChannel(
                plugin.registrar.messenger(),
                "activity_recognition/method_channel",
                JSONMethodCodec.INSTANCE)
        methodChannel.setMethodCallHandler(this)

        val backgroundChannel = MethodChannel(
                plugin.registrar.messenger(),
                "activity_recognition/background_channel",
                JSONMethodCodec.INSTANCE)
        backgroundChannel.setMethodCallHandler(this)
        ActivityRecognizedService.setBackgroundChannel(backgroundChannel)
    }

    // MethodChannel.MethodCallHandler

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        Log.d(TAG, "methodCall")
        val method = call.method
        val arguments = call.arguments
        try {
            if (method == "AlarmService.start") {
                startService(arguments as JSONArray)
                result.success(true)
            } else if (method == "AlarmService.initialized") {
                Log.d("onMethodCall", "AlarmService.initialized")
                ActivityRecognizedService.onInitialized()
                result.success(true)
            } else if (method == "Alarm.periodic") {
                periodic(arguments as JSONArray)
                result.success(true)
            } else if (method == "Alarm.oneShot") {
                //oneShot(arguments as JSONArray)
                //result.success(true)
                result.notImplemented()
            } else if (method == "Alarm.cancel") {
                //cancel(arguments as JSONArray)
                //result.success(true)
                result.notImplemented()
            } else {
                result.notImplemented()
            }
        } catch (e: JSONException) {
            result.error("error", "JSON error: " + e.message, null)
        }

    }

    @Throws(JSONException::class)
    private fun startService(arguments: JSONArray) {
        Log.d(TAG, "startService")
        val callbackHandle = arguments.getLong(0)
        Log.d(TAG, "startAlarmService")
        ActivityRecognizedService.startAlarmService(activityClient.context, callbackHandle)
    }

    @Throws(JSONException::class)
    private fun periodic(arguments: JSONArray) {
        val requestCode = arguments.getInt(0)
        val intervalMillis = arguments.getLong(1)
        val callbackHandle = arguments.getLong(2)
        activityClient.setPeriodic(requestCode, intervalMillis, callbackHandle)
    }
}