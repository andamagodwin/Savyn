package app.andama.savyn.ui.contributions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import app.andama.savyn.R
import app.andama.savyn.SavynApplication
import app.andama.savyn.adapter.ContributionMembersAdapter
import app.andama.savyn.data.entity.Member
import app.andama.savyn.databinding.FragmentContributionsBinding
import app.andama.savyn.databinding.DialogRecordPaymentBinding
import app.andama.savyn.util.CurrencyUtils
import java.text.NumberFormat
import java.util.Locale

class ContributionsFragment : Fragment() {

    private var _binding: FragmentContributionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContributionsViewModel by viewModels {
        ContributionsViewModel.Factory((requireActivity().application as SavynApplication).repository)
    }

    private lateinit var adapter: ContributionMembersAdapter
    private var currentWeek = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContributionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupId = arguments?.getLong("groupId") ?: return
        viewModel.setGroupId(groupId)

        adapter = ContributionMembersAdapter { member -> showPaymentDialog(member) }
        binding.recyclerContributions.adapter = adapter

        viewModel.latestWeek.observe(viewLifecycleOwner) { week ->
            currentWeek = maxOf(week, 1)
            viewModel.setSelectedWeek(currentWeek)
            updateWeekDisplay()
        }

        viewModel.members.observe(viewLifecycleOwner) { members ->
            adapter.submitList(members)
            binding.textNoMembers.visibility = if (members.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerContributions.visibility = if (members.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.paidMemberIds.observe(viewLifecycleOwner) { ids ->
            adapter.setPaidMembers(ids)
        }

        viewModel.weekContributions.observe(viewLifecycleOwner) { contributions ->
            val weekTotal = contributions.sumOf { it.amount }
            binding.textWeekTotal.text = "Total: ${CurrencyUtils.format(weekTotal)}"
        }

        binding.btnPrevWeek.setOnClickListener {
            if (currentWeek > 1) {
                currentWeek--
                viewModel.setSelectedWeek(currentWeek)
                updateWeekDisplay()
            }
        }

        binding.btnNextWeek.setOnClickListener {
            currentWeek++
            viewModel.setSelectedWeek(currentWeek)
            updateWeekDisplay()
        }
    }

    private fun updateWeekDisplay() {
        binding.textWeekNumber.text = "Week $currentWeek"
    }

    private fun showPaymentDialog(member: Member) {
        val dialogBinding = DialogRecordPaymentBinding.inflate(layoutInflater)
        dialogBinding.textMemberName.text = member.name

        viewModel.group.value?.let { group ->
            if (group.weeklyTarget > 0) {
                dialogBinding.editAmount.setText(group.weeklyTarget.toString())
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.record_payment)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.record) { _, _ ->
                val amount = dialogBinding.editAmount.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                val note = dialogBinding.editNote.text.toString().trim()
                if (amount > 0) {
                    viewModel.recordContribution(member.id, amount, currentWeek, note)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
