package app.andama.savyn.ui.groups

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
import app.andama.savyn.adapter.GroupsAdapter
import app.andama.savyn.databinding.FragmentGroupsBinding
import app.andama.savyn.databinding.BottomSheetAddGroupBinding
import app.andama.savyn.util.CurrencyUtils
import java.text.NumberFormat
import java.util.Locale

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupsViewModel by viewModels {
        GroupsViewModel.Factory((requireActivity().application as SavynApplication).repository)
    }

    private lateinit var adapter: GroupsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupsAdapter(
            onClick = { group ->
                val bundle = Bundle().apply { putLong("groupId", group.id) }
                findNavController().navigate(R.id.action_groups_to_detail, bundle)
            },
            onDelete = { group ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_group)
                    .setMessage(getString(R.string.delete_group_confirm, group.name))
                    .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteGroup(group) }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        )
        binding.recyclerGroups.adapter = adapter

        viewModel.allGroups.observe(viewLifecycleOwner) { groups ->
            adapter.submitList(groups)
            binding.textEmpty.visibility = if (groups.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerGroups.visibility = if (groups.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.groupCount.observe(viewLifecycleOwner) { binding.textGroupCount.text = it.toString() }
        viewModel.totalMemberCount.observe(viewLifecycleOwner) { binding.textMemberCount.text = it.toString() }
        viewModel.totalSavings.observe(viewLifecycleOwner) { binding.textTotalSavings.text = CurrencyUtils.format(it) }

        binding.fabAddGroup.setOnClickListener { showAddGroupDialog() }
    }

    private fun showAddGroupDialog() {
        val bottomSheetBinding = BottomSheetAddGroupBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.btnCreateGroup.setOnClickListener {
            val name = bottomSheetBinding.editGroupName.text.toString().trim()
            val desc = bottomSheetBinding.editGroupDescription.text.toString().trim()
            val target = bottomSheetBinding.editWeeklyTarget.text.toString().toDoubleOrNull() ?: 0.0
            if (name.isNotEmpty()) {
                viewModel.insertGroup(name, desc, target)
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
