package app.andama.savyn.ui.groups

import androidx.lifecycle.*
import app.andama.savyn.data.SavynRepository
import app.andama.savyn.data.entity.SavingsGroup
import kotlinx.coroutines.launch

class GroupsViewModel(private val repository: SavynRepository) : ViewModel() {

    val allGroups: LiveData<List<SavingsGroup>> = repository.allGroups
    val groupCount: LiveData<Int> = repository.groupCount
    val totalMemberCount: LiveData<Int> = repository.totalMemberCount
    val totalSavings: LiveData<Double> = repository.totalSavings

    fun insertGroup(name: String, description: String, weeklyTarget: Double) {
        viewModelScope.launch {
            repository.insertGroup(
                SavingsGroup(name = name, description = description, weeklyTarget = weeklyTarget)
            )
        }
    }

    fun updateGroup(group: SavingsGroup) {
        viewModelScope.launch { repository.updateGroup(group) }
    }

    fun deleteGroup(group: SavingsGroup) {
        viewModelScope.launch { repository.deleteGroup(group) }
    }

    class Factory(private val repository: SavynRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GroupsViewModel(repository) as T
        }
    }
}
