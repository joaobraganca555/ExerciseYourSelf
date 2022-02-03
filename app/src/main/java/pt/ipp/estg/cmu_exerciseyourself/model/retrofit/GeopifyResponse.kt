package pt.ipp.estg.cmu_exerciseyourself.model.retrofit

import com.google.gson.annotations.SerializedName


data class GeopifyResponseObject (
    @SerializedName("features" ) var features : ArrayList<Features> = arrayListOf()
)

data class Features (
    @SerializedName("properties" ) var properties : Properties? = Properties(),
)

data class Properties (
    @SerializedName("lon"           ) var lon          : Double?           = null,
    @SerializedName("lat"           ) var lat          : Double?           = null,
    @SerializedName("formatted"     ) var formatted    : String?           = null,
    @SerializedName("categories"    ) var categories   : ArrayList<String> = arrayListOf()
)