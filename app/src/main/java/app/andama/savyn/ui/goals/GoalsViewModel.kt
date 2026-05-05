package app.andama.savyn.ui.goals

import androidx.lifecycle.*
import app.andama.savyn.data.SavynRepository
import app.andama.savyn.data.entity.SavingsGoal
import kotlinx.coroutines.launch

class GoalsViewModel(private val repository: SavynRepository) : ViewModel() {

    private val _groupId = MutableLiveData<Long>()

    val goals: LiveData<List<SavingsGoal>> = _groupId.switchMap { repository.getGoalsByGroup(it) }
    val groupTotal: LiveData<Double> = _groupId.switchMap { repository.getGroupTotal(it) }

    fun setGroupId(id: Long) {
        if (_groupId.value != id) _groupId.value = id
    }

    fun insertGoal(name: String, targetAmount: Double, deadline: Long?) {
        val gid = _groupId.value ?: return
        viewModelScope.launch {
            repository.insertGoal(
                SavingsGoal(groupId = gid, name = name, targetAmount = targetAmount, deadline = deadline)
            )
        }
    }

    fun updateGoal(goal: SavingsGoal) {
        viewModelScope.launch { repository.updateGoal(goal) }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    class Factory(private val repository: SavynRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GoalsViewModel(repository) as T
        }
    }
}
