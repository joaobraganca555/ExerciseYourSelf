package pt.ipp.estg.cmu_exerciseyourself.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.Status

@Entity
data class Workouts(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    val sport: String,
    val status: String,
    val duration:Int,
    val distance:Int,
    val footsteps:Int,
    val local:String,
    val beginDate:String,
    val finishedDate:String)