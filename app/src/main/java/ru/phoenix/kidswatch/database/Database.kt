package ru.phoenix.kidswatch.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Calendar
import java.util.Locale

@Database(
    entities = [DbEvent::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun eventsDao(): EventsDao

    companion object {

        const val dbName = "database"

        val dbCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val calendar = Calendar.getInstance(Locale.getDefault()).apply {
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                calendar.set(Calendar.HOUR_OF_DAY, 7)
                insertEvent(db, calendar.timeInMillis, "icons/image_morning.png")
                calendar.set(Calendar.HOUR_OF_DAY, 8)
                insertEvent(db, calendar.timeInMillis, "icons/image_kindergarden.png")
                calendar.set(Calendar.HOUR_OF_DAY, 9)
                insertEvent(db, calendar.timeInMillis, "icons/image_breakfast.png")
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                insertEvent(db, calendar.timeInMillis, "icons/image_dinner.png")
                calendar.set(Calendar.HOUR_OF_DAY, 13)
                insertEvent(db, calendar.timeInMillis, "icons/image_day_sleep.png")
                calendar.set(Calendar.HOUR_OF_DAY, 15)
                insertEvent(db, calendar.timeInMillis, "icons/image_afternoon.png")
                calendar.set(Calendar.HOUR_OF_DAY, 17)
                insertEvent(db, calendar.timeInMillis, "icons/image_car.png")
                calendar.set(Calendar.HOUR_OF_DAY, 18)
                insertEvent(db, calendar.timeInMillis, "icons/image_evening.png")
                calendar.set(Calendar.HOUR_OF_DAY, 19)
                insertEvent(db, calendar.timeInMillis, "icons/image_evening_meal.png")
                calendar.set(Calendar.HOUR_OF_DAY, 20)
                insertEvent(db, calendar.timeInMillis, "icons/image_bathroom.png")
                calendar.set(Calendar.HOUR_OF_DAY, 21)
                insertEvent(db, calendar.timeInMillis, "icons/image_games.png")
                calendar.set(Calendar.HOUR_OF_DAY, 22)
                insertEvent(db, calendar.timeInMillis, "icons/image_before_sleep.png")
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                insertEvent(db, calendar.timeInMillis, "icons/image_sleep.png")
            }

            private fun insertEvent(db: SupportSQLiteDatabase, time: Long, iconFilename: String) {
                db.execSQL("INSERT INTO events (time, icon_filename) VALUES ($time, '$iconFilename')")
            }
        }
    }
}