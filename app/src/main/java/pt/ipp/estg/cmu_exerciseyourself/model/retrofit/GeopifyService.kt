package pt.ipp.estg.cmu_exerciseyourself.model.retrofit

import pt.ipp.estg.cmu_exerciseyourself.model.models.Location
import retrofit2.Call
import retrofit2.http.GET

interface GeoapifyService {
    @GET("places?categories=sport.fitness&filter=circle:-8.266543,41.367291,10000&bias=proximity:-8.266543,41.367291&limit=20")
    fun getLocation(): Call<Location>
}