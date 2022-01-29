package pt.ipp.estg.cmu_exerciseyourself.model.room

import android.app.Application
import androidx.lifecycle.LiveData
import pt.ipp.estg.cmu_exerciseyourself.model.room.dao.WorkoutsDao
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Status

class FitnessRepository(val application: Application) {
    val workoutsDao: WorkoutsDao

    init{
        workoutsDao = FitnessDb.getInstance(application).WorkoutsDao()
    }

    fun getAllWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllWorkouts()
    }

    fun getAllPlannedWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllPlannedWorkouts()
    }

    fun getAllSuccessfullyWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllSuccessfullyWorkouts()
    }

    fun getAllFailedWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllFailedWorkouts()
    }

    fun getAllExpiredWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllExpiredWorkouts()
    }

    fun updateStateWorkout(id:String, stateOfWorkout: Status){
        return workoutsDao.updateStateWorkout(id,stateOfWorkout)
    }

    fun insertWorkout(workout: Workouts){
        return workoutsDao.insertWorkout(workout)
    }
}