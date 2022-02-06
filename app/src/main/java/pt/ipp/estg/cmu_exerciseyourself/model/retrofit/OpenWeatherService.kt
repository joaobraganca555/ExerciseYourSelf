package pt.ipp.estg.cmu_exerciseyourself.model.retrofit

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET("forecast?&appid=799d052dff5cfa469ea6513f3a274a09")
    fun getForecast(@Query("q") place:String): Call<OpenWeatherResponse>

    companion object{
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        fun retrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getApi(): OpenWeatherService {
            return this.retrofitInstance().create(OpenWeatherService::class.java)
        }
    }
}




