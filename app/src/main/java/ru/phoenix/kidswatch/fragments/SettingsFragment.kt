package ru.phoenix.kidswatch.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
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
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupIntervalsRv()
        setupSex()
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
        binding.boy.setOnCheckedChangeListener(onSexChanged)
        binding.girl.setOnCheckedChangeListener(onSexChanged)
    }

    private val onSexChanged: (CompoundButton, Boolean) -> Unit = { button, isChecked ->
        if (isChecked) {
            prefs.edit().putInt(
                PREF_SEX,
                when (button.id) {
                    R.id.boy -> SEX_MALE
                    else -> SEX_FEMALE
                }
            ).commit()
            setupSex()
        }
    }

    private fun onIntervalsChanged(intervals: List<String>) {
        (binding.intervals.adapter as IntervalsAdapter).updateList(intervals)
    }

    private fun setupIntervalsRv() {
        binding.intervals.addItemDecoration(Decoration())
        binding.intervals.adapter = IntervalsAdapter()
    }

    private fun setupSex() {
        when (prefs.getInt(PREF_SEX, SEX_MALE)) {
            SEX_MALE -> {
                binding.icon.setImageResource(R.drawable.image_face_boy)
                binding.boy.isChecked = true
            }

            else -> {
                binding.icon.setImageResource(R.drawable.image_face_girl)
                binding.girl.isChecked = true
            }
        }
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

    companion object {
        const val PREF_SEX = "pref_sex"
        const val SEX_MALE = 0
        const val SEX_FEMALE = 1
    }

}