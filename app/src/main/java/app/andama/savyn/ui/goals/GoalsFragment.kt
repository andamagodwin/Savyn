package app.andama.savyn.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import app.andama.savyn.R
import app.andama.savyn.SavynApplication
import app.andama.savyn.adapter.GoalsAdapter
import app.andama.savyn.databinding.FragmentGoalsBinding
import app.andama.savyn.databinding.DialogAddGoalBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalsViewModel by viewModels {
        GoalsViewModel.Factory((requireActivity().application as SavynApplication).repository)
    }

    private lateinit var adapter: GoalsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupId = arguments?.getLong("groupId") ?: return
        viewModel.setGroupId(groupId)

        adapter = GoalsAdapter { goal ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_goal)
                .setMessage(getString(R.string.delete_goal_confirm, goal.name))
                .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteGoal(goal) }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
        binding.recyclerGoals.adapter = adapter

        viewModel.goals.observe(viewLifecycleOwner) { goals ->
            adapter.submitList(goals)
            binding.textEmpty.visibility = if (goals.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerGoals.visibility = if (goals.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.groupTotal.observe(viewLifecycleOwner) { total ->
            adapter.setGroupTotal(total)
        }

        binding.fabAddGoal.setOnClickListener { showAddGoalDialog() }
    }

    private fun showAddGoalDialog() {
        val dialogBinding = DialogAddGoalBinding.inflate(layoutInflater)
        var selectedDeadline: Long? = null

        dialogBinding.btnSetDeadline.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_deadline))
                .build()
            picker.addOnPositiveButtonClickListener { millis ->
                selectedDeadline = millis
                val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                dialogBinding.btnSetDeadline.text = dateFmt.format(Date(millis))
            }
            picker.show(parentFragmentManager, "deadline_picker")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_goal)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.create) { _, _ ->
                val name = dialogBinding.editGoalName.text.toString().trim()
                val amount = dialogBinding.editTargetAmount.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                if (name.isNotEmpty() && amount > 0) {
                    viewModel.insertGoal(name, amount, selectedDeadline)
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
