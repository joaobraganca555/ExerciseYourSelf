package pt.ipp.estg.cmu_exerciseyourself.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Measurements (
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var height:Float,
    var weight:Float,
    var percFat:Double,
    var date:String)