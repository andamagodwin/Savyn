package app.andama.savyn.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.andama.savyn.data.dao.ContributionDao
import app.andama.savyn.data.dao.MemberDao
import app.andama.savyn.data.dao.SavingsGoalDao
import app.andama.savyn.data.dao.SavingsGroupDao
import app.andama.savyn.data.entity.Contribution
import app.andama.savyn.data.entity.Member
import app.andama.savyn.data.entity.SavingsGoal
import app.andama.savyn.data.entity.SavingsGroup

@Database(
    entities = [SavingsGroup::class, Member::class, Contribution::class, SavingsGoal::class],
    version = 1,
    exportSchema = false
)
abstract class SavynDatabase : RoomDatabase() {
    abstract fun savingsGroupDao(): SavingsGroupDao
    abstract fun memberDao(): MemberDao
    abstract fun contributionDao(): ContributionDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    companion object {
        @Volatile
        private var INSTANCE: SavynDatabase? = null

        fun getDatabase(context: Context): SavynDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavynDatabase::class.java,
                    "savyn_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
