package pt.ipp.estg.cmu_exerciseyourself.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Measurements

@Dao
interface MeasurementsDao {
    @Insert
    fun insertMeasurement(measurement: Measurements)

    @Query("SELECT * FROM Measurements")
    fun getAllMeasurements():LiveData<List<Measurements>>

    @Query("SELECT * FROM Measurements ORDER BY Measurements.date DESC LIMIT 1" )
    fun getCurrentMeasurement():LiveData<Measurements>
}