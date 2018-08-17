package at.resiverbindet.activityrecognitionexample

import android.os.Bundle

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import at.resiverbindet.activityrecognition.activity.ActivityRecognizedService
import io.flutter.plugin.common.PluginRegistry

class MainActivity(): FlutterActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GeneratedPluginRegistrant.registerWith(this)
  }
}
