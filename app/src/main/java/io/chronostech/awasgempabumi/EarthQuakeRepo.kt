package io.chronostech.awasgempabumi

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.chronostech.awasgempabumi.model.EarthQuakeResponse
import io.chronostech.awasgempabumi.model.Gempa
import io.chronostech.awasgempabumi.model.Infogempa
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class EarthQuakeRepo @Inject constructor(private val retrofitService : API) {
    suspend fun getResponse(): Response<EarthQuakeResponse> = retrofitService.getResponse()
}
