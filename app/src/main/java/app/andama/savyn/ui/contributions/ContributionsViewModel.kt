package app.andama.savyn.ui.contributions

import androidx.lifecycle.*
import app.andama.savyn.data.SavynRepository
import app.andama.savyn.data.entity.Contribution
import app.andama.savyn.data.entity.Member
import app.andama.savyn.data.entity.SavingsGroup
import kotlinx.coroutines.launch

class ContributionsViewModel(private val repository: SavynRepository) : ViewModel() {

    private val _groupId = MutableLiveData<Long>()
    private val _selectedWeek = MutableLiveData<Int>()

    val group: LiveData<SavingsGroup?> = _groupId.switchMap { repository.getGroupById(it) }
    val members: LiveData<List<Member>> = _groupId.switchMap { repository.getMembersByGroup(it) }
    val contributions: LiveData<List<Contribution>> = _groupId.switchMap { repository.getContributionsByGroup(it) }
    val groupTotal: LiveData<Double> = _groupId.switchMap { repository.getGroupTotal(it) }
    val latestWeek: LiveData<Int> = _groupId.switchMap { repository.getLatestWeek(it) }

    private val weekGroupPair = MediatorLiveData<Pair<Long, Int>>().apply {
        addSource(_groupId) { gid -> _selectedWeek.value?.let { w -> value = gid to w } }
        addSource(_selectedWeek) { w -> _groupId.value?.let { gid -> value = gid to w } }
    }

    val weekContributions: LiveData<List<Contribution>> = weekGroupPair.switchMap { (gid, week) ->
        repository.getContributionsByGroupAndWeek(gid, week)
    }

    private val _paidMemberIds = MutableLiveData<Set<Long>>(emptySet())
    val paidMemberIds: LiveData<Set<Long>> = _paidMemberIds

    fun setGroupId(id: Long) {
        if (_groupId.value != id) _groupId.value = id
    }

    fun setSelectedWeek(week: Int) {
        _selectedWeek.value = week
        loadPaidMembers()
    }

    private fun loadPaidMembers() {
        val gid = _groupId.value ?: return
        val week = _selectedWeek.value ?: return
        viewModelScope.launch {
            val ids = repository.getMemberIdsWhoPaidForWeek(gid, week)
            _paidMemberIds.value = ids.toSet()
        }
    }

    fun recordContribution(memberId: Long, amount: Double, week: Int, note: String = "") {
        val gid = _groupId.value ?: return
        viewModelScope.launch {
            repository.insertContribution(
                Contribution(memberId = memberId, groupId = gid, amount = amount, week = week, note = note)
            )
            loadPaidMembers()
        }
    }

    fun deleteContribution(contribution: Contribution) {
        viewModelScope.launch {
            repository.deleteContribution(contribution)
            loadPaidMembers()
        }
    }

    class Factory(private val repository: SavynRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ContributionsViewModel(repository) as T
        }
    }
}
