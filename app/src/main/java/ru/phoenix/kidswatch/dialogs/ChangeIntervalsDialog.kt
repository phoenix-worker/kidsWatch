package ru.phoenix.kidswatch.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import ru.phoenix.kidswatch.MainViewModel
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.databinding.DialogChangeIntervalsBinding
import ru.phoenix.kidswatch.fragments.MainFragment

class ChangeIntervalsDialog : DialogFragment() {

    private lateinit var binding: DialogChangeIntervalsBinding
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(requireActivity()) }
    private val mainVM by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChangeIntervalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ApplySharedPref")
    override fun onStart() {
        super.onStart()
        binding.intervals.doOnTextChanged { text, _, _, _ ->
            binding.buttonSave.isEnabled = checkIntervals(text.toString())
        }
        binding.buttonSave.setOnClickListener {
            prefs.edit()
                .putString(MainFragment.PREF_INTERVALS, binding.intervals.text.toString())
                .commit()
            mainVM.updateIntervals()
            dismiss()
        }
        binding.buttonSave.isEnabled = checkIntervals(binding.intervals.text.toString())
    }

    private fun checkIntervals(input: String): Boolean {
        return try {
            val points = MainViewModel.getPointFromIntervalsString(input)
            when {
                points.any { it < 0 || it > 23 } -> {
                    binding.info.text = getString(R.string.weirdDataErrorIntervals)
                    false
                }

                points.size < 3 -> {
                    binding.info.text = getString(R.string.littleDataErrorIntervals)
                    false
                }

                points.size > 5 -> {
                    binding.info.text = getString(R.string.tooMuchDataErrorIntervals)
                    false
                }

                !isOrderCorrect(points) -> {
                    binding.info.text = getString(R.string.orderErrorIntervals)
                    false
                }

                else -> {
                    binding.info.text = ""
                    true
                }
            }
        } catch (exc: Exception) {
            Log.d("SISKI", exc.toString())
            binding.info.text = getString(R.string.inputErrorIntervals)
            false
        }
    }

    private fun isOrderCorrect(points: List<Int>): Boolean {
        var pointer = points[0]
        val list = points.toMutableList()
        for (i in 0 until 24) {
            if (list.isNotEmpty() && list[0] == pointer) list.removeFirst()
            pointer++
            if (pointer == 24) pointer = 0
        }
        return list.isEmpty()
    }

}