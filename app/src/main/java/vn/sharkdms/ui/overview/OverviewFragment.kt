package vn.sharkdms.ui.overview

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Anchor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentOverviewBinding
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private val viewModel by viewModels<OverviewViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOverviewBinding.bind(view)
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.overviewEvent.collect { event ->
                when (event) {
                    is OverviewViewModel.OverviewEvent.OnResponse -> handleGetAmountsResponse(
                        binding, event.amounts)
                    is OverviewViewModel.OverviewEvent.OnFailure -> handleRequestFailure(binding)
                }
            }
        }
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity ->
            val mustLoad = binding.progressBar.visibility == View.VISIBLE
            if (connectivity && mustLoad) {
                viewModel.sendGetAmountsRequest(sharedViewModel.token)
            } else if (mustLoad) {
                handleRequestFailure(binding)
            }
        }
    }

    private fun handleGetAmountsResponse(binding: FragmentOverviewBinding, amounts: List<Amount>?) {
        val calendar = Calendar.getInstance()
        if (amounts == null || amounts.isEmpty()) {
            val defaultAmounts = getDefaultAmounts(calendar)
            bindChart(binding, defaultAmounts)
        } else if (amounts.size < 3) {
            val defaultAmounts = getDefaultAmounts(calendar)
            amounts.forEach { amount ->
                defaultAmounts.forEach { defaultAmount ->
                    if (amount.month == defaultAmount.month) {
                        defaultAmount.revenue = amount.revenue
                    }
                }
            }
            bindChart(binding, defaultAmounts)
        } else {
            bindChart(binding, amounts)
        }
    }

    private fun getDefaultAmounts(calendar: Calendar): List<Amount> {
        val thirdAmount = getDefaultAmount(calendar)
        calendar.add(Calendar.MONTH, -1)
        val secondAmount = getDefaultAmount(calendar)
        calendar.add(Calendar.MONTH, -1)
        val firstAmount = getDefaultAmount(calendar)
        return listOf(firstAmount, secondAmount, thirdAmount)
    }

    private fun getDefaultAmount(calendar: Calendar) =
        Amount("" + 0, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), 0)

    private fun bindChart(binding: FragmentOverviewBinding, amounts: List<Amount>) {
        val data = ArrayList<DataEntry>()
        amounts.forEach { amount ->
            val x = getString(R.string.fragment_overview_format_x, amount.month, amount.year)
            data.add(ValueDataEntry(x, amount.revenue.toBigDecimal()))
        }
        val color = getString(R.string.color_primary)
        val chart = AnyChart.column()
        chart.column(data).apply {
            color(color)
            tooltip().anchor(Anchor.CENTER)
                .format(getString(R.string.fragment_overview_format_tooltip_y))
        }
        chart.apply {
            animation(true)
            xAxis(0).apply {
                title().fontColor(color)
                labels().fontColor(color)
            }
            yAxis(0).apply {
                title().fontColor(color)
                title(getString(R.string.fragment_overview_title_y))
                labels().format(getString(R.string.fragment_overview_format_y)).fontColor(color)
            }
        }
        binding.anyChartView.apply {
            setChart(chart)
            setProgressBar(binding.progressBar)
            setOnRenderedListener {
                bindTotalRevenue(binding, amounts)
            }
        }
    }

    private fun bindTotalRevenue(binding: FragmentOverviewBinding, amounts: List<Amount>) {
        var totalRevenue = BigDecimal(0)
        amounts.forEach { amount ->
            totalRevenue += amount.revenue.toBigDecimal()
        }
        val dfs = DecimalFormatSymbols()
        dfs.groupingSeparator = ' '
        val formattedTotalRevenue = DecimalFormat("#,###", dfs).format(totalRevenue)
        binding.apply {
            textViewTotalAmount.text = getString(R.string.fragment_overview_format_total_revenue,
                formattedTotalRevenue)
            textViewTotalAmount.visibility = View.VISIBLE
            textViewTotalAmountTitle.visibility = View.VISIBLE
        }
    }

    private fun handleRequestFailure(binding: FragmentOverviewBinding) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }
}