package com.example.stepcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.util.*
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var MagnitudePrevious = 0f

    private var running = false

    private var currentSteps = 0
    private var totalSteps = 0
    private var previousTotalSteps = 0
    private var previousTodaysSteps = 0
    private var stepsToday = 0

    private var cal = Calendar.getInstance()
    private var currentDate = cal.get(Calendar.DAY_OF_YEAR)

    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var totalTextView: TextView
    private lateinit var stepsTodayView: TextView
    private lateinit var circularProgressBar: CircularProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.stepsTaken)
        button = findViewById(R.id.button)
        totalTextView = findViewById(R.id.totalSteps)
        stepsTodayView = findViewById(R.id.stepsToday)
        circularProgressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        circularProgressBar.progressMax = 10000f
        resetSteps()
        loadData()

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

        if (MagnitudeDelta > 6 && MagnitudePrevious != 0f){
            totalSteps++;
        }
        MagnitudePrevious = Magnitude
        currentSteps = totalSteps - previousTotalSteps
        stepsToday = currentSteps

        circularProgressBar.apply{
            setProgressWithAnimation(stepsToday.toFloat())
        }

        stepsTodayView.text = stepsToday.toString()
        textView.text = currentSteps.toString();
        totalTextView.text = totalSteps.toString()
        saveData()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun buttonClick(view: View){


    }
    fun resetSteps() {
        var currentStepsView = findViewById<TextView>(R.id.stepsTaken)
        var button = findViewById<TextView>(R.id.button)
        button.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset total steps", Toast.LENGTH_SHORT).show()
            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            currentStepsView.text = 0.toString()
            currentSteps = 0
            // This will save the data
            saveData()
        }

        button.setOnLongClickListener {
            textView.text = 0.toString()
            currentStepsView.text = 0.toString()
            previousTotalSteps = 0
            currentSteps = 0
            totalSteps = 0

            saveData()
            true
        }
    }
    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putInt("key1", totalSteps)
        editor.putInt("key2", stepsToday)
        editor.putInt("DATE_KEY", currentDate)

        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getInt("key1", 0)

        val savedDate = sharedPreferences.getInt("DATE_KEY",0)
        val todayStepNumber = sharedPreferences.getInt("key2", 0)
        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")
        Log.d("CURRENT DATE ", "$currentDate")
        Log.d("SAVED DATE", "$savedDate")
        if (currentDate != savedDate) {
            previousTodaysSteps = todayStepNumber
            stepsToday = 0
            stepsTodayView.text = 0.toString()
        }
        totalSteps = savedNumber
        previousTotalSteps = totalSteps

    }

    

}