package ru.phoenix.kidswatch.ui.fragments.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupSchedule()
        addScheduleEvents()
        binding.schedule.startWatches()
    }

    override fun onStop() {
        super.onStop()
        binding.schedule.stopWatches()
    }

    private fun setupSchedule() {
        val calendar = getScheduleCalendar()
        val pairs = mutableListOf<ScheduleView.Row.RowInitializer>()

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

        binding.schedule.initialize(pairs)
    }

    private fun addScheduleEvents() {
        val calendar = getScheduleCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_morning)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_kindergarden)
        calendar.set(Calendar.HOUR_OF_DAY, 13)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_day_sleep)
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_afternoon)
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        binding.schedule.addEvent(calendar.timeInMillis, R.drawable.image_evening)
        calendar.set(Calendar.HOUR_OF_DAY, 22)
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

    companion object {
        const val START_DAY_HOUR = 7
    }

}