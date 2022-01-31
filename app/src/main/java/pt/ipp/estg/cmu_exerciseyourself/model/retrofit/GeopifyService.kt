package pt.ipp.estg.cmu_exerciseyourself.model.retrofit

import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.errorprone.annotations.Var
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GeoapifyService {
    @GET("places?categories=sport.fitness&filter=circle:-8.20094,41.36735,{radius}&bias=proximity:-8.20094,41.36735&limit=20&apiKey=d620983b61cb4f7fb733a8a9cdc4bbcc")
    fun findPlaces(@Query("radius", encoded = true)radius:String): Call<ResponseObject>
}