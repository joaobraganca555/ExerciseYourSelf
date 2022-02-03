package pt.ipp.estg.trashtalkerapp.retrofitService

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.GeopifyResponseObject
import retrofit2.http.*

interface IGeopify {
    @GET("places?categories=sport.fitness&filter=circle:-8.20094,41.36735,5000&bias=proximity:-8.20094,41.36735&limit=20&apiKey=d620983b61cb4f7fb733a8a9cdc4bbcc")
    fun findPlaces(@Query("filter")filter:String, @Query("bias")bias:String): Call<GeopifyResponseObject>

    companion object{
        private const val BASE_URL = "https://api.geoapify.com/v2/"

        fun retrofitInstance():Retrofit{
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getApi():IGeopify{
            return this.retrofitInstance().create(IGeopify::class.java)
        }

    }
}