package pt.ipp.estg.cmu_exerciseyourself.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.Status
import java.time.LocalDateTime

@Entity
data class Workouts(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val sport: Sport,
    val status: Status,
    val duration:Int,
    val distance:Int,
    val local:String,
    val beginDate:LocalDateTime,
    val finishedDate:LocalDateTime)

