package io.chronostech.awasgempabumi.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Infogempa {
    @SerializedName("gempa")
    @Expose
    var gempa: List<Gempa>? = null
}