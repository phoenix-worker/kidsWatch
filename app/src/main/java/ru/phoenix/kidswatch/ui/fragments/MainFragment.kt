package ru.phoenix.kidswatch.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.databinding.FragmentMainBinding
import ru.phoenix.kidswatch.ui.custom.ScheduleView
import java.util.Calendar

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setupWidgetsSize()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupSchedule()
        addScheduleEvents()
        binding.schedule.startWatches()
        binding.watch.startWatches()
        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_SettingsFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.schedule.stopWatches()
        binding.watch.stopWatches()
    }

    private var pairs: MutableList<ScheduleView.Row.RowInitializer> = mutableListOf()
    private fun setupSchedule() {
        val calendar = getScheduleCalendar()
        pairs = mutableListOf()

        var first = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, 11)
        var second = calendar.timeInMillis
        pairs.add(ScheduleView.Row.RowInitializer(first, second, Color.parseColor("#03A9F4")))

        first = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, 5)
        second = calendar.timeInMillis
        pairs.add(ScheduleView.Row.RowInitializer(first, second, Color.parseColor("#4CAF50")))

        first = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, 8)
        second = calendar.timeInMillis
        pairs.add(ScheduleView.Row.RowInitializer(first, second, Color.parseColor("#212121")))
    }

    private fun addScheduleEvents() {
        val calendar = getScheduleCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_morning)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_kindergarden)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_breakfast)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_dinner)
        calendar.set(Calendar.HOUR_OF_DAY, 13)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_day_sleep)
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_afternoon)
        calendar.set(Calendar.HOUR_OF_DAY, 17)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_car)
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_evening)
        calendar.set(Calendar.HOUR_OF_DAY, 19)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_evening_meal)
        calendar.set(Calendar.HOUR_OF_DAY, 20)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_bathroom)
        calendar.set(Calendar.HOUR_OF_DAY, 21)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_games)
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 30)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_before_sleep)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_sleep)
    }

    private fun getScheduleCalendar(): Calendar {
        return Calendar.getInstance().apply {
            if (get(Calendar.HOUR_OF_DAY) < START_DAY_HOUR) add(Calendar.DAY_OF_MONTH, -1)
            set(Calendar.HOUR_OF_DAY, START_DAY_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun setupWidgetsSize() {
        binding.root.doOnLayout {
            val screenSize = activity?.window?.decorView?.width ?: 0
            val size = (screenSize * ANALOG_WATCH_RATIO).toInt()
            val params = ConstraintLayout.LayoutParams(size, size)
            params.startToEnd = binding.schedule.id
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            binding.watch.layoutParams = params
        }
        binding.schedule.doOnLayout { binding.schedule.initialize(pairs) }
    }

    companion object {
        const val START_DAY_HOUR = 7
        const val ANALOG_WATCH_RATIO = 0.25
    }

}