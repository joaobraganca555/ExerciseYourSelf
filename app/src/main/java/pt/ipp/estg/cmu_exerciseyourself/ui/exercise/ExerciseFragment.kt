package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.newFixedThreadPoolContext
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentExerciseBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt

class ExerciseFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var myContext: IServiceController
    lateinit var supportMapFragment: SupportMapFragment
    lateinit var workoutViewModel: WorkoutsViewModel
    val current = LatLng(37.129665, -8.669586)
    var marker = MarkerOptions().position(LatLng(37.129665, -8.669586))
    var listPoints = ArrayList<LatLng>()
    lateinit var polyOptions: PolylineOptions
    var googlemap: GoogleMap? = null


    var timer = object : Timer() {}
    lateinit var timerTask: TimerTask
    var time: Double = 0.0

    var distance = 0.0
    var caloriesBurned = 0
    var weight = 0.0

    lateinit var repository:FitnessRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as IServiceController
        repository = FitnessRepository(requireActivity().application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workoutViewModel = ViewModelProvider(requireActivity()).get(WorkoutsViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        repository.getCurrentMeasurement().observe(viewLifecycleOwner) {
            weight = it?.weight!!
        }

        binding.btnStart.setOnClickListener {
            startTimer()
            myContext.startAutomaticExercise()
            binding.btnStart.isEnabled = false
            binding.btnStart.isClickable = false
        }

        binding.btnStop.setOnClickListener {
            timerTask.cancel()
            myContext.stopAutomaticExercise()
            binding.btnStart.isEnabled = true
            binding.btnStart.isClickable = true
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        polyOptions = PolylineOptions()
        workoutViewModel.getCurrentPosition().observe(viewLifecycleOwner) {
            googlemap?.apply {
                animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
                clear()
                addMarker(
                    MarkerOptions().position(it)
                )
                polyOptions.add(it)
                addPolyline(polyOptions)
            }
        }

        workoutViewModel.getDistance().observe(viewLifecycleOwner) {
            distance = it.toString().toDouble()
            binding.totalDistance.text = it.toString()
            binding.calories.text = getCalories()
        }

        return root
    }

    private fun getCalories():String {
        caloriesBurned = (weight * distance).toInt()
        return caloriesBurned.toString()
    }

    private fun startTimer() {
        time = 0.0
        timerTask = object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    time++
                    updateTimer(getTimerText())
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 1000)
    }

    fun updateTimer(timeText: String) {
        binding.timer.setText(timeText)
    }

    fun getTimerText(): String {
        var rounded = time.roundToInt()

        var seconds = ((rounded % 86400) % 3600) % 60
        var minutes = ((rounded % 86400) % 3600) / 60
        var hours = ((rounded % 86400) / 3600)

        return formatTime(seconds, minutes, hours)
    }

    private fun formatTime(seconds: Int, minutes: Int, hours: Int): String {
        return String.format("%02d", hours) + " : " + String.format(
            "%02d",
            minutes
        ) + " : " + String.format("%02d", seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googlemap = googleMap
        googleMap?.apply {
            addMarker(
                marker
            )
            addPolyline(polyOptions)
            animateCamera(CameraUpdateFactory.newLatLngZoom(current, 16f));
        }
    }
}