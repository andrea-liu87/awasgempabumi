package io.chronostech.awasgempabumi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Gempa {
    @SerializedName("Tanggal")
    @Expose
    var tanggal: String? = null

    @SerializedName("Jam")
    @Expose
    var jam: String? = null

    @SerializedName("DateTime")
    @Expose
    var dateTime: String? = null

    @SerializedName("Coordinates")
    @Expose
    var coordinates: String? = null

    @SerializedName("Lintang")
    @Expose
    var lintang: String? = null

    @SerializedName("Bujur")
    @Expose
    var bujur: String? = null

    @SerializedName("Magnitude")
    @Expose
    var magnitude: String? = null

    @SerializedName("Kedalaman")
    @Expose
    var kedalaman: String? = null

    @SerializedName("Wilayah")
    @Expose
    var wilayah: String? = null

    @SerializedName("Dirasakan")
    @Expose
    var dirasakan: String? = null
}