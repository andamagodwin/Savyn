package app.andama.savyn.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.andama.savyn.data.entity.Member
import app.andama.savyn.data.entity.MemberWithTotal

@Dao
interface MemberDao {
    @Insert
    suspend fun insert(member: Member): Long

    @Update
    suspend fun update(member: Member)

    @Delete
    suspend fun delete(member: Member)

    @Query("SELECT * FROM members WHERE groupId = :groupId ORDER BY name ASC")
    fun getMembersByGroup(groupId: Long): LiveData<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id")
    fun getMemberById(id: Long): LiveData<Member?>

    @Query("SELECT COUNT(*) FROM members WHERE groupId = :groupId")
    fun getMemberCount(groupId: Long): LiveData<Int>

    @Query("""
        SELECT m.id AS memberId, m.name AS memberName,
               COALESCE(SUM(c.amount), 0) AS totalContributed
        FROM members m
        LEFT JOIN contributions c ON m.id = c.memberId
        WHERE m.groupId = :groupId
        GROUP BY m.id
        ORDER BY totalContributed DESC
    """)
    fun getMembersWithTotals(groupId: Long): LiveData<List<MemberWithTotal>>

    @Query("SELECT COUNT(*) FROM members")
    fun getTotalMemberCount(): LiveData<Int>
}
