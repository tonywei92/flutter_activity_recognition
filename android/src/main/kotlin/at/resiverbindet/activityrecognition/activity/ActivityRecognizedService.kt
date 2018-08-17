/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

package at.resiverbindet.activityrecognition.activity

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import at.resiverbindet.activityrecognition.Codec
import com.google.android.gms.location.ActivityRecognitionResult
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterRunArguments
import java.util.concurrent.atomic.AtomicBoolean


class ActivityRecognizedService(name: String = "ActivityRecognizedService") : IntentService(name) {

    companion object {
        val TAG = "ActivityRecognizedServi"

        private val sStarted = AtomicBoolean(false)
        private var sBackgroundFlutterView: FlutterNativeView? = null
        private var sBackgroundChannel: MethodChannel? = null
        private var sPluginRegistrantCallback: PluginRegistrantCallback? = null

        private var mAppBundlePath: String? = null

        fun onInitialized() {
            sStarted.set(true)
        }

        fun setBackgroundChannel(channel: MethodChannel) {
            sBackgroundChannel = channel
        }

        fun setBackgroundFlutterView(view: FlutterNativeView): Boolean {
            if (sBackgroundFlutterView != null && sBackgroundFlutterView !== view) {
                Log.i(TAG, "setBackgroundFlutterView tried to overwrite an existing FlutterNativeView")
                return false
            }
            sBackgroundFlutterView = view
            return true
        }

        fun setPluginRegistrant(callback: PluginRegistrantCallback) {
            sPluginRegistrantCallback = callback
        }

        // Here we start the AlarmService. This method does a few things:
        //   - Retrieves the callback information for the handle associated with the
        //     callback dispatcher in the Dart portion of the plugin.
        //   - Builds the arguments object for running in a new FlutterNativeView.
        //   - Enters the isolate owned by the FlutterNativeView at the callback
        //     represented by `callbackHandle` and initializes the callback
        //     dispatcher.
        //   - Registers the FlutterNativeView's PluginRegistry to receive
        //     MethodChannel messages.
        fun startAlarmService(context: Context, callbackHandle: Long) {
            Log.d(TAG, "startAlarmService()")
            FlutterMain.ensureInitializationComplete(context, null)
            Log.d(TAG, "ensureInitializationComplete done")
            val mAppBundlePath = FlutterMain.findAppBundlePath(context)
            Log.d(TAG, "findAppBundlePath done")
            val cb = FlutterCallbackInformation.lookupCallbackInformation(callbackHandle)
            if (cb == null) {
                Log.e(TAG, "Fatal: failed to find callback")
                return
            }

            Log.d(TAG, "got callback")

            // Note that we're passing `true` as the second argument to our
            // FlutterNativeView constructor. This specifies the FlutterNativeView
            // as a background view and does not create a drawing surface.
            sBackgroundFlutterView = FlutterNativeView(context, true)
            if (mAppBundlePath != null && !sStarted.get()) {
                Log.i(TAG, "Starting AlarmService...")
                val args = FlutterRunArguments()
                args.bundlePath = mAppBundlePath
                args.entrypoint = cb.callbackName
                args.libraryPath = cb.callbackLibraryPath
                sBackgroundFlutterView!!.runFromBundle(args)
                sPluginRegistrantCallback!!.registerWith(sBackgroundFlutterView!!.getPluginRegistry())
            }
        }
    }



    override fun onCreate() {
        super.onCreate()
        val context = getApplicationContext()
        FlutterMain.ensureInitializationComplete(context, null)
        mAppBundlePath = FlutterMain.findAppBundlePath(context)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onHandleIntent(intent: Intent) {
        Log.d(TAG, "received activity update!")

        if (intent.getBooleanExtra("demo", false)) {
            Log.e("onHandleIntent", "Service still running!")
            return
        }

        if (!sStarted.get()) {
            Log.i(TAG, "ActivityRecognitionService has not yet started.")
        }
        // Grab the handle for the callback associated with this alarm. Pay close
        // attention to the type of the callback handle as storing this value in a
        // variable of the wrong size will cause the callback lookup to fail.
        val callbackHandle = intent.getLongExtra("callbackHandle", 0)
        if (sBackgroundChannel == null) {
            Log.e(
                    TAG,
                    "setBackgroundChannel was not called before alarms were scheduled." + " Bailing out.")
            return
        }


        val result = ActivityRecognitionResult.extractResult(intent)

        if (result == null) {
            Log.e("onHandleIntent", "Service still running!")
            Log.e("onHandleIntent", "extras: ${intent.extras}")
            return
        }

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result.probableActivities as ArrayList


        val mostProbableActivity = detectedActivities.maxBy { it.confidence }

        sBackgroundChannel!!.invokeMethod("activityUpdate", arrayOf(callbackHandle, Codec.encodeResult(mostProbableActivity!!)))
    }
}