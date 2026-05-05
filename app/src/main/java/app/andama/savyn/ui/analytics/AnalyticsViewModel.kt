package app.andama.savyn.ui.analytics

import androidx.lifecycle.*
import app.andama.savyn.data.SavynRepository
import app.andama.savyn.data.entity.MemberWithTotal
import app.andama.savyn.data.entity.SavingsGroup
import app.andama.savyn.data.entity.WeeklyContributionSummary

class AnalyticsViewModel(private val repository: SavynRepository) : ViewModel() {

    private val _groupId = MutableLiveData<Long>()

    val group: LiveData<SavingsGroup?> = _groupId.switchMap { repository.getGroupById(it) }
    val groupTotal: LiveData<Double> = _groupId.switchMap { repository.getGroupTotal(it) }
    val memberCount: LiveData<Int> = _groupId.switchMap { repository.getMemberCount(it) }
    val membersWithTotals: LiveData<List<MemberWithTotal>> = _groupId.switchMap { repository.getMembersWithTotals(it) }
    val weeklySummary: LiveData<List<WeeklyContributionSummary>> = _groupId.switchMap { repository.getRecentWeeklySummary(it) }

    fun setGroupId(id: Long) {
        if (_groupId.value != id) _groupId.value = id
    }

    class Factory(private val repository: SavynRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(repository) as T
        }
    }
}
