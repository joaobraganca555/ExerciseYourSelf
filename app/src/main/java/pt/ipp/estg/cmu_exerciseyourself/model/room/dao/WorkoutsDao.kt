package pt.ipp.estg.cmu_exerciseyourself.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Status

@Dao
interface WorkoutsDao {

    @Query("SELECT * FROM Workouts")
    fun getAllWorkouts():LiveData<List<Workouts>>

    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'PLANNED'")
    fun getAllPlannedWorkouts():LiveData<List<Workouts>>

    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'SUCCESSFULLY'")
    fun getAllSuccessfullyWorkouts():LiveData<List<Workouts>>

    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'FAILED'")
    fun getAllFailedWorkouts():LiveData<List<Workouts>>

    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'EXPIRED'")
    fun getAllExpiredWorkouts():LiveData<List<Workouts>>

    @Query("UPDATE Workouts SET status = :stateOfWorkout WHERE Workouts.id = :id")
    fun updateStateWorkout(id:String, stateOfWorkout:Status)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkout(workout:Workouts):Long
}