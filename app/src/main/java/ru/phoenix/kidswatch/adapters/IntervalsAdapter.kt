package ru.phoenix.kidswatch.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phoenix.kidswatch.databinding.RvIntervalBinding

class IntervalsAdapter : RecyclerView.Adapter<IntervalsAdapter.IntervalVH>() {

    private var _intervals = listOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(intervals: List<String>) {
        _intervals = intervals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalVH {
        return IntervalVH.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: IntervalVH, position: Int) {
        holder.bind(_intervals[position])
    }

    override fun getItemCount(): Int {
        return _intervals.size
    }

    class IntervalVH(private val binding: RvIntervalBinding) : ViewHolder(binding.root) {

        fun bind(interval: String) {
            binding.title.text = interval
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): IntervalVH {
                val binding = RvIntervalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IntervalVH(binding)
            }
        }
    }
}