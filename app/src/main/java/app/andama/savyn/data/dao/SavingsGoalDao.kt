package app.andama.savyn.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.andama.savyn.data.entity.SavingsGoal

@Dao
interface SavingsGoalDao {
    @Insert
    suspend fun insert(goal: SavingsGoal): Long

    @Update
    suspend fun update(goal: SavingsGoal)

    @Delete
    suspend fun delete(goal: SavingsGoal)

    @Query("SELECT * FROM savings_goals WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getGoalsByGroup(groupId: Long): LiveData<List<SavingsGoal>>

    @Query("SELECT * FROM savings_goals WHERE id = :id")
    fun getGoalById(id: Long): LiveData<SavingsGoal?>
}
