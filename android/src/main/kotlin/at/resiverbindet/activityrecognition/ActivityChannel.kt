/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

package at.resiverbindet.activityrecognition

import android.util.Log
import at.resiverbindet.activityrecognition.activity.ActivityClient
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class ActivityChannel(private val activityClient: ActivityClient) :
        MethodChannel.MethodCallHandler, EventChannel.StreamHandler {

    fun register(plugin: ActivityRecognitionPlugin) {
        val methodChannel = MethodChannel(plugin.registrar.messenger(), "activity_recognition/activities")
        methodChannel.setMethodCallHandler(this)

        val eventChannel = EventChannel(plugin.registrar.messenger(), "activity_recognition/activityUpdates")
        eventChannel.setStreamHandler(this)
    }

    // MethodChannel.MethodCallHandler

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "startActivityUpdates" -> startActivityUpdates(result)
            else -> result.notImplemented()
        }
    }

    private fun startActivityUpdates(result: MethodChannel.Result) {
        activityClient.resume()
        result.success(true)
    }

    // EventChannel.StreamHandler

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        activityClient.registerActivityUpdateCallback { result ->
            events.success(result)
        }
    }

    override fun onCancel(p0: Any?) {
        activityClient.deregisterLocationUpdatesCallback()
    }
}