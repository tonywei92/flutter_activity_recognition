/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

package at.resiverbindet.activityrecognition

import com.google.android.gms.location.DetectedActivity

class Codec {

    companion object {
        /**
         * Returns a human readable String corresponding to a detected activity type.
         */
        fun getActivityString(detectedActivityType: Int): String {
            return when (detectedActivityType) {
                DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
                DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
                DetectedActivity.ON_FOOT -> "ON_FOOT"
                DetectedActivity.RUNNING -> "RUNNING"
                DetectedActivity.STILL -> "STILL"
                DetectedActivity.TILTING -> "TILTING"
                DetectedActivity.UNKNOWN -> "UNKNOWN"
                DetectedActivity.WALKING -> "WALKING"
                else -> "UNDEFINED"
            }
        }

        fun encodeResult(result: List<DetectedActivity>): String {
            val builder = StringBuilder()
            builder.append("[")
            for (r in result) {
                builder.append("{")
                builder.append("\"type\":\"" + getActivityString(r.type) + "\",")
                builder.append("\"confidence\":" + r.confidence.toString())
                builder.append("}")
            }
            builder.append("]")
            return builder.toString()
        }
    }
}