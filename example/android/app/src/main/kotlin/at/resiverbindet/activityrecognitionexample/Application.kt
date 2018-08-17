package at.resiverbindet.activityrecognitionexample

import at.resiverbindet.activityrecognition.activity.ActivityRecognizedService
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback
import io.flutter.app.FlutterApplication


class Application : FlutterApplication(), PluginRegistrantCallback {
    override fun onCreate() {
        super.onCreate()
        ActivityRecognizedService.setPluginRegistrant(this)
    }

    override fun registerWith(registry: PluginRegistry) {
        GeneratedPluginRegistrant.registerWith(registry)
    }
}