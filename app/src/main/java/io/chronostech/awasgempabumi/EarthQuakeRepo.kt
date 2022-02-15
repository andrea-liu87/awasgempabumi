package io.chronostech.awasgempabumi

import io.chronostech.awasgempabumi.model.EarthQuakeResponse
import retrofit2.Response
import javax.inject.Inject


class EarthQuakeRepo @Inject constructor(private val retrofitService : API) {
    suspend fun getResponse(): Response<EarthQuakeResponse> = retrofitService.getResponse()

    suspend fun getResponseBigMagnitude(): Response<EarthQuakeResponse> =
        retrofitService.getResponseBigMagnitude()
}
