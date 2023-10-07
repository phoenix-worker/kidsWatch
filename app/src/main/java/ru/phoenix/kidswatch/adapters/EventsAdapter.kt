package ru.phoenix.kidswatch.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phoenix.kidswatch.MainViewModel
import ru.phoenix.kidswatch.database.DbEvent
import ru.phoenix.kidswatch.databinding.RvEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsAdapter(
    private val mainVM: MainViewModel
) : ListAdapter<DbEvent, EventsAdapter.EventVH>(EventDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventVH {
        return EventVH.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: EventVH, position: Int) {
        holder.bind(currentList[position], mainVM)
    }

    class EventVH(private val binding: RvEventBinding) : ViewHolder(binding.root) {

        private val format = SimpleDateFormat("H:mm", Locale.getDefault())

        fun bind(event: DbEvent, mainVM: MainViewModel) {
            binding.icon.setImageBitmap(
                BitmapFactory.decodeStream(itemView.context.assets.open(event.iconFilename))
            )
            binding.time.text = format.format(Date(event.time))
            binding.delete.setOnClickListener { mainVM.deleteEvent(event.time) }
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

    class EventDiffUtilCallback : DiffUtil.ItemCallback<DbEvent>() {

        override fun areItemsTheSame(oldItem: DbEvent, newItem: DbEvent): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: DbEvent, newItem: DbEvent): Boolean {
            return oldItem.time == newItem.time && oldItem.iconFilename == newItem.iconFilename
        }
    }
}