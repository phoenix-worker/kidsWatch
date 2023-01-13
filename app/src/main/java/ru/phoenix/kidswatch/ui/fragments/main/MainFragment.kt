package ru.phoenix.kidswatch.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.databinding.FragmentMainBinding

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
        binding.run {
            morning.setTime("7:00", 60)
            morning.setIcon(R.drawable.image_morning)
            kindergarden.setTime("8:00", 270)
            kindergarden.setIcon(R.drawable.image_kindergarden)
            daySleep.setTime("12:30", 150)
            daySleep.setIcon(R.drawable.image_day_sleep)
            afternoon.setTime("15:00", 180)
            afternoon.setIcon(R.drawable.image_afternoon)
            evening.setTime("18:00", 240)
            evening.setIcon(R.drawable.image_evening)
            beforeSleep.setTime("22:00", 60)
            beforeSleep.setIcon(R.drawable.image_before_sleep)
            sleep.setTime("23:00", 480)
            sleep.setIcon(R.drawable.image_sleep)
        }
    }

}