package pt.ipp.estg.cmu_exerciseyourself.model.room

import android.app.Application
import androidx.lifecycle.LiveData
import pt.ipp.estg.cmu_exerciseyourself.model.room.dao.MeasurementsDao
import pt.ipp.estg.cmu_exerciseyourself.model.room.dao.WorkoutsDao
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Measurements
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Status

class FitnessRepository(val application: Application) {
    val workoutsDao: WorkoutsDao
    val measurementsDao: MeasurementsDao

    init{
        workoutsDao = FitnessDb.getInstance(application).WorkoutsDao()
        measurementsDao = FitnessDb.getInstance(application).MeasurementsDao()
    }

    fun getAllWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllWorkouts()
    }

    fun getAllPlannedWorkouts(): LiveData<List<Workouts>>{
        return workoutsDao.getAllPlannedWorkouts()
    }

    fun getAllFinishedWorkouts():LiveData<List<Workouts>>{
        return workoutsDao.getAllFinishedWorkouts()
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

    fun insertMeasurement(measurement: Measurements){
        measurementsDao.insertMeasurement(measurement)
    }

    fun getAllMeasurements():LiveData<List<Measurements>>{
        return measurementsDao.getAllMeasurements()
    }

    fun getCurrentMeasurement():LiveData<Measurements>{
        return measurementsDao.getCurrentMeasurement()
    }
}