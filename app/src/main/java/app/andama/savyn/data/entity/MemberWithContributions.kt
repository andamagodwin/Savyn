package app.andama.savyn.data.entity

data class MemberWithTotal(
    val memberId: Long,
    val memberName: String,
    val totalContributed: Double
)

data class WeeklyContributionSummary(
    val week: Int,
    val totalAmount: Double,
    val contributorCount: Int
)
