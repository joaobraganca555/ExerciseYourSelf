package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts

class WorkoutsViewModel(application: Application):AndroidViewModel(application) {
    val fitnessRepo:FitnessRepository
    private val plannedWorkouts:LiveData<List<Workouts>>
    private val onGoingWorkout:MutableLiveData<Workouts> = MutableLiveData()
    private val currentPosition:MutableLiveData<LatLng> = MutableLiveData()
    private var allPositions:MutableLiveData<ArrayList<LatLng>> = MutableLiveData()
    val distance:MutableLiveData<Double> = MutableLiveData()

    init{
        fitnessRepo = FitnessRepository(application)
        plannedWorkouts = fitnessRepo.getAllPlannedWorkouts()
        allPositions.value = ArrayList()
    }

    fun isTrackingActivity():Boolean = (onGoingWorkout.value != null)

    fun getAllPlannedWorkouts():LiveData<List<Workouts>> = plannedWorkouts

    fun getOnGoingWorkout():LiveData<Workouts>{
        return this.onGoingWorkout
    }

    fun setOnGoingWorkout(workout: Workouts?){
        this.onGoingWorkout.value = workout
    }

    fun setCurrentPosition(currentPos:LatLng){
        this.currentPosition.value = currentPos
        this.allPositions.value?.add(currentPos)
    }

    fun getCurrentPosition():LiveData<LatLng>{
        return this.currentPosition
    }

    fun setDistance(distance:Double){
        this.distance.value = distance
    }

    fun getDistance():LiveData<Double>{
        return this.distance
    }

    fun getAllPositions():LiveData<ArrayList<LatLng>>{
        return this.allPositions
    }

}