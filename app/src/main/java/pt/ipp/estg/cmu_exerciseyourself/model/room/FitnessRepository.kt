package pt.ipp.estg.cmu_exerciseyourself.model.room

import android.app.Application
import androidx.lifecycle.LiveData
import pt.ipp.estg.cmu_exerciseyourself.model.room.dao.WorkoutsDao
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Status

class FitnessRepository(val application: Application) {
    val workoutsDao: WorkoutsDao

    init{
        workoutsDao = FitnessDb.getInstance(application).WorkoutsDao()
    }

    fun getAllWorkouts(): LiveData<List<WorkoutWithCoord>>{
        return workoutsDao.getAllWorkouts()
    }

    fun getAllPlannedWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllPlannedWorkouts()
    }

    fun getAllSuccessfullyWorkouts(): LiveData<List<WorkoutWithCoord>>{
        return workoutsDao.getAllSuccessfullyWorkouts()
    }

    fun getAllFailedWorkouts(): LiveData<List<WorkoutWithCoord>>{
        return workoutsDao.getAllFailedWorkouts()
    }

    fun getAllExpiredWorkouts(): LiveData<List<WorkoutWithCoord>>{
        return workoutsDao.getAllExpiredWorkouts()
    }

    fun updateStateWorkout(id:String, stateOfWorkout: Status){
        workoutsDao.updateStateWorkout(id,stateOfWorkout)
    }

    fun insertWorkoutWithCoord(workout:WorkoutWithCoord){
        return workoutsDao.insertWorkoutsWithCoord(workout)
    }

    fun deleteAllWorkouts(){
        workoutsDao.deleteAllWorkouts()
    }

    fun deleteAllCoord(){
        workoutsDao.deleteAllCoord()
    }

    fun getAllCoord():LiveData<List<Coordinates>>{
        return workoutsDao.getAllCoord()
    }
}