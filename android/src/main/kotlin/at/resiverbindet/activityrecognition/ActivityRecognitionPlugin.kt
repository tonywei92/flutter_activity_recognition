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
        registrar.activity().application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity?) {
                activityClient.pause()
            }

            override fun onActivityResumed(activity: Activity?) {
                activityClient.resume()
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {

            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

            }

            override fun onActivityStopped(activity: Activity?) {

            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

            }
        })

        activityChannel.register(this)
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar): Unit {
            val plugin = ActivityRecognitionPlugin(registrar)
        }
    }
}
