package io.chronostech.awasgempabumi

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.chronostech.awasgempabumi.databinding.ListitemEarthquakeBinding
import io.chronostech.awasgempabumi.model.Gempa

class EarthQuakeAdapter(val context: Context) : RecyclerView.Adapter<EarthQuakeAdapter.EarthQuakeViewHolder>() {
    private var data = listOf<Gempa>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthQuakeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListitemEarthquakeBinding.inflate(layoutInflater, parent, false)
        return EarthQuakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EarthQuakeViewHolder, position: Int) {
        val earthquake = data[position]
        holder.binding.tvPlace.text = earthquake.coordinates
        holder.binding.tvPlacedetail.text = earthquake.dirasakan
        holder.binding.tvMagnitude.text = "${earthquake.magnitude}"
        holder.binding.tvTime.text = "${earthquake.tanggal} ${earthquake.jam}"
    }

    override fun getItemCount(): Int {
        if (data.isEmpty()) return 0
        return data.size
    }

    fun setData (newData : List<Gempa>){
        data = newData
        notifyDataSetChanged()
    }

    class EarthQuakeViewHolder(val binding : ListitemEarthquakeBinding) : RecyclerView.ViewHolder(binding.root)
}