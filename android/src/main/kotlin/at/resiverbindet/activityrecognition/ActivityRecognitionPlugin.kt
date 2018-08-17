package at.resiverbindet.activityrecognition

import at.resiverbindet.activityrecognition.activity.ActivityClient
import at.resiverbindet.activityrecognition.activity.ActivityRecognizedService
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.view.FlutterNativeView

class ActivityRecognitionPlugin(val registrar: Registrar): PluginRegistry.ViewDestroyListener {

    private val activityClient = ActivityClient(registrar.context())
    private val activityChannel = ActivityChannel(activityClient)

    init {
        activityChannel.register(this)
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = ActivityRecognitionPlugin(registrar)
            registrar.addViewDestroyListener(plugin)
        }
    }

    override fun onViewDestroy(nativeView: FlutterNativeView): Boolean {
        return ActivityRecognizedService.setBackgroundFlutterView(nativeView)
    }
}
