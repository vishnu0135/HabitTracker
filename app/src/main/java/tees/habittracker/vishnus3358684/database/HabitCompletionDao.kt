package tees.habittracker.vishnus3358684.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: HabitCompletion)

    @Query("""
        SELECT * FROM habit_completion 
        WHERE habitId = :habitId AND date = :date
        LIMIT 1
    """)
    suspend fun getCompletionForDate(
        habitId: Int,
        date: String
    ): HabitCompletion?

    @Query("""
        SELECT * FROM habit_completion 
        WHERE habitId = :habitId
        ORDER BY date DESC
    """)
    suspend fun getAllCompletions(habitId: Int): List<HabitCompletion>

    @Query("""
    SELECT * FROM habit_completion
    WHERE habitId = :habitId
    ORDER BY date DESC
""")
    suspend fun getCompletionsDesc(habitId: Int): List<HabitCompletion>




    @Query("""
    SELECT * FROM habit_completion
    WHERE habitId = :habitId
    AND date = :date
""")
    suspend fun getCompletionsForHabitOnDate(
        habitId: Int,
        date: String
    ): List<HabitCompletion>


    @Query("""
    SELECT * FROM habit_completion
    WHERE habitId = :habitId
    AND date LIKE :month || '%'
""")
    suspend fun getCompletionsForMonth(
        habitId: Int,
        month: String
    ): List<HabitCompletion>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("""
        SELECT * FROM habit_completion
        WHERE habitId = :habitId
        AND date = :date
        LIMIT 1
    """)
    suspend fun getCompletionForHabitOnDate(
        habitId: Int,
        date: String
    ): HabitCompletion?


}
