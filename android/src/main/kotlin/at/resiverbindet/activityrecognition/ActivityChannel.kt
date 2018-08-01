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
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ActivityChannel(private val activityClient: ActivityClient) :
    MethodChannel.MethodCallHandler, EventChannel.StreamHandler {


    fun register(plugin: ActivityRecognitionPlugin) {
        val methodChannel = MethodChannel(plugin.registrar.messenger(), "activity_recognition/activities")
        methodChannel.setMethodCallHandler(this)

        val eventChannel = EventChannel(plugin.registrar.messenger(), "activity_recognition/activityUpdates")
        eventChannel.setStreamHandler(this)
    }

    private fun currentActivity(result: MethodChannel.Result) {
        launch(UI) {

        }
    }

    private fun startActivityUpdates(result: MethodChannel.Result) {
        activityClient.resume()
        result.success(true)
    }

    // MethodChannel.MethodCallHandler

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "currentActivity" -> currentActivity(result)
            "startActivityUpdates" -> startActivityUpdates(result)
            else -> result.notImplemented()
        }
    }

    // EventChannel.StreamHandler

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        Log.d("event", "onListen: start")
        activityClient.registerActivityUpdateCallback { result ->
            Log.d("event", "onListen: $result")
            events.success(result)
        }
    }

    override fun onCancel(p0: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}