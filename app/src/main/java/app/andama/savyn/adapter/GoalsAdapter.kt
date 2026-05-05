package app.andama.savyn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.andama.savyn.data.entity.SavingsGoal
import app.andama.savyn.databinding.ItemGoalBinding
import app.andama.savyn.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalsAdapter(
    private val onDelete: (SavingsGoal) -> Unit
) : ListAdapter<SavingsGoal, GoalsAdapter.ViewHolder>(DIFF) {

    private var groupTotal: Double = 0.0

    fun setGroupTotal(total: Double) {
        groupTotal = total
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: SavingsGoal) {
            binding.textGoalName.text = goal.name
            binding.textProgressAmount.text = "${CurrencyUtils.format(groupTotal)} of ${CurrencyUtils.format(goal.targetAmount)}"

            val percent = if (goal.targetAmount > 0) {
                ((groupTotal / goal.targetAmount) * 100).toInt().coerceAtMost(100)
            } else 0

            binding.textProgressPercent.text = "$percent%"
            binding.progressGoal.progress = percent

            if (goal.deadline != null) {
                binding.textDeadline.visibility = View.VISIBLE
                val dateFmt = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                binding.textDeadline.text = "Deadline: ${dateFmt.format(Date(goal.deadline))}"
            } else {
                binding.textDeadline.visibility = View.GONE
            }

            binding.btnDeleteGoal.setOnClickListener { onDelete(goal) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SavingsGoal>() {
            override fun areItemsTheSame(a: SavingsGoal, b: SavingsGoal) = a.id == b.id
            override fun areContentsTheSame(a: SavingsGoal, b: SavingsGoal) = a == b
        }
    }
}
