package io.chronostech.awasgempabumi

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.chronostech.awasgempabumi.databinding.ListitemEarthquakeBinding
import io.chronostech.awasgempabumi.model.Gempa
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class EarthQuakeAdapter(val context: Context) :
    RecyclerView.Adapter<EarthQuakeAdapter.EarthQuakeViewHolder>() {
    private var data = listOf<Gempa>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthQuakeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListitemEarthquakeBinding.inflate(layoutInflater, parent, false)
        return EarthQuakeViewHolder(binding)
    }

    @ExperimentalTime
    override fun onBindViewHolder(holder: EarthQuakeViewHolder, position: Int) {
        val earthquake = data[position]

        holder.binding.tvPlace.text =
            earthquake.dirasakan?.replace("I", "")?.replace("V", "")?.removePrefix("-")
                ?.removePrefix(" ")
        holder.binding.tvPlacedetail.text =
            earthquake.wilayah?.removePrefix("Pusat gempa berada di laut ")
                ?.removePrefix("Pusat gempa berada di darat ")
        holder.binding.tvMagnitude.text = "${earthquake.magnitude}"

        val serverFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
        serverFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val date = serverFormat.parse(("${earthquake.tanggal} ${earthquake.jam}").dropLast(4))
        val timeMillis = date.time
        val timeMillisNow = System.currentTimeMillis()
        val string = Duration.minutes((timeMillisNow - timeMillis) / 60000)
        holder.binding.tvTime.text = "$string ago"
    }

    override fun getItemCount(): Int {
        if (data.isEmpty()) return 0
        return data.size
    }

    fun setData(newData: List<Gempa>) {
        data = newData
        notifyDataSetChanged()
    }

    class EarthQuakeViewHolder(val binding: ListitemEarthquakeBinding) :
        RecyclerView.ViewHolder(binding.root)
}