package pt.ipp.estg.cmu_exerciseyourself

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import pt.ipp.estg.cmu_exerciseyourself.databinding.ActivityMainBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.ServiceController
import pt.ipp.estg.cmu_exerciseyourself.services.BackgroundTrackActivity
import pt.ipp.estg.cmu_exerciseyourself.utils.hasPermission
import pt.ipp.estg.cmu_exerciseyourself.utils.requestPermissionWithRationale

class MainActivity : AppCompatActivity(),ServiceController {
    lateinit var binding: ActivityMainBinding
    var locationService: BackgroundTrackActivity? = null
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

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_health, R.id.navigation_discover,R.id.navigation_exercise, R.id.navigation_comunity
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
                Snackbar.make(
                        binding.container, "You approved FINE location, click start!",
                        Snackbar.LENGTH_LONG).show()
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

    override fun onStop() {
        super.onStop()
        //unregisterReceiver(broadcastReceiver)
    }
    override fun onPause() {
        super.onPause()
        //unregisterReceiver(broadcastReceiver)
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

}