package pt.ipp.estg.cmu_exerciseyourself.ui.measurements

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentMeasurementsBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Measurements

class MeasurementsFragment : Fragment() {

    private var _binding: FragmentMeasurementsBinding? = null
    private val binding get() = _binding!!
    lateinit var addMeasurementBtn: ImageView
    lateinit var repository: FitnessRepository
    private lateinit var navController: NavController
    var totalMeasurements: List<Measurements>? = null
    private var chartWeight: LineChart? = null
    private var chartFat: LineChart? = null
    private var weightEntries: ArrayList<Entry>? = null
    private var fatEntries: ArrayList<Entry>? = null
    lateinit var hostActivity: IServiceController

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMeasurementsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        repository = FitnessRepository(requireActivity().application)

        navController = findNavController(this)

        addMeasurementBtn = binding.addMeasure

        addMeasurementBtn.setOnClickListener {
            navController.navigate(R.id.navigation_addMeasurement)
        }

        chartWeight = root.findViewById(R.id.lineChartWeight)
        chartFat = root.findViewById(R.id.lineChartFat)

        repository.getCurrentMeasurement().observe(viewLifecycleOwner) {
            val cm = " cm"
            binding.belly.text = it?.belly.toString() + cm
            binding.chest.text = it?.chest.toString() + cm
            binding.height.text = it?.height.toString() + cm
            binding.weight.text = it?.weight.toString() + " kg"
        }

        repository.getAllMeasurements().observe(viewLifecycleOwner) {
            totalMeasurements = it

            weightEntries = ArrayList()
            fatEntries = ArrayList()

            var count = 0f
            var indexToRemove = 0
            for (i in totalMeasurements!!) {
                weightEntries?.add(BarEntry(count, i.weight.toFloat()))
                fatEntries?.add(BarEntry(count, i.percFat.toFloat()))
                count++
                if (count.toInt() > 10) {
                    weightEntries?.removeAt(indexToRemove)
                    fatEntries?.removeAt(indexToRemove)
                }
            }
            setupCharts()

            var dataSet = LineDataSet(weightEntries, "")
            dataSet.setColors(*ColorTemplate.LIBERTY_COLORS)
            var data = LineData(dataSet)
            chartWeight?.data = data

            dataSet = LineDataSet(fatEntries, "")
            dataSet.setColors(*ColorTemplate.LIBERTY_COLORS)
            data = LineData(dataSet)
            chartFat?.data = data
        }

        return root
    }

    private fun setupCharts() {
        chartWeight?.let {
            val xAxis: XAxis = it.xAxis
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f;
            xAxis.labelCount = 0
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.labelRotationAngle = -40f
            it.axisLeft.setDrawLabels(false)
            it.axisLeft.setDrawGridLines(false)
            it.xAxis.setDrawAxisLine(false)
            it.xAxis.isEnabled = false
            it.axisRight.isEnabled = false
            it.legend.isEnabled = false
            it.description.isEnabled = false
            it.animateY(2000)
            it.invalidate()
        }
        chartFat?.let {
            val xAxis: XAxis = it.xAxis
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f;
            xAxis.labelCount = 0
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.labelRotationAngle = -40f
            it.axisLeft.setDrawLabels(false)
            it.axisLeft.setDrawGridLines(false)
            it.xAxis.setDrawAxisLine(false)
            it.xAxis.isEnabled = false
            it.axisRight.isEnabled = false
            it.legend.isEnabled = false
            it.description.isEnabled = false
            it.animateY(2000)
            it.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            hostActivity = context as IServiceController
        } catch (e: ClassCastException) {
            Log.d("error", "onAttach: $e")
            Toast.makeText(context, "An problem occurred.Please try later.", Toast.LENGTH_SHORT)
                .show()
        }
    }

}