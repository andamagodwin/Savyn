package app.andama.savyn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.andama.savyn.data.entity.SavingsGroup
import app.andama.savyn.databinding.ItemGroupBinding
import app.andama.savyn.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupsAdapter(
    private val onClick: (SavingsGroup) -> Unit,
    private val onDelete: (SavingsGroup) -> Unit
) : ListAdapter<SavingsGroup, GroupsAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: SavingsGroup) {
            binding.textGroupName.text = group.name
            binding.textGroupDescription.text = group.description
            binding.textWeeklyTarget.text = "Target: ${CurrencyUtils.format(group.weeklyTarget)}/week"
            val dateFmt = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            binding.textGroupDate.text = "Created ${dateFmt.format(Date(group.createdAt))}"
            binding.root.setOnClickListener { onClick(group) }
            binding.btnDeleteGroup.setOnClickListener { onDelete(group) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SavingsGroup>() {
            override fun areItemsTheSame(a: SavingsGroup, b: SavingsGroup) = a.id == b.id
            override fun areContentsTheSame(a: SavingsGroup, b: SavingsGroup) = a == b
        }
    }
}
