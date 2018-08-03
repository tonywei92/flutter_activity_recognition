package at.resiverbindet.activityrecognition

import android.app.Activity
import android.app.Application
import android.os.Bundle
import at.resiverbindet.activityrecognition.activity.ActivityClient
import io.flutter.plugin.common.PluginRegistry.Registrar

class ActivityRecognitionPlugin(val registrar: Registrar) {

    private val activityClient = ActivityClient(registrar.activity())
    private val activityChannel = ActivityChannel(activityClient)

    init {
        activityChannel.register(this)
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar): Unit {
            val plugin = ActivityRecognitionPlugin(registrar)
        }
    }
}
