package app.andama.savyn.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.andama.savyn.data.entity.SavingsGroup

@Dao
interface SavingsGroupDao {
    @Insert
    suspend fun insert(group: SavingsGroup): Long

    @Update
    suspend fun update(group: SavingsGroup)

    @Delete
    suspend fun delete(group: SavingsGroup)

    @Query("SELECT * FROM savings_groups ORDER BY createdAt DESC")
    fun getAllGroups(): LiveData<List<SavingsGroup>>

    @Query("SELECT * FROM savings_groups WHERE id = :id")
    fun getGroupById(id: Long): LiveData<SavingsGroup?>

    @Query("SELECT * FROM savings_groups WHERE id = :id")
    suspend fun getGroupByIdSync(id: Long): SavingsGroup?

    @Query("SELECT COUNT(*) FROM savings_groups")
    fun getGroupCount(): LiveData<Int>
}
