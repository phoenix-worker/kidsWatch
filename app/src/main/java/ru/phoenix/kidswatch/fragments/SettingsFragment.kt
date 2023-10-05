package ru.phoenix.kidswatch.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import ru.phoenix.kidswatch.MainViewModel
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.adapters.IntervalsAdapter
import ru.phoenix.kidswatch.databinding.FragmentSettingsBinding
import ru.phoenix.kidswatch.dialogs.ChangeIntervalsDialog

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val mainVM by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupIntervalsRv()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        mainVM.intervals.observe(viewLifecycleOwner) { onIntervalsChanged(it) }
    }

    private fun setListeners() {
        binding.changeIntervals.setOnClickListener {
            ChangeIntervalsDialog().show(childFragmentManager, "change_intervals_dialog")
        }
    }

    private fun onIntervalsChanged(intervals: List<String>) {
        (binding.intervals.adapter as IntervalsAdapter).updateList(intervals)
    }

    private fun setupIntervalsRv() {
        binding.intervals.addItemDecoration(Decoration())
        binding.intervals.adapter = IntervalsAdapter()
    }

    private inner class Decoration : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val horizontalMargin = resources.getDimension(R.dimen.normalMargin).toInt()
            val verticalMargin = (resources.getDimension(R.dimen.normalMargin)).toInt() / 2
            outRect.set(
                horizontalMargin,
                verticalMargin / 2,
                horizontalMargin,
                verticalMargin / 2
            )
        }
    }

}