package app.andama.savyn.ui.members

import androidx.lifecycle.*
import app.andama.savyn.data.SavynRepository
import app.andama.savyn.data.entity.Member
import app.andama.savyn.data.entity.MemberWithTotal
import app.andama.savyn.data.entity.SavingsGroup
import kotlinx.coroutines.launch

class MembersViewModel(private val repository: SavynRepository) : ViewModel() {

    private val _groupId = MutableLiveData<Long>()

    val group: LiveData<SavingsGroup?> = _groupId.switchMap { repository.getGroupById(it) }
    val members: LiveData<List<Member>> = _groupId.switchMap { repository.getMembersByGroup(it) }
    val memberCount: LiveData<Int> = _groupId.switchMap { repository.getMemberCount(it) }
    val groupTotal: LiveData<Double> = _groupId.switchMap { repository.getGroupTotal(it) }
    val membersWithTotals: LiveData<List<MemberWithTotal>> = _groupId.switchMap { repository.getMembersWithTotals(it) }

    fun setGroupId(id: Long) {
        if (_groupId.value != id) _groupId.value = id
    }

    fun insertMember(name: String, phone: String) {
        val gid = _groupId.value ?: return
        viewModelScope.launch {
            repository.insertMember(Member(groupId = gid, name = name, phone = phone))
        }
    }

    fun updateMember(member: Member) {
        viewModelScope.launch { repository.updateMember(member) }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch { repository.deleteMember(member) }
    }

    class Factory(private val repository: SavynRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MembersViewModel(repository) as T
        }
    }
}
