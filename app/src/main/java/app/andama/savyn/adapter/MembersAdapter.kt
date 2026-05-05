package app.andama.savyn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.andama.savyn.data.entity.Member
import app.andama.savyn.data.entity.MemberWithTotal
import app.andama.savyn.databinding.ItemMemberBinding
import app.andama.savyn.util.CurrencyUtils
import java.util.Locale

class MembersAdapter(
    private val onDelete: (Member) -> Unit
) : ListAdapter<Member, MembersAdapter.ViewHolder>(DIFF) {

    private var totalsMap: Map<Long, Double> = emptyMap()

    fun submitTotals(totals: List<MemberWithTotal>) {
        totalsMap = totals.associate { it.memberId to it.totalContributed }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: Member) {
            binding.textMemberName.text = member.name
            binding.textMemberPhone.text = member.phone.ifEmpty { "No phone" }
            binding.textAvatar.text = member.name.firstOrNull()?.uppercase() ?: "?"
            val total = totalsMap[member.id] ?: 0.0
            binding.textMemberTotal.text = CurrencyUtils.format(total)
            binding.btnDeleteMember.setOnClickListener { onDelete(member) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(a: Member, b: Member) = a.id == b.id
            override fun areContentsTheSame(a: Member, b: Member) = a == b
        }
    }
}
