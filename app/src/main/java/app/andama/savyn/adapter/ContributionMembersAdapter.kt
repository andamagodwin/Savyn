package app.andama.savyn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.andama.savyn.data.entity.Member
import app.andama.savyn.databinding.ItemContributionMemberBinding

class ContributionMembersAdapter(
    private val onRecordPayment: (Member) -> Unit
) : ListAdapter<Member, ContributionMembersAdapter.ViewHolder>(DIFF) {

    private var paidMemberIds: Set<Long> = emptySet()

    fun setPaidMembers(ids: Set<Long>) {
        paidMemberIds = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContributionMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemContributionMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: Member) {
            val hasPaid = member.id in paidMemberIds
            binding.textMemberName.text = member.name

            if (hasPaid) {
                binding.iconStatus.setImageResource(android.R.drawable.checkbox_on_background)
                binding.textContributionStatus.text = "Paid"
                binding.btnRecordPayment.text = "Paid"
                binding.btnRecordPayment.isEnabled = false
                binding.btnRecordPayment.alpha = 0.5f
            } else {
                binding.iconStatus.setImageResource(android.R.drawable.checkbox_off_background)
                binding.textContributionStatus.text = "Not paid"
                binding.btnRecordPayment.text = "Pay"
                binding.btnRecordPayment.isEnabled = true
                binding.btnRecordPayment.alpha = 1f
            }

            binding.btnRecordPayment.setOnClickListener {
                if (!hasPaid) onRecordPayment(member)
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(a: Member, b: Member) = a.id == b.id
            override fun areContentsTheSame(a: Member, b: Member) = a == b
        }
    }
}
