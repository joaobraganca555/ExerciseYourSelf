package pt.ipp.estg.cmu_exerciseyourself.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProfile (
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var name:String,
    var age:Int,
    var height:Float,
    var weight:Float,
    var email:String)