package pt.ipp.estg.cmu_exerciseyourself.ui.health

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentHealthBinding

class HealthFragment : Fragment(),SensorEventListener {
    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousSteps = 0f
    private lateinit var myContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if(stepSensor == null){
            Log.d("asd", "No sensor detected on this device.")
        }else{
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        loadData()
        resetSteps()
        sensorManager = myContext.getSystemService(SENSOR_SERVICE) as SensorManager
        binding.circularProgressBar.apply {
            progressMax = 10000f
        }

        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousSteps.toInt()

            binding.txtFootSteps.text = ("$currentSteps")
            binding.circularProgressBar.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun loadData(){
        val sharedPreferences = myContext.getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1",0f)

        previousSteps = savedNumber
    }

    private fun saveData(){
        val sharedPreferences = myContext.getSharedPreferences("myPrefs",Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1",previousSteps)
        editor.apply()
    }


    private fun resetSteps(){
        binding.txtFootSteps.setOnClickListener {
            previousSteps = totalSteps
            binding.txtFootSteps.text = "0"
            saveData()
        }
    }
}