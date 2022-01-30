package pt.ipp.estg.cmu_exerciseyourself.ui.measurements

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentMeasurementsBinding
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Measurements
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class MeasurementsFragment : Fragment() {

    private var _binding: FragmentMeasurementsBinding? = null
    private val binding get() = _binding!!

    var height by Delegates.notNull<Double>()
    var weight by Delegates.notNull<Double>()
    var belly by Delegates.notNull<Double>()
    var chest by Delegates.notNull<Double>()
    var fat by Delegates.notNull<Double>()
    lateinit var saveBtn : Button

    lateinit var repository : FitnessRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        _binding = FragmentMeasurementsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        repository = FitnessRepository(requireActivity().application)

        saveBtn = binding.saveBtn

        saveBtn.setOnClickListener {

            height = binding.height.text.toString().toDouble()
            weight = binding.weight.text.toString().toDouble()
            belly = binding.belly.text.toString().toDouble()
            chest = binding.chest.text.toString().toDouble()
            fat = binding.fat.text.toString().toDouble()

            val date = LocalDateTime.now().toString()

            var newMeasurment = Measurements(null,height, weight, belly, chest,fat, date)

            repository.insertMeasurement(newMeasurment)

            Log.d("MEASURE",repository.getCurrentMeasurement().toString())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}