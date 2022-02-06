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
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.api.LogDescriptor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.ipp.estg.cmu_exerciseyourself.databinding.ActivityMainBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.services.BackgroundTrackActivity
import pt.ipp.estg.cmu_exerciseyourself.ui.authentication.AuthenticationActivity
import pt.ipp.estg.cmu_exerciseyourself.ui.exercise.WorkoutsViewModel
import pt.ipp.estg.cmu_exerciseyourself.utils.*
import java.time.LocalDateTime
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(),IServiceController {
    lateinit var fitnessRepository:FitnessRepository
    lateinit var binding: ActivityMainBinding
    var locationService: BackgroundTrackActivity? = null
    lateinit var navController:NavController
    var workoutsViewModel:WorkoutsViewModel? = null
    private val REQUEST_AUTHENTICATION = 666
    lateinit var toolbar:Toolbar
    var menu:Menu? = null

    val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            workoutsViewModel?.let {
                val currentLat =  intent?.getDoubleExtra("lat",0.0)
                val currentLong =  intent?.getDoubleExtra("long",0.0)
                val distance =  intent?.getDoubleExtra("distance",0.0)

                if(currentLat != 0.0 && currentLong != 0.0 && distance != null){
                    it.setCurrentPosition(LatLng(currentLat!!,currentLong!!))
                    it.setDistance(distance)
                }else{
                    Toast.makeText(baseContext,"Service Finished", Toast.LENGTH_SHORT).show()
                }
            }
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

        workoutsViewModel = ViewModelProvider(this).get(WorkoutsViewModel::class.java)
        fitnessRepository = FitnessRepository(application)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView
        navView.menu.getItem(2).isEnabled = false
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_health, R.id.navigation_discover,R.id.navigation_exercise,
                R.id.navigation_comunity
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val floatingButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        floatingButton.setOnClickListener{
            navController.navigate(R.id.navigation_automatic_exercise)
        }

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
                    handleAccessDenied()
                }
            }
        }else if(requestCode == PERMISSION_REQUEST_CODE){
            Log.d("asd", "onRequestPermissionsResult: location entrou !!")
            when {
                grantResults.isEmpty() ->
                    Log.d("asd", "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    Log.d("asd", "onRequestPermissionsResult: location access !!")
                else -> {
                    handleAccessDenied()
                }
            }
        }
    }

    private fun handleAccessDenied(){
        Snackbar.make(
            binding.container,
            "Acesso à localização foi negada.Algumas funcionalidades serão desativadas.",
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

    override fun startAutomaticExercise() {
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

    override fun stopAutomaticExercise() {
        val intent = Intent("pt.ipp.estg.sensorapp.src.MainActivity");
        sendBroadcast(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun openAddActivity(){
        navController.navigate(R.id.navigation_addManual_Exercise)
    }

    override fun openExerciseView() {
        navController.navigate(R.id.navigation_exercise)
        Toast.makeText(baseContext,"Treino Registado com sucesso",Toast.LENGTH_LONG).show()
    }

    override fun openMeasurementView() {
        navController.navigate(R.id.navigation_measurements)
        Toast.makeText(baseContext,"Medições atualizadas",Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_measurements -> {
                navController.navigate(R.id.navigation_measurements)
            }
            R.id.navigation_settings -> {
                navController.navigate(R.id.navigation_userProfile)
            }
            R.id.navigation_logout -> {
                Firebase.auth.signOut()
                val intent = Intent(this, AuthenticationActivity::class.java)
                startActivityForResult(intent, REQUEST_AUTHENTICATION)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}