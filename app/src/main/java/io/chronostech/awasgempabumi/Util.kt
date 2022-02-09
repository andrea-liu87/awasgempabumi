package io.chronostech.awasgempabumi

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Util {
    companion object {
        private val acehBound = LatLngBounds(LatLng(2.00, 95.00), LatLng(6.00, 99.00))
        private val sumutBound = LatLngBounds(LatLng(1.00, 97.00), LatLng(5.00, 101.00))
        private val jakartaBound = LatLngBounds(LatLng(-7.00, 106.00), LatLng(-6.00, 107.00))
        private val papuaBound = LatLngBounds(LatLng(-6.00, 131.00), LatLng(-1.00, 141.00))
        private val papuaBaratBound = LatLngBounds(LatLng(-5.00, 130.00), LatLng(0.00, 138.00))

        val province = mapOf(
            "Aceh" to acehBound,
            "Sumatra Utara" to sumutBound,
            "Jakarta" to jakartaBound,
            "Papua" to papuaBound,
            "Papua Barat" to papuaBaratBound
        )
    }
}