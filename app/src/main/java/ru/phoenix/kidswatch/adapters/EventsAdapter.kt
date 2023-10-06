package ru.phoenix.kidswatch.adapters

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phoenix.kidswatch.database.DbEvent
import ru.phoenix.kidswatch.databinding.RvEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.EventVH>() {

    private var _events = listOf<DbEvent>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(events: List<DbEvent>) {
        _events = events
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventVH {
        return EventVH.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: EventVH, position: Int) {
        holder.bind(_events[position])
    }

    override fun getItemCount(): Int {
        return _events.size
    }

    class EventVH(private val binding: RvEventBinding) : ViewHolder(binding.root) {

        private val format = SimpleDateFormat("H:mm", Locale.getDefault())

        fun bind(event: DbEvent) {
            binding.icon.setImageBitmap(
                BitmapFactory.decodeStream(itemView.context.assets.open(event.iconFilename))
            )
            binding.time.text = format.format(Date(event.time))
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): EventVH {
                val binding = RvEventBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return EventVH(binding)
            }
        }
    }
}