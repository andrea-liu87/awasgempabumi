package io.chronostech.awasgempabumi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.chronostech.awasgempabumi.EarthQuakeRepo
import io.chronostech.awasgempabumi.model.Gempa
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class EarthQuakeViewModel @Inject constructor(private val repo: EarthQuakeRepo) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val earthquakeList = MutableLiveData<List<Gempa>>()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    val loading = MutableLiveData<Boolean>()

    fun getAllEarthquake(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch{
            val response = repo.getResponse()
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    val list = response.body()?.infogempa?.gempa
                    earthquakeList.postValue(list!!)
                    loading.value = false
                } else {
                    onError("Error : ${response.errorBody()}")
                }
            }
        }
    }

    private fun onError(message: String){
        errorMessage.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}