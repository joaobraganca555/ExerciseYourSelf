package pt.ipp.estg.cmu_exerciseyourself.model.models

data class UserProfile (
    var id:Int,
    var name:String,
    var age:Int,
    var height:Float,
    var weight:Float,
    var email:String)