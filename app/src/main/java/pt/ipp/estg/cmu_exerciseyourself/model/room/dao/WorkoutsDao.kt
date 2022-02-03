package pt.ipp.estg.cmu_exerciseyourself.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Status

@Dao
interface WorkoutsDao {
    @Transaction
    @Query("SELECT * FROM Workouts")
    fun getAllWorkouts():LiveData<List<Workouts>>

    @Transaction
    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'PLANNED'")
    fun getAllPlannedWorkouts():LiveData<List<Workouts>>

    @Transaction
    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'SUCCESSFULLY'")
    fun getAllSuccessfullyWorkouts():LiveData<List<Workouts>>

    @Transaction
    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'FAILED'")
    fun getAllFailedWorkouts():LiveData<List<Workouts>>

    @Transaction
    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'EXPIRED'")
    fun getAllExpiredWorkouts():LiveData<List<Workouts>>

    @Transaction
    @Query("SELECT * FROM Workouts WHERE Workouts.status = 'FAILED' AND Workouts.status = 'SUCCESSFULLY'")
    fun getAllFinishedWorkouts():LiveData<List<Workouts>>

    @Transaction
    @Query("UPDATE Workouts SET status = :stateOfWorkout WHERE Workouts.workoutId = :id")
    fun updateStateWorkout(id:String, stateOfWorkout:Status)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkout(workout:Workouts):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoord(coord: Coordinates)

    @Query("SELECT * FROM Coordinates")
    fun getAllCoord():LiveData<List<Coordinates>>

    @Transaction
    @Insert
    fun insertWorkoutsWithCoord(workoutWithCoord: WorkoutWithCoord){
        val workoutId = insertWorkout(workout = workoutWithCoord.workout)
        for(coord in workoutWithCoord.coordinates){
            val coord = coord.copy(workoutId = workoutId.toInt())
            insertCoord(coord)
        }
    }

    @Query("DELETE FROM Workouts")
    fun deleteAllWorkouts()

    @Query("DELETE FROM Coordinates")
    fun deleteAllCoord()
}
