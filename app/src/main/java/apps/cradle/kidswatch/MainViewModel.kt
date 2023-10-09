package apps.cradle.kidswatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import androidx.room.Room
import apps.cradle.kidswatch.database.Database
import apps.cradle.kidswatch.database.DbEvent
import apps.cradle.kidswatch.fragments.MainFragment.Companion.DEFAULT_INTERVALS
import apps.cradle.kidswatch.fragments.MainFragment.Companion.PREF_INTERVALS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel : ViewModel() {

    val db: Database = Room
        .databaseBuilder(App.getInstance(), Database::class.java, Database.dbName)
        .addCallback(Database.dbCallback)
        .build()

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

    private val _events = MutableLiveData<List<DbEvent>>()
    val events: LiveData<List<DbEvent>> = _events

    private fun updateEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _events.postValue(db.eventsDao().getAllEvents().sortedBy { it.time })
        }
    }

    fun deleteEvent(time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.eventsDao().deleteEventByTime(time)
            updateEvents()
        }
    }

    fun saveNewEvent(time: Long, iconFileName: String) = runBlocking(Dispatchers.IO) {
        db.eventsDao().insert(
            DbEvent(
                time = time,
                iconFilename = iconFileName
            )
        )
        updateEvents()
    }

    init {
        updateIntervals()
        updateEvents()
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