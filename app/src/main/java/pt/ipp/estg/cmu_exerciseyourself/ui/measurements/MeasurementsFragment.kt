package pt.ipp.estg.cmu_exerciseyourself.ui.measurements

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentMeasurementsBinding
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository

class MeasurementsFragment : Fragment() {

    private var _binding: FragmentMeasurementsBinding? = null
    private val binding get() = _binding!!

    lateinit var addMeasurementBtn: Button

    lateinit var repository : FitnessRepository

    private lateinit var navController: NavController




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        _binding = FragmentMeasurementsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        repository = FitnessRepository(requireActivity().application)

        navController = findNavController(this)

        addMeasurementBtn = binding.addMeasure

        addMeasurementBtn.setOnClickListener {
            navController.navigate(R.id.navigation_addMeasurement)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}