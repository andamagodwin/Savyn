package app.andama.savyn.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.andama.savyn.data.entity.Contribution
import app.andama.savyn.data.entity.WeeklyContributionSummary

@Dao
interface ContributionDao {
    @Insert
    suspend fun insert(contribution: Contribution): Long

    @Update
    suspend fun update(contribution: Contribution)

    @Delete
    suspend fun delete(contribution: Contribution)

    @Query("SELECT * FROM contributions WHERE groupId = :groupId ORDER BY date DESC")
    fun getContributionsByGroup(groupId: Long): LiveData<List<Contribution>>

    @Query("SELECT * FROM contributions WHERE memberId = :memberId ORDER BY date DESC")
    fun getContributionsByMember(memberId: Long): LiveData<List<Contribution>>

    @Query("SELECT * FROM contributions WHERE groupId = :groupId AND week = :week ORDER BY date DESC")
    fun getContributionsByGroupAndWeek(groupId: Long, week: Int): LiveData<List<Contribution>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM contributions WHERE groupId = :groupId")
    fun getGroupTotal(groupId: Long): LiveData<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM contributions WHERE memberId = :memberId")
    fun getMemberTotal(memberId: Long): LiveData<Double>

    @Query("SELECT COALESCE(MAX(week), 0) FROM contributions WHERE groupId = :groupId")
    fun getLatestWeek(groupId: Long): LiveData<Int>

    @Query("""
        SELECT week, SUM(amount) AS totalAmount, COUNT(DISTINCT memberId) AS contributorCount
        FROM contributions
        WHERE groupId = :groupId
        GROUP BY week
        ORDER BY week ASC
    """)
    fun getWeeklySummary(groupId: Long): LiveData<List<WeeklyContributionSummary>>

    @Query("""
        SELECT DISTINCT memberId FROM contributions
        WHERE groupId = :groupId AND week = :week
    """)
    suspend fun getMemberIdsWhoPaidForWeek(groupId: Long, week: Int): List<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM contributions")
    fun getTotalSavings(): LiveData<Double>

    @Query("""
        SELECT week, SUM(amount) AS totalAmount, COUNT(DISTINCT memberId) AS contributorCount
        FROM contributions
        WHERE groupId = :groupId
        GROUP BY week
        ORDER BY week DESC
        LIMIT 12
    """)
    fun getRecentWeeklySummary(groupId: Long): LiveData<List<WeeklyContributionSummary>>
}
