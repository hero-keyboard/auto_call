package com.keyboardhero.call.features

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class ProximitySensorListener(private val context: Context) : SensorEventListener {

    private var proximitySensor: Sensor? = null

    fun register() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregister() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_PROXIMITY) {
                val distance = it.values[0]
                if (distance < it.sensor.maximumRange) {
                    Log.i("AAA", "onSensorChanged: true")
                    onSensorChangedCall?.invoke(true)
                } else {
                    Log.i("AAA", "onSensorChanged: false")
                    onSensorChangedCall?.invoke(false)
                }
            }
        }
    }

    var onSensorChangedCall: ((Boolean) -> Unit)? = null
}
