package pt.ipp.estg.cmu_exerciseyourself.ui.home

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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentHealthBinding
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.ui.exercise.WorkoutsViewModel
import pt.ipp.estg.cmu_exerciseyourself.utils.ChallengesAdapter
import java.math.BigDecimal
import java.math.RoundingMode

class HomeFragment : Fragment(),SensorEventListener {
    private lateinit var binding: FragmentHealthBinding
    private lateinit var txtFootSteps: TextView
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var recView: RecyclerView
    private lateinit var txtOnGoingActivity: TextView

    private lateinit var repository: FitnessRepository

    private var sensorManager: SensorManager? = null
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHealthBinding.inflate(inflater, container, false)
        loadData()
        resetSteps()
        sensorManager = myContext.getSystemService(SENSOR_SERVICE) as SensorManager

        val root: View = binding.root

        circularProgressBar = binding.circularProgressBar
        txtFootSteps = binding.txtFootSteps
        recView = binding.recViewChallenges

        circularProgressBar.apply { progressMax = 10000f }

        var workoutsAdapter = ChallengesAdapter(ArrayList())

        txtOnGoingActivity = binding.txtOnGoing

        recView.apply {
            adapter = workoutsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        repository = FitnessRepository(requireActivity().application)

        repository.getCurrentMeasurement().observe(viewLifecycleOwner){
            binding.weight.text = it?.weight.toString()
            binding.height.text = it?.height.toString()
            binding.imc.text = (it?.weight?.div((it?.height).div(100)*(it?.height).div(100))
                ?.let { it1 -> BigDecimal(it1).setScale(2, RoundingMode.HALF_EVEN).toString() })
        }

        ViewModelProvider(this)[WorkoutsViewModel::class.java].getAllPlannedWorkouts()
            .observe(viewLifecycleOwner) {
                workoutsAdapter.updateList(it)
            }

        ViewModelProvider(this)[WorkoutsViewModel::class.java].getOnGoingWorkout()
            .observe(viewLifecycleOwner) {
                if (it != null)
                    txtOnGoingActivity.text = "A decorrer " + it.sport + " em " + it.local
                else
                    txtOnGoingActivity.text = "Sem atividades a decorrer"
            }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        totalSteps = event!!.values[0]
        val currentSteps = totalSteps.toInt() - previousSteps.toInt()

        txtFootSteps.text = ("$currentSteps")
        circularProgressBar.apply {
            setProgressWithAnimation(currentSteps.toFloat())
        }

        binding.circularProgressBarCal.apply {
            setProgressWithAnimation(currentSteps.toFloat()*0.04f)
        }
        binding.txtNumCal.text = (currentSteps.toFloat()*0.04f).toInt().toString()
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