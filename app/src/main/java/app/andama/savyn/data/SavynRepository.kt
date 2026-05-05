package app.andama.savyn.data

import androidx.lifecycle.LiveData
import app.andama.savyn.data.entity.*

class SavynRepository(private val database: SavynDatabase) {

    private val groupDao = database.savingsGroupDao()
    private val memberDao = database.memberDao()
    private val contributionDao = database.contributionDao()
    private val goalDao = database.savingsGoalDao()

    // Groups
    val allGroups: LiveData<List<SavingsGroup>> = groupDao.getAllGroups()
    val groupCount: LiveData<Int> = groupDao.getGroupCount()
    val totalMemberCount: LiveData<Int> = memberDao.getTotalMemberCount()
    val totalSavings: LiveData<Double> = contributionDao.getTotalSavings()

    suspend fun insertGroup(group: SavingsGroup): Long = groupDao.insert(group)
    suspend fun updateGroup(group: SavingsGroup) = groupDao.update(group)
    suspend fun deleteGroup(group: SavingsGroup) = groupDao.delete(group)
    fun getGroupById(id: Long): LiveData<SavingsGroup?> = groupDao.getGroupById(id)
    suspend fun getGroupByIdSync(id: Long): SavingsGroup? = groupDao.getGroupByIdSync(id)

    // Members
    fun getMembersByGroup(groupId: Long): LiveData<List<Member>> = memberDao.getMembersByGroup(groupId)
    fun getMemberCount(groupId: Long): LiveData<Int> = memberDao.getMemberCount(groupId)
    fun getMembersWithTotals(groupId: Long): LiveData<List<MemberWithTotal>> = memberDao.getMembersWithTotals(groupId)
    suspend fun insertMember(member: Member): Long = memberDao.insert(member)
    suspend fun updateMember(member: Member) = memberDao.update(member)
    suspend fun deleteMember(member: Member) = memberDao.delete(member)

    // Contributions
    fun getContributionsByGroup(groupId: Long): LiveData<List<Contribution>> = contributionDao.getContributionsByGroup(groupId)
    fun getContributionsByMember(memberId: Long): LiveData<List<Contribution>> = contributionDao.getContributionsByMember(memberId)
    fun getContributionsByGroupAndWeek(groupId: Long, week: Int): LiveData<List<Contribution>> = contributionDao.getContributionsByGroupAndWeek(groupId, week)
    fun getGroupTotal(groupId: Long): LiveData<Double> = contributionDao.getGroupTotal(groupId)
    fun getMemberTotal(memberId: Long): LiveData<Double> = contributionDao.getMemberTotal(memberId)
    fun getLatestWeek(groupId: Long): LiveData<Int> = contributionDao.getLatestWeek(groupId)
    fun getWeeklySummary(groupId: Long): LiveData<List<WeeklyContributionSummary>> = contributionDao.getWeeklySummary(groupId)
    fun getRecentWeeklySummary(groupId: Long): LiveData<List<WeeklyContributionSummary>> = contributionDao.getRecentWeeklySummary(groupId)
    suspend fun getMemberIdsWhoPaidForWeek(groupId: Long, week: Int): List<Long> = contributionDao.getMemberIdsWhoPaidForWeek(groupId, week)
    suspend fun insertContribution(contribution: Contribution): Long = contributionDao.insert(contribution)
    suspend fun deleteContribution(contribution: Contribution) = contributionDao.delete(contribution)

    // Goals
    fun getGoalsByGroup(groupId: Long): LiveData<List<SavingsGoal>> = goalDao.getGoalsByGroup(groupId)
    fun getGoalById(id: Long): LiveData<SavingsGoal?> = goalDao.getGoalById(id)
    suspend fun insertGoal(goal: SavingsGoal): Long = goalDao.insert(goal)
    suspend fun updateGoal(goal: SavingsGoal) = goalDao.update(goal)
    suspend fun deleteGoal(goal: SavingsGoal) = goalDao.delete(goal)
}
