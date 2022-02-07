package io.chronostech.awasgempabumi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.chronostech.awasgempabumi.model.Gempa

class DetailViewModel : ViewModel() {

    val gempa = MutableLiveData<Gempa>()
}