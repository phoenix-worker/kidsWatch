package ru.phoenix.kidswatch.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.databinding.FragmentMainBinding
import java.util.Calendar
import java.util.Date

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
    }

    private fun setupSchedule() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, START_DAY_HOUR)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (Date().time < calendar.timeInMillis) calendar.add(Calendar.DAY_OF_MONTH, -1)

        binding.run {
            morning.setTime(calendar.time, 60)
            calendar.add(Calendar.MINUTE, 60)
            morning.setIcon(R.drawable.image_morning)

            kindergarden.setTime(calendar.time, 300)
            calendar.add(Calendar.MINUTE, 300)
            kindergarden.setIcon(R.drawable.image_kindergarden)

            daySleep.setTime(calendar.time, 120)
            calendar.add(Calendar.MINUTE, 120)
            daySleep.setIcon(R.drawable.image_day_sleep)

            afternoon.setTime(calendar.time, 180)
            calendar.add(Calendar.MINUTE, 180)
            afternoon.setIcon(R.drawable.image_afternoon)

            evening.setTime(calendar.time, 240)
            calendar.add(Calendar.MINUTE, 240)
            evening.setIcon(R.drawable.image_evening)

            beforeSleep.setTime(calendar.time, 60)
            calendar.add(Calendar.MINUTE, 60)
            beforeSleep.setIcon(R.drawable.image_before_sleep)

            sleep.setTime(calendar.time, 480)
            calendar.add(Calendar.MINUTE, 480)
            sleep.setIcon(R.drawable.image_sleep)
        }
    }

    companion object {
        const val START_DAY_HOUR = 7
    }

}