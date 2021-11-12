package vn.sharkdms.ui.overview

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.XAxisRenderer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentOverviewBinding
import vn.sharkdms.util.Formatter
import vn.sharkdms.util.OfflineDialog
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
        setNotificationsIconListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.overviewEvent.collect { event ->
                when (event) {
                    is OverviewViewModel.OverviewEvent.OnResponse -> handleGetAmountsResponse(
                        binding, event.amounts)
                    is OverviewViewModel.OverviewEvent.OnFailure -> showOfflineDialog(
                        requireActivity().supportFragmentManager)
                    is OverviewViewModel.OverviewEvent.BindWelcomeView -> bindWelcomeView(binding,
                        event.name)
                }
            }
        }
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity ->
            if (connectivity) {
                hideOfflineDialog()
                viewModel.sendGetAmountsRequest(sharedViewModel.token)
            } else {
                showOfflineDialog(requireActivity().supportFragmentManager)
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.getName(it)
        }
    }

    private fun setNotificationsIconListener(binding: FragmentOverviewBinding) {
        binding.iconNotification.setOnClickListener {
            val action = OverviewFragmentDirections.actionOverviewFragmentToNotificationsFragment()
            findNavController().navigate(action)
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
        val entries = ArrayList<BarEntry>()
        amounts.forEachIndexed { index, amount ->
            entries.add(BarEntry(index.toFloat(), amount.revenue.toFloat()))
        }
        val dataSet = BarDataSet(entries, "")
        dataSet.apply {
            setDrawValues(false)
            color = Color.parseColor(getString(R.string.color_primary))
        }
        binding.chart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            extraBottomOffset = 1f
            legend.isEnabled = false
            xAxis.apply {
                granularity = 1f
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        val month = amounts[value.toInt()].month
                        val year = amounts[value.toInt()].year
                        return getString(R.string.fragment_overview_format_x, month, year)
                    }
                }
            }
            highlightValue(Highlight(viewModel.selectedColumnIndex.value!!.toFloat(), 0, 0))
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
                    if (entry == null) return
                    val index = entry.x.toInt()
                    viewModel.selectedColumnIndex.postValue(index)
                    val formattedRevenue = Formatter.formatCurrency(entry.y.toString())
                    binding.textViewRevenue.text = getString(
                        R.string.fragment_overview_format_total_revenue, formattedRevenue)
                    setXAxisRenderer(getXAxisRenderer(this@apply, getXAxisColors(index)))
                    notifyDataSetChanged()
                    invalidate()
                }

                override fun onNothingSelected() { //
                }
            })
            setScaleEnabled(false)
            setXAxisRenderer(
                getXAxisRenderer(this, getXAxisColors(viewModel.selectedColumnIndex.value!!)))
        }
        bindRevenue(binding, amounts[viewModel.selectedColumnIndex.value!!].revenue)
        bindTotalRevenue(binding, amounts)
    }

    private fun getXAxisColors(index: Int): List<Int> {
        val colors = mutableListOf(Color.BLACK, Color.BLACK, Color.BLACK)
        colors[index] = Color.parseColor(getString(R.string.color_primary))
        return colors
    }

    private fun getXAxisRenderer(chart: BarChart, colors: List<Int>): XAxisRenderer {
        return MyXAxisRenderer(chart.viewPortHandler, chart.xAxis,
            chart.getTransformer(AxisDependency.LEFT), colors)
    }

    private fun bindRevenue(binding: FragmentOverviewBinding, revenue: String) {
        val formattedRevenue = Formatter.formatCurrency(revenue)
        binding.apply {
            textViewRevenue.text = getString(R.string.fragment_overview_format_total_revenue,
                formattedRevenue)
            cardViewRevenue.visibility = View.VISIBLE
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
            textViewWelcome.visibility = View.VISIBLE
        }
    }

    private fun showOfflineDialog(supportFragmentManager: FragmentManager) {
        val offlineDialog = findOfflineDialog()
        if (offlineDialog == null || !offlineDialog.isAdded) {
            OfflineDialog().show(supportFragmentManager, OfflineDialog.TAG)
        }
    }

    private fun hideOfflineDialog() {
        val offlineDialog = findOfflineDialog()
        if (offlineDialog != null && offlineDialog.isAdded) {
            (offlineDialog as OfflineDialog).dismiss()
        }
    }

    private fun findOfflineDialog(): Fragment? {
        val supportFragmentManager = requireActivity().supportFragmentManager
        return supportFragmentManager.findFragmentByTag(OfflineDialog.TAG)
    }

    private fun bindWelcomeView(binding: FragmentOverviewBinding, name: String) {
        binding.apply {
            textViewWelcome.text = getString(R.string.fragment_overview_format_welcome, name)
        }
    }
}