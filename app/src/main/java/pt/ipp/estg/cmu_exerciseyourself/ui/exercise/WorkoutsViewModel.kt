package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts

class WorkoutsViewModel(application: Application):AndroidViewModel(application) {
    val fitnessRepo:FitnessRepository
    val plannedWorkouts:LiveData<List<Workouts>>
    val onGoingWorkout:MutableLiveData<Workouts> = MutableLiveData()

    init{
        fitnessRepo = FitnessRepository(application)
        plannedWorkouts = fitnessRepo.getAllPlannedWorkouts()
    }

    fun isTrackingActivity():Boolean = (onGoingWorkout.value != null)
    fun getAllPlannedWorkouts():LiveData<List<Workouts>> = plannedWorkouts
    fun getOnGoingWorkout():LiveData<Workouts>{
        return this.onGoingWorkout
    }
    fun setOnGoingWorkout(workout: Workouts){
        this.onGoingWorkout.value = workout
    }
}