package pt.ipp.estg.cmu_exerciseyourself.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Coordinates(
    val lat:Double,
    val lng:Double,
    val workoutId:Int?,
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
){

    fun copy(
        lat:Double = this.lat,
        lng:Double = this.lng,
        workoutId: Int? = this.workoutId
        ) = Coordinates(lat,lng,workoutId,null)
}

