package apps.cradle.kidswatch.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dbEvent: DbEvent)

    @Query("DELETE FROM events WHERE time = :time")
    fun deleteEventByTime(time: Long): Int

    @Query("SELECT * FROM events")
    fun getAllEvents(): List<DbEvent>

}