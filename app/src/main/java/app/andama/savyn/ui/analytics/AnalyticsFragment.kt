package app.andama.savyn.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import app.andama.savyn.R
import app.andama.savyn.SavynApplication
import app.andama.savyn.databinding.FragmentAnalyticsBinding
import app.andama.savyn.util.CurrencyUtils
import java.text.NumberFormat
import java.util.*

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyticsViewModel by viewModels {
        AnalyticsViewModel.Factory((requireActivity().application as SavynApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupId = arguments?.getLong("groupId") ?: return
        viewModel.setGroupId(groupId)

        viewModel.groupTotal.observe(viewLifecycleOwner) { total ->
            binding.textTotalSaved.text = CurrencyUtils.format(total)
        }

        viewModel.memberCount.observe(viewLifecycleOwner) { count ->
            binding.textMemberCount.text = count.toString()

            val total = viewModel.groupTotal.value ?: 0.0
            val avg = if (count > 0) total / count else 0.0
            binding.textAvgPerMember.text = CurrencyUtils.format(avg)
        }

        viewModel.groupTotal.observe(viewLifecycleOwner) { total ->
            binding.textTotalSaved.text = CurrencyUtils.format(total)
            val count = viewModel.memberCount.value ?: 0
            val avg = if (count > 0) total / count else 0.0
            binding.textAvgPerMember.text = CurrencyUtils.format(avg)
        }

        setupWeeklyChart()
        setupMembersChart()
        setupParticipationChart()

        viewModel.weeklySummary.observe(viewLifecycleOwner) { summaries ->
            updateWeeklyChart(summaries.sortedBy { it.week })
            updateParticipationChart(summaries.sortedBy { it.week })
        }

        viewModel.membersWithTotals.observe(viewLifecycleOwner) { members ->
            updateMembersChart(members)
        }
    }

    private fun setupWeeklyChart() {
        binding.chartWeekly.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setFitBars(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            setNoDataText(getString(R.string.no_data_yet))
        }
    }

    private fun setupMembersChart() {
        binding.chartMembers.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setFitBars(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            setNoDataText(getString(R.string.no_data_yet))
        }
    }

    private fun setupParticipationChart() {
        binding.chartParticipation.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            setNoDataText(getString(R.string.no_data_yet))
        }
    }

    private fun updateWeeklyChart(summaries: List<app.andama.savyn.data.entity.WeeklyContributionSummary>) {
        if (summaries.isEmpty()) {
            binding.chartWeekly.clear()
            return
        }

        val primaryColor = requireContext().getColor(R.color.chart_primary)
        val entries = summaries.mapIndexed { i, s -> BarEntry(i.toFloat(), s.totalAmount.toFloat()) }
        val dataSet = BarDataSet(entries, "Weekly Savings").apply {
            color = primaryColor
            valueTextSize = 10f
        }

        binding.chartWeekly.apply {
            data = BarData(dataSet)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val idx = value.toInt()
                    return if (idx in summaries.indices) "W${summaries[idx].week}" else ""
                }
            }
            xAxis.labelCount = summaries.size
            invalidate()
        }
    }

    private fun updateMembersChart(members: List<app.andama.savyn.data.entity.MemberWithTotal>) {
        if (members.isEmpty()) {
            binding.chartMembers.clear()
            return
        }

        val sorted = members.sortedBy { it.totalContributed }
        val primaryColor = requireContext().getColor(R.color.chart_secondary)
        val entries = sorted.mapIndexed { i, m -> BarEntry(i.toFloat(), m.totalContributed.toFloat()) }
        val dataSet = BarDataSet(entries, "Member Contributions").apply {
            color = primaryColor
            valueTextSize = 10f
        }

        binding.chartMembers.apply {
            data = BarData(dataSet)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val idx = value.toInt()
                    return if (idx in sorted.indices) sorted[idx].memberName.take(8) else ""
                }
            }
            xAxis.labelCount = sorted.size
            invalidate()
        }
    }

    private fun updateParticipationChart(summaries: List<app.andama.savyn.data.entity.WeeklyContributionSummary>) {
        if (summaries.isEmpty()) {
            binding.chartParticipation.clear()
            return
        }

        val memberCount = viewModel.memberCount.value ?: 1
        val accentColor = requireContext().getColor(R.color.chart_accent)
        val entries = summaries.mapIndexed { i, s ->
            val rate = if (memberCount > 0) (s.contributorCount.toFloat() / memberCount) * 100 else 0f
            Entry(i.toFloat(), rate)
        }

        val dataSet = LineDataSet(entries, "Participation %").apply {
            color = accentColor
            setCircleColor(accentColor)
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = accentColor
            fillAlpha = 50
        }

        binding.chartParticipation.apply {
            data = LineData(dataSet)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val idx = value.toInt()
                    return if (idx in summaries.indices) "W${summaries[idx].week}" else ""
                }
            }
            axisLeft.axisMaximum = 100f
            axisLeft.axisMinimum = 0f
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
