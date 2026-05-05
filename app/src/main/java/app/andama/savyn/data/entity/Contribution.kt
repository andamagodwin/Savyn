package app.andama.savyn.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contributions",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SavingsGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("memberId"), Index("groupId")]
)
data class Contribution(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val groupId: Long,
    val amount: Double,
    val week: Int,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)
