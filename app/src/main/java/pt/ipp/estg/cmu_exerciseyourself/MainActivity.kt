package pt.ipp.estg.cmu_exerciseyourself

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import pt.ipp.estg.cmu_exerciseyourself.databinding.ActivityMainBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.services.BackgroundTrackActivity
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.Status
import pt.ipp.estg.cmu_exerciseyourself.utils.hasPermission
import pt.ipp.estg.cmu_exerciseyourself.utils.requestPermissionWithRationale
import java.time.LocalDateTime
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(),IServiceController {
    lateinit var fitnessRepository:FitnessRepository
    lateinit var binding: ActivityMainBinding
    var locationService: BackgroundTrackActivity? = null
    lateinit var navController:NavController

    val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(baseContext,"Service Finished", Toast.LENGTH_SHORT).show()
        }
    }

    private val fineLocationRationalSnackbar by lazy {
        Snackbar.make(binding.container,
            "The fine location permission is needed for core functionality.",
            Snackbar.LENGTH_LONG
        ).setAction("Ok") {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE)
        }
    }

    companion object{
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fitnessRepository = FitnessRepository(application)

        fitnessRepository.getAllWorkouts().observe(this, {
            for (current in it){
                Log.d("asd", current.workout.toString() + current.coordinates.toString())
            }
        })

        fitnessRepository.getAllPlannedWorkouts().observe(this, {
            Log.d("asd", "planned=" + it.toString())
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            loadInfo()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        val floatingButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        floatingButton.setOnClickListener{
          //TODO
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_health, R.id.navigation_discover,R.id.navigation_exercise,
                R.id.navigation_comunity,R.id.navigation_manual_exercise
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val serviceConnection by lazy{
            object: ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    locationService = (service as BackgroundTrackActivity.MyBinder).getService()
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    locationService = null
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("asd", "onRequestPermissionResult()")

        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    Log.d("asd", "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    startService(Intent(this,BackgroundTrackActivity::class.java))
                else -> {
                    Snackbar.make(
                        binding.container,
                        "Fine location permission was denied but is needed for core functionality.",
                        Snackbar.LENGTH_LONG)
                        .setAction("Definições") {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    fun locationRequestAccepted():Boolean {
        val permissionApproved =
            applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        return (permissionApproved)
    }


    override fun onResume() {
        super.onResume()
        val intentFil= IntentFilter("pt.ipp.estg.sensorapp.src.BackgroundDetectActivities");
        registerReceiver(broadcastReceiver,intentFil);
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun startManualExercise() {
        if(locationRequestAccepted()) {
            startService(Intent(this,BackgroundTrackActivity::class.java))
        }else{
            requestPermissionWithRationale(
                Manifest.permission.ACCESS_FINE_LOCATION,
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                fineLocationRationalSnackbar
            )
        }
    }

    override fun stopManualExercise() {
        val intent = Intent("pt.ipp.estg.sensorapp.src.MainActivity");
        sendBroadcast(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadInfo(){
        Executors.newFixedThreadPool(1).execute {
            fitnessRepository.deleteAllWorkouts()
            fitnessRepository.deleteAllCoord()

            val workout = Workouts(sport = Sport.GYM.toString(), duration = 15,
                status = Status.PLANNED.toString(), distance = 15, local = "Vizela", footsteps = 5000,
                beginDate = LocalDateTime.now().toString(), finishedDate =  LocalDateTime.now().toString(), workoutId = null)
            val listCoord=ArrayList<Coordinates>()
            val workoutWithCoord = WorkoutWithCoord(workout,listCoord)
            fitnessRepository.insertWorkoutWithCoord(workoutWithCoord)

            val workout1 = Workouts(sport = Sport.GYM.toString(), duration = 25,
                status = Status.SUCCESSFULLY.toString(), distance = 25, local = "Moreira", footsteps = 5000,
                beginDate = LocalDateTime.now().toString(), finishedDate =  LocalDateTime.now().toString(), workoutId = null)
            val listCoord1=ArrayList<Coordinates>()
            listCoord1.add(Coordinates(13.232,123.21,null,null))
            listCoord1.add(Coordinates(23.232,143.21,null,null))
            val workoutWithCoord1= WorkoutWithCoord(workout1,listCoord1)
            fitnessRepository.insertWorkoutWithCoord(workoutWithCoord1)

            val workout2 = Workouts(sport = Sport.RUNNING_OUTDOOR.toString(), duration = 15,
                status = Status.PLANNED.toString(), distance = 15, local = "Vizela", footsteps = 5000,
                beginDate = LocalDateTime.now().toString(), finishedDate =  LocalDateTime.now().toString(), workoutId = null)
            val listCoord2=ArrayList<Coordinates>()
            val workoutWithCoord2 = WorkoutWithCoord(workout2,listCoord2)
            fitnessRepository.insertWorkoutWithCoord(workoutWithCoord2)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_measurements -> {
                navController.navigate(R.id.navigation_measurements)
                return true
            }
            R.id.navigation_settings -> {
                Log.d("asd", "onOptionsItemSelected: sett")
                return true
            }
            R.id.navigation_logout -> {
                Log.d("asd", "onOptionsItemSelected: logout")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}