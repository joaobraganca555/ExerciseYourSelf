package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.utils.DistanceMonth

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import pt.ipp.estg.cmu_exerciseyourself.utils.monthsToString
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList


class ManualExercise : Fragment() {
    var barChart:BarChart? = null
    var entryList:ArrayList<BarEntry>? = null
    var labelsNames:ArrayList<String>? = ArrayList(
        Arrays.asList("Jan","Fev","Mar","Abr","Mai","Jun","Jul","Aug","Set","Out","Nov","Dez"))
    var listDistancesbyMonth:ArrayList<DistanceMonth> = ArrayList()
    private lateinit var fitnessRepository:FitnessRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_manual_exercise, container, false)
        barChart = view.findViewById<BarChart>(R.id.barChartDistance)
        fitnessRepository = FitnessRepository(requireActivity().application)

        entryList = ArrayList()
        entryList?.add(BarEntry(1f, 150f))
        entryList?.add(BarEntry(2f, 26f))
        entryList?.add(BarEntry(3f, 26f))
        entryList?.add(BarEntry(4f, 155f))
        entryList?.add(BarEntry(5f, 163f))
        entryList?.add(BarEntry(6f, 25f))
        entryList?.add(BarEntry(7f, 55f))
        entryList?.add(BarEntry(8f, 25f))
        entryList?.add(BarEntry(9f, 25f))
        entryList?.add(BarEntry(10f, 55f))
        entryList?.add(BarEntry(11f, 25f))
        entryList?.add(BarEntry(12f, 95f))

        val barDataSet = BarDataSet(entryList, "")
        barDataSet.setColors(*ColorTemplate.LIBERTY_COLORS)
        val data = BarData(barDataSet)
        barChart?.data = data
        setupChart()

        // Inflate the layout for this fragment
        return view
    }


    fun setupChart(){
        barChart?.let{
            val xAxis: XAxis = it.getXAxis()
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f;
            xAxis.setLabelCount(labelsNames?.size!!)
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
            xAxis.setGranularity(1f)
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.labelRotationAngle = -40f
            xAxis.valueFormatter = IndexAxisValueFormatter(labelsNames)
            it.axisLeft.setDrawLabels(false)
            it.axisLeft.setDrawGridLines(false)
            it.xAxis.setDrawAxisLine(false)

            it.axisRight.isEnabled = false
            it.legend.isEnabled = false
            it.description.isEnabled = false
            it.animateY(2000)
            it.invalidate()
        }
        }
    }
