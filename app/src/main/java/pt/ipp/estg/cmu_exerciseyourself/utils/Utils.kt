package pt.ipp.estg.cmu_exerciseyourself.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar

const val PERMISSION_REQUEST_CODE = 555
data class WorkoutChat(val data:String,val sport:String,val distance:String,val user:String?)

enum class Sport{
    RUNNING_OUTDOOR,
    RUNNING_INDOOR,
    WALKING,
    GYM,
    HOME_TRAINING,
    OTHER
}

enum class ViewMode{
    ADD_MEASUREMENT_VIEW,
    ADD_ACTIVITY_VIEW
}

enum class Status{
    PLANNED,
    SUCCESSFULLY,
    FAILED,
    EXPIRED
}

fun Context.hasPermission(permission: String): Boolean {

    // Background permissions didn't exit prior to Q, so it's approved by default.
    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
        return true
    }

    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissionWithRationale(
    permission: String,
    requestCode: Int,
    snackbar: Snackbar
) {
    val provideRationale = shouldShowRequestPermissionRationale(permission)

    if (provideRationale) {
        snackbar.show()
    } else {
        requestPermissions(arrayOf(permission), requestCode)
    }
}
