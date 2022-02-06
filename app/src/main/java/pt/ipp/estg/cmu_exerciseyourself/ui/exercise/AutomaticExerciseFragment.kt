package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentExerciseBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.Status
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AutomaticExerciseFragment : Fragment(), OnMapReadyCallback, SensorEventListener {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    var db = FirebaseFirestore.getInstance()
    private lateinit var myContext: IServiceController
    lateinit var workoutViewModel: WorkoutsViewModel
    val current = LatLng(41.36683124768688, -8.19474151019716)
    var marker = MarkerOptions().position(LatLng(41.36683124768688, -8.19474151019716))
    var workoutWithCoord: WorkoutWithCoord? = null

    var listCoordinates = ArrayList<Coordinates>()

    lateinit var polyOptions: PolylineOptions
    var googlemap: GoogleMap? = null

    private var sensorManager: SensorManager? = null

    private var timer = object : Timer() {}
    private var timerTask: TimerTask? = null

    var time: Double = 0.0

    private lateinit var beginDate: LocalDateTime
    lateinit var endDate: LocalDateTime

    var distance = 0.0
    private var caloriesBurned = 0
    private var weight = 0.0

    private var currentSteps = 0
    private var done = false

    private lateinit var repository: FitnessRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as IServiceController
        repository = FitnessRepository(requireActivity().application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workoutViewModel = ViewModelProvider(requireActivity()).get(WorkoutsViewModel::class.java)

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Log.d("asd", "No sensor detected on this device.")
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
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
            if (it == null) {
                weight = 70.0
                Toast.makeText(
                    context,
                    "Nenhuma medição encontrada, porfavor adicionar uma",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                weight = it.weight

            }
        }

        workoutViewModel.getOnGoingWorkout().observe(viewLifecycleOwner) {
            if (it == null) {

                binding.btnStart.isEnabled = true
                binding.btnStop.isEnabled = false
            } else {
                binding.btnStart.isEnabled = false
                binding.btnStop.isEnabled = true
            }
        }

        binding.btnStart.setOnClickListener {
            beginDate = LocalDateTime.now()
            //reset polyline
            polyOptions = PolylineOptions()
            startTimer(0)
            myContext.startAutomaticExercise()

            workoutViewModel.setOnGoingWorkout(
                Workouts(
                    sport = "",
                    duration = "",
                    status = "",
                    distance = 0.0,
                    local = "",
                    footsteps = 0,
                    beginDate = beginDate.toString(),
                    finishedDate = "",
                    workoutId = null,
                    calories = 0
                )
            )

        }

        binding.btnStop.setOnClickListener {

            workoutViewModel.setOnGoingWorkout(null)

            endDate = LocalDateTime.now()
            timerTask?.cancel()
            myContext.stopAutomaticExercise()

            saveWorkout()
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

                var coord = Coordinates(it.latitude, it.longitude, null, null)
                listCoordinates.add(coord)
            }
        }

        workoutViewModel.getDistance().observe(viewLifecycleOwner) {
            distance = it.toString().toDouble()
            binding.totalDistance.text = it.toString()
            binding.calories.text = getCalories()
        }

        return root
    }

    override fun onPause() {
        super.onPause()
        timerTask?.cancel()
    }

    override fun onStop() {
        super.onStop()
        timerTask?.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        if (workoutViewModel.isTrackingActivity()) {
            beginDate = LocalDateTime.parse(workoutViewModel.getOnGoingWorkout().value?.beginDate)

            val sec = ChronoUnit.SECONDS.between(beginDate, LocalDateTime.now()).toInt()

            startTimer(sec)

            var allPositions = workoutViewModel.getAllPositions().value

            if (allPositions != null) {
                for(i in allPositions){
                    var coord = Coordinates(i.latitude, i.longitude, null, null)
                    listCoordinates.add(coord)
                }
            }

            if (allPositions != null) {
                for (i in allPositions){
                    polyOptions.add(i)
                }
                googlemap?.addPolyline(polyOptions)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveWorkout() {
        var workout = Workouts(
            sport = Sport.RUNNING_OUTDOOR.toString(),
            duration = ChronoUnit.MINUTES.between(beginDate, endDate).toInt().toString(),
            status = Status.SUCCESSFULLY.toString(),
            distance = distance,
            local = "null",
            footsteps = currentSteps,
            beginDate = beginDate.toString(),
            finishedDate = endDate.toString(),
            workoutId = null,
            calories = caloriesBurned
        )

        Executors.newFixedThreadPool(1).execute {

            val listCoord = listCoordinates

            workoutWithCoord = WorkoutWithCoord(workout, listCoord)
            repository.insertWorkoutWithCoord(workoutWithCoord!!)
        }
        onAlertDialog(binding.container, workout)
    }

    private fun getCalories(): String {
        caloriesBurned = (weight * distance).toInt()
        return caloriesBurned.toString()
    }

    private fun startTimer(seconds: Int) {
        time = seconds.toDouble()
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
        binding.timer.text = timeText
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

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!done) {
            currentSteps = event!!.values[0].toInt()
            done = true
        }
        currentSteps += event!!.values[0].toInt()
        Toast.makeText(
            myContext as Context,
            "Current steps: ".plus(currentSteps.toString()),
            Toast.LENGTH_SHORT
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAlertDialog(view: View, workout: Workouts) {
        val sharedPreferences =
            (myContext as Context).getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedUser = sharedPreferences.getString("user", "user")

        var record = workout.beginDate + " " +
                workout.sport + " " + workout.distance.toString() + " " + savedUser

        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Treino Terminado")
        builder.setMessage("Deseja publicar o treino com a comunidade?")

        builder.setPositiveButton(
            "Publicar"
        ) { dialog, id ->
            var workoutsRef = db.collection("comunity").document("workouts")
            workoutsRef.update(
                "published", FieldValue.arrayUnion("$record")
            )
        }

        builder.setNegativeButton(
            "Desta Vez Não"
        ) { dialog, id ->
        }
        builder.show()
    }
}



