package pt.ipp.estg.cmu_exerciseyourself.services

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.ui.exercise.AutomaticExerciseFragment
import pt.ipp.estg.cmu_exerciseyourself.utils.LocationHelper
import pt.ipp.estg.cmu_exerciseyourself.utils.MyLocationListener
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

//Fused Location API Google Play Services
class BackgroundTrackActivity : Service() {
    private val binder = MyBinder()
    private var locationHelper = LocationHelper()
    private var previousLat: Double? = null
    private var previousLong: Double? = null
    private var totalDistance: Double = 0.0
    private var totalDuration: Long = 0
    private var beginDate: LocalDateTime? = null

    private val broadReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onReceive(context: Context?, intent: Intent?) {
            stopServiceForeGround()
        }
    }

    companion object {
        const val ONGOING_NOTIFICATION_ID = 12
        const val CHANNEL_ID = "channel1"
        var mLocation: Location? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        val intentFilter = IntentFilter()
        intentFilter.addAction("pt.ipp.estg.sensorapp.src.MainActivity")
        registerReceiver(broadReceiver, intentFilter)

        createNotificationChannel()

        //Build a notification for foreground service
        val pendingIntent: PendingIntent = Intent(this, AutomaticExerciseFragment::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Just Do It")
            .setContentText("Exercicío a decorrer")
            .setSmallIcon(R.mipmap.ic_launcher_running)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var currentLat: Double?
        var currentLong: Double?
        locationHelper.startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    mLocation = location
                    currentLat = mLocation?.latitude
                    currentLong = mLocation?.longitude

                    if (currentLat != null && currentLong != null) {
                        //If was not the first coordinate
                        if (previousLat != null && previousLong != null) {
                            totalDistance += calculateDistance(
                                previousLat!!,
                                previousLong!!,
                                currentLat!!,
                                currentLong!!
                            )
                        }
                        previousLat = currentLat
                        previousLong = currentLong
                        val i = Intent("pt.ipp.estg.sensorapp.src.BackgroundDetectActivities")
                        i.putExtra("lat", currentLat)
                        i.putExtra("long", currentLong)

                        val distance = BigDecimal(totalDistance).setScale(2, RoundingMode.HALF_EVEN)
                        i.putExtra("distance", distance.toDouble())

                        sendBroadcast(i)
                    }
                    mLocation?.let {
                        Log.d(
                            "asd",
                            "lat = ${it.latitude} e long = ${it.longitude} e alt = ${it.altitude} e dist = $totalDistance"
                        )
                    }
                }
            })
        //Set exercise begin date
        beginDate = LocalDateTime.now()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return this.binder
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "Service notification for foreground application"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun stopServiceForeGround() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            totalDuration = ChronoUnit.MINUTES.between(beginDate, LocalDateTime.now())
        }

        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf()

        //Build a intent with the action equals to intent filter action defined on the activity destination
        val i = Intent("pt.ipp.estg.sensorapp.src.BackgroundDetectActivities")
        sendBroadcast(i)
        locationHelper.stopUpdates()

    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(
                deg2rad(theta)
            )
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        dist *= 1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    inner class MyBinder : Binder() {
        fun getService(): BackgroundTrackActivity = this@BackgroundTrackActivity
    }
}