package ru.phoenix.kidswatch.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.adapters.EventIconsAdapter.EventIcon
import ru.phoenix.kidswatch.adapters.EventIconsAdapter.EventIconVH
import ru.phoenix.kidswatch.databinding.RvEventIconBinding
import ru.phoenix.kidswatch.dialogs.AddEventsDialogViewModel

class EventIconsAdapter(
    private val addEventsDialogVM: AddEventsDialogViewModel
) : ListAdapter<EventIcon, EventIconVH>(EventIconDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventIconVH {
        return EventIconVH.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: EventIconVH, position: Int) {
        holder.bind(currentList[position], addEventsDialogVM)
    }

    class EventIconVH(
        private val binding: RvEventIconBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(eventIcon: EventIcon, addEventsDialogVM: AddEventsDialogViewModel) {
            binding.icon.setImageBitmap(
                BitmapFactory.decodeStream(itemView.context.assets.open(eventIcon.fileName))
            )
            when (eventIcon.isSelected) {
                true -> binding.icon.setBackgroundResource(R.drawable.background_event_icon_selected)
                else -> binding.icon.background = null
            }
            binding.root.setOnClickListener {
                addEventsDialogVM.selectEventIcon(eventIcon)
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): EventIconVH {
                val binding = RvEventIconBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return EventIconVH(binding)
            }
        }
    }

    class EventIconDiffUtilCallback : ItemCallback<EventIcon>() {

        override fun areItemsTheSame(oldItem: EventIcon, newItem: EventIcon): Boolean {
            return oldItem.fileName == newItem.fileName
        }

        override fun areContentsTheSame(oldItem: EventIcon, newItem: EventIcon): Boolean {
            return oldItem == newItem
        }
    }

    data class EventIcon(
        val fileName: String,
        val isSelected: Boolean
    )

}