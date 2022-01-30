package pt.ipp.estg.cmu_exerciseyourself.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Measurements (
    @PrimaryKey(autoGenerate = true)
    var id:Int?,
    var height:Double,
    var weight:Double,
    var belly:Double,
    var chest:Double,
    var percFat:Double,
    var date:String)