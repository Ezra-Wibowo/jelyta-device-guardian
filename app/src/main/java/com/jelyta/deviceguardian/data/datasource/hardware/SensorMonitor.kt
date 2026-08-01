package com.jelyta.deviceguardian.data.datasource.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

data class HardwareSensorInfo(
    val name: String,
    val vendor: String,
    val typeName: String,
    val powerMa: Float,
    val resolution: Float,
    val maxRange: Float,
    val version: Int
)

class SensorMonitor(private val context: Context) {

    fun getHardwareSensors(): List<HardwareSensorInfo> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensorList = sensorManager?.getSensorList(Sensor.TYPE_ALL) ?: emptyList()

        return sensorList.map { s ->
            HardwareSensorInfo(
                name = s.name ?: "Unknown Sensor",
                vendor = s.vendor ?: "System Hardware",
                typeName = getSensorTypeName(s.type),
                powerMa = s.power,
                resolution = s.resolution,
                maxRange = s.maximumRange,
                version = s.version
            )
        }
    }

    private fun getSensorTypeName(type: Int): String {
        return when (type) {
            Sensor.TYPE_ACCELEROMETER -> "Accelerometer (Motion)"
            Sensor.TYPE_MAGNETIC_FIELD -> "Magnetometer (Compass)"
            Sensor.TYPE_GYROSCOPE -> "Gyroscope (Rotation)"
            Sensor.TYPE_LIGHT -> "Light Sensor (Ambient)"
            Sensor.TYPE_PRESSURE -> "Barometer (Atmospheric Pressure)"
            Sensor.TYPE_PROXIMITY -> "Proximity Sensor"
            Sensor.TYPE_GRAVITY -> "Gravity Vector"
            Sensor.TYPE_LINEAR_ACCELERATION -> "Linear Acceleration"
            Sensor.TYPE_ROTATION_VECTOR -> "Rotation Vector 3D"
            Sensor.TYPE_ORIENTATION -> "Orientation Sensor"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "Humidity Sensor"
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "Ambient Temperature"
            Sensor.TYPE_STEP_COUNTER -> "Pedometer Step Counter"
            Sensor.TYPE_STEP_DETECTOR -> "Step Detector"
            Sensor.TYPE_HEART_RATE -> "Biometric Heart Rate"
            else -> "Hardware Sensor (Type $type)"
        }
    }
}
