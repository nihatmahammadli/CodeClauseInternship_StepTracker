package com.nihatmahammadli.myapplication.presentation.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.nihatmahammadli.myapplication.databinding.FragmentHomePageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomePage : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentHomePageBinding
    private var sensorManager: SensorManager? = null

    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private val requestActivityRecognitionPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                registerStepSensor()
            } else {
                Toast.makeText(requireContext(), "Give permission for step counting", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        sensorManager = requireContext().getSystemService(SensorManager::class.java)

        loadData()
        setupResetUi()
        setTime()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        running = true
        checkPermissionAndStart()
    }

    override fun onPause() {
        super.onPause()
        running = false
        unregisterStepSensor()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!running || event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return

        totalSteps = event.values[0]
        val currentSteps = (totalSteps - previousTotalSteps).toInt().coerceAtLeast(0)

        binding.count.text = currentSteps.toString()
        binding.circularProgressBar.setProgressWithAnimation(currentSteps.toFloat())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                registerStepSensor()
            } else {
                requestActivityRecognitionPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        } else {
            registerStepSensor()
        }
    }

    private fun registerStepSensor() {
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null) {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Toast.makeText(requireContext(), "There is no sensor in this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun unregisterStepSensor() {
        sensorManager?.unregisterListener(this)
    }

    private fun setupResetUi() {
        binding.resetBtn.setOnClickListener {
            previousTotalSteps = totalSteps
            binding.count.text = "0"
            binding.circularProgressBar.setProgressWithAnimation(0f)
            saveData()
            Toast.makeText(requireContext(), "Step count reset", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData() {
        requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE).edit {
            putFloat("key1", previousTotalSteps)
        }
    }

    private fun loadData() {
        val savedNumber = requireContext()
            .getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            .getFloat("key1", 0f)
        previousTotalSteps = savedNumber
    }

    private fun setTime(){
        binding.time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}
