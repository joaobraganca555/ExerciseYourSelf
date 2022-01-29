package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentExerciseBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController

class ExerciseFragment : Fragment() {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var myContext: IServiceController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as IServiceController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnStart.setOnClickListener {
            myContext.startManualExercise()
        }

        binding.btnStop.setOnClickListener {
            myContext.stopManualExercise()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}