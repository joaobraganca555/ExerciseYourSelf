package pt.ipp.estg.cmu_exerciseyourself.model.retrofit

import pt.ipp.estg.cmu_exerciseyourself.model.models.Location
import retrofit2.Call
import retrofit2.http.POST


interface GoogleMapsService {
    @POST("geolocate?key=")
    fun getLocation(): Call<Location>

}