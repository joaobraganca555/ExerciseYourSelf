package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentManualExerciseBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Status
import java.lang.ClassCastException
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ExerciseFragment : Fragment() {
    private lateinit var binding: FragmentManualExerciseBinding
    var totalWorkouts:List<Workouts>? = null

    var barChartDistance:BarChart? = null
    var barChartCalories:BarChart? = null
    var barChartDuration:BarChart? = null

    var entryList:ArrayList<BarEntry>? = null
    var entryListCalories:ArrayList<BarEntry>? = null
    var entryListDuration:ArrayList<BarEntry>? = null

    var labelsNames:ArrayList<String>? = ArrayList(
        Arrays.asList("Jan","Fev","Mar","Abr","Mai","Jun","Jul","Aug","Set","Out","Nov","Dez"))
    lateinit var repository: FitnessRepository
    lateinit var hostActivity:IServiceController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            hostActivity = context as IServiceController
        }catch (e: ClassCastException){
            Log.d("error", "onAttach: $e")
            Toast.makeText(context,"An problem occurred.Please try later.", Toast.LENGTH_SHORT).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManualExerciseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val btnAdd = binding.iconAdd

        btnAdd.setOnClickListener {
            hostActivity.openAddActivity()
        }

        repository = FitnessRepository(requireActivity().application)

        barChartDistance = binding.barChartDistance
        barChartCalories = binding.barChartCalories
        barChartDuration = binding.barChartDuration

        setupChartDistance()
        setupChartCalories()
        setupChartDuration()

        repository.getAllWorkouts().observe(viewLifecycleOwner) {
            totalWorkouts = it

            entryList = ArrayList()
            entryListCalories = ArrayList()
            entryListDuration = ArrayList()

            getCaloriesLastDays()
            getDurationLastDays()

            //Calculate each month
            for (i in 1..12) {
                var totalDistance = getTotalDistanceByMonth(i)
                if (totalDistance != null)
                    entryList?.add(BarEntry(i.toFloat(), totalDistance))
                else
                    entryList?.add(BarEntry(i.toFloat(), 0f))
            }
            val barDataSet = BarDataSet(entryList, "")
            barDataSet.setColors(*ColorTemplate.LIBERTY_COLORS)
            val data = BarData(barDataSet)
            barChartDistance?.data = data
        }

        // Inflate the layout for this fragment
        return root
    }

    private fun setupChartDistance(){
        barChartDistance?.let{
            val xAxis: XAxis = it.xAxis
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f;
            xAxis.labelCount = labelsNames?.size!!
            xAxis.position = XAxis.XAxisPosition.BOTTOM
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

    private fun setupChartCalories(){
        barChartCalories?.let{
            val xAxis: XAxis = it.xAxis
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f;
            xAxis.labelCount = 7
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setAvoidFirstLastClipping(true)
            it.xAxis.isEnabled = false
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

    private fun setupChartDuration(){
        barChartDuration?.let{
            val xAxis: XAxis = it.xAxis
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f;
            xAxis.labelCount = 7
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setAvoidFirstLastClipping(true)
            it.xAxis.isEnabled = false
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTotalDistanceByMonth(month:Int):Float?{
        var workoutsByMonth = totalWorkouts?.filter {
            LocalDateTime.parse(it.beginDate).month == Month.of(month)
                    && !it.status.equals(Status.PLANNED.toString())
        }

        var totalDistance = workoutsByMonth?.map { it.distance }?.sum()
        return totalDistance?.toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCaloriesLastDays(){
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (i in 1..6){
            var totalCalories = 0
            var targetDay = LocalDateTime.now().minusDays(i.toLong())
            var workoutsByDay = totalWorkouts?.filter {
                LocalDateTime.parse(it.beginDate).format(formatter).equals(targetDay.format(formatter))
            }

            totalCalories = workoutsByDay?.map { it.calories }?.sum() ?: 0
            entryListCalories?.add(BarEntry(i.toFloat(), totalCalories.toFloat()))
        }

        val barDataSetCalories = BarDataSet(entryListCalories, "")
        barDataSetCalories.setColors(*ColorTemplate.LIBERTY_COLORS)
        val data = BarData(barDataSetCalories)
        barChartCalories?.data = data
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDurationLastDays(){
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (i in 1..6){
            var totalDuration = 0
            var targetDay = LocalDateTime.now().minusDays(i.toLong())
            var workoutsByDay = totalWorkouts?.filter {
                LocalDateTime.parse(it.beginDate).format(formatter).equals(targetDay.format(formatter))
            }

            Log.d("DURA", workoutsByDay?.map{it.duration}.toString())

            totalDuration = workoutsByDay?.map { it.duration.toInt() }?.sum() ?: 0

            Log.d("DURA", "total" + totalDuration.toFloat().toString())

            entryListDuration?.add(BarEntry(i.toFloat(), totalDuration.toFloat()))
        }

        val barDataSetDuration = BarDataSet(entryListDuration, "")
        barDataSetDuration.setColors(*ColorTemplate.LIBERTY_COLORS)
        val data = BarData(barDataSetDuration)
        barChartDuration?.data = data
    }
}
