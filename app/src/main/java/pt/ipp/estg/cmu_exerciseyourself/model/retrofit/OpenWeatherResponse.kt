package pt.ipp.estg.cmu_exerciseyourself.model.retrofit

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse (
    @SerializedName("cod"     ) var cod     : String?         = null,
    @SerializedName("list"    ) var list    : ArrayList<ListDays> = arrayListOf(),
    @SerializedName("city"    ) var city    : City?           = City()
)

data class ListDays (
    @SerializedName("main"       ) var main       : Main?              = Main(),
    @SerializedName("weather"    ) var weather    : ArrayList<Weather> = arrayListOf(),
    @SerializedName("dt_txt"     ) var dtTxt      : String?            = null
)

data class Main (
    @SerializedName("temp"       ) var temp      : Double? = null,
    @SerializedName("temp_min"   ) var tempMin   : Double? = null,
    @SerializedName("temp_max"   ) var tempMax   : Double? = null,
    @SerializedName("pressure"   ) var pressure  : Int?    = null,
    @SerializedName("humidity"   ) var humidity  : Int?    = null,
)

data class Weather (
    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("main"        ) var main        : String? = null,
    @SerializedName("description" ) var description : String? = null,
)

data class City (
    @SerializedName("id"         ) var id         : Int?    = null,
    @SerializedName("name"       ) var name       : String? = null,
    @SerializedName("coord"      ) var coord      : Coord?  = Coord(),
)

data class Coord (
    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lon" ) var lon : Double? = null
)