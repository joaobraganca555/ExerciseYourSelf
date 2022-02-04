package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentHealthBinding
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentManualExerciseBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.Status
import java.lang.ClassCastException
import java.time.LocalDateTime
import java.time.Month
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class ManualExercise : Fragment() {
    private lateinit var binding: FragmentManualExerciseBinding
    var totalWorkouts:List<Workouts>? = null
    var barChart:BarChart? = null
    var entryList:ArrayList<BarEntry>? = null
    var labelsNames:ArrayList<String>? = ArrayList(
        Arrays.asList("Jan","Fev","Mar","Abr","Mai","Jun","Jul","Aug","Set","Out","Nov","Dez"))
    lateinit var repository: FitnessRepository
    lateinit var hostActivity:IServiceController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        barChart = root.findViewById(R.id.barChartDistance)
        setupChart()

        repository.getAllWorkouts().observe(viewLifecycleOwner,{
            totalWorkouts = it
            entryList = ArrayList()

            //Calculate each month
            for (i in 1..12){
                var totalDistance = getTotalDistanceByMonth(i)
                if(totalDistance!= null)
                    entryList?.add(BarEntry(i.toFloat(), totalDistance))
                else
                    entryList?.add(BarEntry(i.toFloat(), 0f))
            }
            val barDataSet = BarDataSet(entryList, "")
            barDataSet.setColors(*ColorTemplate.LIBERTY_COLORS)
            val data = BarData(barDataSet)
            barChart?.data = data
        })

        repository.getAllPlannedWorkouts().observe(viewLifecycleOwner,{
            var res = it
        })

        // Inflate the layout for this fragment
        return root
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTotalDistanceByMonth(month:Int):Float?{
        var workoutsByMonth = totalWorkouts?.filter {
            LocalDateTime.parse(it.beginDate).month == Month.of(month)
        }

        var totalDistance = workoutsByMonth?.map { it.distance }?.sum()
        return totalDistance?.toFloat()
    }
}
