package io.chronostech.awasgempabumi

import io.chronostech.awasgempabumi.model.EarthQuakeResponse
import io.chronostech.awasgempabumi.model.Infogempa
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface API {
    @GET("gempadirasakan.json")
    suspend fun getResponse() : Response<EarthQuakeResponse>

    companion object {
        var apiInterface : API? = null
        var BASE_URL = "https://data.bmkg.go.id/DataMKG/TEWS/"

        fun create() : API {
            if (apiInterface == null) {
                val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()
                apiInterface = retrofit.create(API::class.java)
            }
            return apiInterface!!
        }
    }
}