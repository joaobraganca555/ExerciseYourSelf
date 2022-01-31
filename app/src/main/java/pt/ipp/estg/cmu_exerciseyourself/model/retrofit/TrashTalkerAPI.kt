package pt.ipp.estg.trashtalkerapp.retrofitService

import android.provider.MediaStore
import android.widget.ImageView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Request
import okhttp3.ResponseBody
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.ResponseObject
import retrofit2.http.*
import java.io.File

interface TrashtalkerAPI {
    @GET("places?categories=sport.fitness&filter=circle:-8.20094,41.36735,{radius}&bias=proximity:-8.20094,41.36735&limit=20&apiKey=d620983b61cb4f7fb733a8a9cdc4bbcc")
    fun findPlaces(@Path("radius")radius:String): Call<ResponseObject>

    companion object{
        //Use only with emulator
        private const val BASE_URL = "https://api.geoapify.com/v2/"

        fun retrofitInstance():Retrofit{
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getApi():TrashtalkerAPI{
            return this.retrofitInstance().create(TrashtalkerAPI::class.java)
        }

    }
}