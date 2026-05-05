package app.andama.savyn.ui.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import app.andama.savyn.R
import app.andama.savyn.SavynApplication
import app.andama.savyn.adapter.MembersAdapter
import app.andama.savyn.databinding.FragmentGroupDetailBinding
import app.andama.savyn.databinding.BottomSheetAddMemberBinding
import app.andama.savyn.util.CurrencyUtils
import java.text.NumberFormat
import java.util.Locale

class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MembersViewModel by viewModels {
        MembersViewModel.Factory((requireActivity().application as SavynApplication).repository)
    }

    private lateinit var adapter: MembersAdapter
    private var groupId: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = arguments?.getLong("groupId") ?: return

        viewModel.setGroupId(groupId)

        adapter = MembersAdapter(
            onDelete = { member ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.remove_member)
                    .setMessage(getString(R.string.remove_member_confirm, member.name))
                    .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteMember(member) }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        )
        binding.recyclerMembers.adapter = adapter

        viewModel.group.observe(viewLifecycleOwner) { group ->
            group ?: return@observe
            binding.textGroupName.text = group.name
            binding.textGroupDescription.text = group.description
        }

        viewModel.memberCount.observe(viewLifecycleOwner) {
            binding.textMemberCount.text = it.toString()
        }

        viewModel.groupTotal.observe(viewLifecycleOwner) {
            binding.textGroupTotal.text = CurrencyUtils.format(it)
        }

        viewModel.members.observe(viewLifecycleOwner) { members ->
            adapter.submitList(members)
            binding.textEmptyMembers.visibility = if (members.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerMembers.visibility = if (members.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.membersWithTotals.observe(viewLifecycleOwner) { totals ->
            adapter.submitTotals(totals)
        }

        binding.btnAddMember.setOnClickListener { showAddMemberDialog() }

        binding.btnContributions.setOnClickListener {
            val bundle = Bundle().apply { putLong("groupId", groupId) }
            findNavController().navigate(R.id.action_detail_to_contributions, bundle)
        }

        binding.btnAnalytics.setOnClickListener {
            val bundle = Bundle().apply { putLong("groupId", groupId) }
            findNavController().navigate(R.id.action_detail_to_analytics, bundle)
        }

        binding.btnGoals.setOnClickListener {
            val bundle = Bundle().apply { putLong("groupId", groupId) }
            findNavController().navigate(R.id.action_detail_to_goals, bundle)
        }
    }

    private fun showAddMemberDialog() {
        val bottomSheetBinding = BottomSheetAddMemberBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.btnAddMember.setOnClickListener {
            val name = bottomSheetBinding.editMemberName.text.toString().trim()
            val phone = bottomSheetBinding.editMemberPhone.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.insertMember(name, phone)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
