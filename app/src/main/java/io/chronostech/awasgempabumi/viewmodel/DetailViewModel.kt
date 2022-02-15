package io.chronostech.awasgempabumi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.chronostech.awasgempabumi.EarthQuakeRepo
import io.chronostech.awasgempabumi.model.Gempa
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repo: EarthQuakeRepo) : ViewModel() {

    val gempa = MutableLiveData<Gempa>()
    val potensiTsunami = MutableLiveData<Boolean>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val errorMessage = MutableLiveData<String>()

    fun getPotensiTsunami(coordinates: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repo.getResponseBigMagnitude()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.infogempa?.gempa?.forEach {
                        if (coordinates == it.coordinates && !it.potensi!!.contains("Tidak")) {
                            potensiTsunami.value = true
                        }
                    }
                } else {
                    onError("Error : ${response.errorBody()}")
                }
            }
        }
    }


    private fun onError(message: String) {
        errorMessage.postValue(message)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}