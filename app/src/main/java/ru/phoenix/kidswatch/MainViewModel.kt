package ru.phoenix.kidswatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import ru.phoenix.kidswatch.fragments.MainFragment.Companion.DEFAULT_INTERVALS
import ru.phoenix.kidswatch.fragments.MainFragment.Companion.PREF_INTERVALS

class MainViewModel : ViewModel() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(App.getInstance()) }

    private val _intervals = MutableLiveData<List<String>>()
    val intervals: LiveData<List<String>> = _intervals

    fun updateIntervals() {
        val intervalsString = (prefs.getString(PREF_INTERVALS, null) ?: DEFAULT_INTERVALS)
        val points = getPointFromIntervalsString(intervalsString)
        val intervals = mutableListOf<String>()
        for ((index, point) in points.withIndex()) {
            intervals.add(
                when {
                    index != points.lastIndex -> "$point:00 - ${points[index + 1]}:00"
                    else -> "$point:00 - ${points[0]}:00"
                }
            )
        }
        _intervals.postValue(intervals)
    }

    init {
        updateIntervals()
    }

    companion object {

        fun getPointFromIntervalsString(intervalsString: String): List<Int> {
            return intervalsString
                .split(' ')
                .filter { it.isNotBlank() }
                .map { it.trim().toInt() }
        }

    }

}