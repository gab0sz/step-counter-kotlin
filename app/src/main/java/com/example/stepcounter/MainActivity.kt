package com.example.stepcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var MagnitudePrevious = 0f
    private var running = false
    private var totalSteps = 0
    private var previousTotalSteps = 0f
    private lateinit var textView: TextView
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.stepsTaken)
        button = findViewById(R.id.button)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        }else{
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        val x_acceleration: Float = event.values[0]
        val y_acceleration: Float = event.values[1]
        val z_acceleration: Float = event.values[2]
        var Magnitude = sqrt(x_acceleration * x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration)
        val MagnitudeDelta = Magnitude - MagnitudePrevious
        MagnitudePrevious = Magnitude
        if (MagnitudeDelta > 10){
            totalSteps++;
        }
        textView.text = (totalSteps).toString();
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun buttonClick(view: View){
        textView.text = 0.toString()
        totalSteps = 0
    }
}