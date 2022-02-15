package io.chronostech.awasgempabumi

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Util {
    companion object {
        private val acehBound = LatLngBounds(LatLng(2.00, 95.00), LatLng(6.00, 99.00))
        private val sumutBound = LatLngBounds(LatLng(1.00, 97.00), LatLng(5.00, 101.00))
        private val sumbarBound = LatLngBounds(LatLng(1.00, 98.00), LatLng(4.00, 102.00))
        private val riauBound = LatLngBounds(LatLng(2.00, 100.00), LatLng(3.00, 109.00))
        private val kepriBound = LatLngBounds(LatLng(-3.00, 101.00), LatLng(-1.00, 104.00))
        private val jambiBound = LatLngBounds(LatLng(-3.00, 101.00), LatLng(-1.00, 105.00))
        private val sumselBound = LatLngBounds(LatLng(-5.00, 102.00), LatLng(-1.00, 107.00))
        private val bengkuluBound = LatLngBounds(LatLng(-6.00, 101.00), LatLng(-2.00, 104.00))
        private val lampungBound = LatLngBounds(LatLng(-7.00, 103.00), LatLng(-3.00, 106.00))
        private val babelBound = LatLngBounds(LatLng(-4.00, 105.00), LatLng(-1.00, 109.00))

        private val jakartaBound = LatLngBounds(LatLng(-7.00, 106.00), LatLng(-6.00, 107.00))
        private val jabarBound = LatLngBounds(LatLng(-8.00, 106.00), LatLng(-5.00, 109.00))
        private val bantenBound = LatLngBounds(LatLng(-8.00, 105.00), LatLng(-5.00, 107.00))
        private val jatengBound = LatLngBounds(LatLng(-9.00, 108.00), LatLng(-6.00, 112.00))
        private val jogjaBound = LatLngBounds(LatLng(-9.00, 110.00), LatLng(-7.00, 111.00))
        private val jatimBound = LatLngBounds(LatLng(-9.00, 110.00), LatLng(-6.00, 115.00))
        private val baliBound = LatLngBounds(LatLng(-9.00, 114.00), LatLng(-8.00, 116.00))
        private val ntbBound = LatLngBounds(LatLng(-10.00, 115.00), LatLng(-8.00, 120.00))
        private val nttBound = LatLngBounds(LatLng(-11.00, 118.00), LatLng(-8.00, 126.00))

        //        27 . Sulawesi Tengah : 2ºLU-4ºLS dan 119º-125ºBT
//
//        28 . Sulawesi Selatan : 0ºLS-8ºLS dan 118º-122ºBT
//
//        29. Sulawesi Barat: 0ºLS-3ºLS dan 118º-120ºBT
//
//        30 . Sulawesi Tenggara : 2ºLS-7ºLS dan 120º-125ºBT
//
//        31 . Maluku : 0ºLS-9ºLS dan 124º-136ºBT
//
//        32 . Maluku utara : 3ºLU-º3LS dan 124º-129ºBT
        private val sulutBound = LatLngBounds(LatLng(0.00, 118.00), LatLng(8.00, 112.00))
        private val gorontaloBound = LatLngBounds(LatLng(0.00, 120.00), LatLng(1.00, 124.00))
        private val papuaBound = LatLngBounds(LatLng(-6.00, 131.00), LatLng(-1.00, 141.00))
        private val papuaBaratBound = LatLngBounds(LatLng(-5.00, 130.00), LatLng(0.00, 138.00))

        val province = mapOf(
            "Aceh" to acehBound,
            "Sumatra Utara" to sumutBound,
            "Sumatra Barat" to sumbarBound,
            "Riau" to riauBound,
            "Kepulauan Riau" to kepriBound,
            "Jambi" to jambiBound,
            "Sumatra Selatan" to sumselBound,
            "Bengkulu" to bengkuluBound,
            "Lampung" to lampungBound,
            "Bangka Belitung" to babelBound,
            "Jakarta" to jakartaBound,
            "Jawa Barat" to jabarBound,
            "Banten" to bantenBound,
            "Jawa Tengah" to jatengBound,
            "Jawa Timur" to jatimBound,
            "DIY Jogyakarta" to jogjaBound,
            "Bali" to baliBound,
            "NTB" to ntbBound,
            "NTT" to nttBound,
            "Papua" to papuaBound,
            "Papua Barat" to papuaBaratBound
        )
    }
}