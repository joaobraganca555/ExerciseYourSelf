package pt.ipp.estg.cmu_exerciseyourself.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.Status

@Entity
data class Workouts(
    @PrimaryKey(autoGenerate = true)
    val workoutId:Int?,
    val sport: String,
    val status: String,
    val duration:String,
    val distance:Double,
    val footsteps:Int,
    val calories:Int,
    val local:String,
    val beginDate:String,
    val finishedDate:String)