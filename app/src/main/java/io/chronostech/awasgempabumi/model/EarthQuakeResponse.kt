package io.chronostech.awasgempabumi.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class EarthQuakeResponse {
    @SerializedName("Infogempa")
    @Expose
    var infogempa: Infogempa? = null
}