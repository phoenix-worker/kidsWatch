package ru.phoenix.kidswatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ru.phoenix.kidswatch.App
import ru.phoenix.kidswatch.MainViewModel
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.custom.ScheduleView.Row.RowInitializer
import ru.phoenix.kidswatch.databinding.FragmentMainBinding
import java.util.Calendar

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(requireActivity()) }
    private val mainVM by activityViewModels<MainViewModel>()

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
        binding.calendar.date = System.currentTimeMillis()
        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_SettingsFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.schedule.stopWatches()
        binding.watch.stopWatches()
    }

    private var pairs: MutableList<RowInitializer> = mutableListOf()

    private fun getStartHours(): List<Int> {
        val intervalsString = prefs.getString(PREF_INTERVALS, null) ?: DEFAULT_INTERVALS
        return MainViewModel.getPointFromIntervalsString(intervalsString)
    }

    private val rowColors = listOf(
        ContextCompat.getColor(App.getInstance(), R.color.rows_1),
        ContextCompat.getColor(App.getInstance(), R.color.rows_2),
        ContextCompat.getColor(App.getInstance(), R.color.rows_3),
        ContextCompat.getColor(App.getInstance(), R.color.rows_4),
        ContextCompat.getColor(App.getInstance(), R.color.rows_5)
    )

    private fun setupSchedule() {
        val startHours = getStartHours()
        val calendar = getScheduleCalendar(startHours[0])
        pairs = mutableListOf()
        for ((index, startHour) in startHours.withIndex()) {
            val first = calendar.timeInMillis
            if (index < startHours.lastIndex)
                calendar.add(Calendar.HOUR_OF_DAY, countHours(startHour, startHours[index + 1]))
            else calendar.add(Calendar.HOUR_OF_DAY, countHours(startHour, startHours[0]))
            val color = rowColors[index + rowColors.size - startHours.size]
            pairs.add(RowInitializer(first, calendar.timeInMillis, color))
        }
        binding.schedule.initialize(pairs)
    }

    private fun countHours(start: Int, end: Int): Int {
        var pointer = start
        var counter = 0
        while (pointer != end) {
            counter++
            pointer++
            if (pointer == 24) pointer = 0
        }
        return counter
    }

    private fun addScheduleEvents() = runBlocking(Dispatchers.IO) {
        val startHours = getStartHours()
        val calendar = getScheduleCalendar(startHours[0])
        val tempCalendar = Calendar.getInstance()
        mainVM.db.eventsDao().getAllEvents().forEach { event ->
            tempCalendar.timeInMillis = event.time
            calendar.set(Calendar.HOUR_OF_DAY, tempCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE))
            binding.schedule.addEvent(calendar.timeInMillis, event.iconFilename)
        }
    }

    private fun getScheduleCalendar(startHour: Int): Calendar {
        return Calendar.getInstance().apply {
            if (get(Calendar.HOUR_OF_DAY) < startHour) add(Calendar.DAY_OF_MONTH, -1)
            set(Calendar.HOUR_OF_DAY, startHour)
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
            params.bottomToTop = binding.calendar.id
            params.verticalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD
            binding.watch.layoutParams = params
            val calendarParams =
                ConstraintLayout.LayoutParams(size, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            calendarParams.topToBottom = binding.watch.id
            calendarParams.startToStart = binding.watch.id
            calendarParams.endToEnd = binding.watch.id
            calendarParams.bottomToTop = binding.buttonSettings.id
            binding.calendar.layoutParams = calendarParams
        }
        binding.schedule.doOnLayout { binding.schedule.initialize(pairs) }
    }

    companion object {
        const val PREF_INTERVALS = "pref_intervals"
        const val DEFAULT_INTERVALS = "7 13 18 23"
        const val ANALOG_WATCH_RATIO = 0.25
    }

}