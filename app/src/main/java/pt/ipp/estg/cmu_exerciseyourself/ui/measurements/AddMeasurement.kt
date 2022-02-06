package pt.ipp.estg.cmu_exerciseyourself.ui.measurements

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentMeasurementsBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Measurements
import androidx.lifecycle.Observer
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentAddMeasurementBinding
import java.time.LocalDateTime
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class AddMeasurement : Fragment() {
    private var _binding: FragmentAddMeasurementBinding? = null
    private val binding get() = _binding!!

    var height by Delegates.notNull<Double>()
    var weight by Delegates.notNull<Double>()
    var belly by Delegates.notNull<Double>()
    var chest by Delegates.notNull<Double>()
    var fat by Delegates.notNull<Double>()
    lateinit var saveBtn : Button
    lateinit var myContext: IServiceController
    lateinit var repository : FitnessRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as IServiceController
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        _binding = FragmentAddMeasurementBinding.inflate(inflater, container, false)
        val root: View = binding.root

        repository = FitnessRepository(requireActivity().application)

        saveBtn = binding.saveBtn

        repository.getCurrentMeasurement().observe(viewLifecycleOwner, Observer {
            if(it == null){
                binding.height.setText("0")
                binding.weight.setText("0")
                binding.belly.setText("0")
                binding.chest.setText("0")
                binding.fat.setText("0")
            }else{
                binding.height.setText(it?.height.toString())
                binding.weight.setText(it?.weight.toString())
                binding.belly.setText(it?.belly.toString())
                binding.chest.setText(it?.chest.toString())
                binding.fat.setText(it?.percFat.toString())
            }
        })

        saveBtn.setOnClickListener {
            try {
                height = binding.height.text.toString().toDouble()
                weight = binding.weight.text.toString().toDouble()
                belly = binding.belly.text.toString().toDouble()
                chest = binding.chest.text.toString().toDouble()
                fat = binding.fat.text.toString().toDouble()

                val date = LocalDateTime.now().toString()

                var newMeasurement = Measurements(null, height, weight, belly, chest, fat, date)

                Executors.newFixedThreadPool(1).execute{
                    repository.insertMeasurement(newMeasurement)
                }
                myContext.openMeasurementView()
            }catch (e:NumberFormatException){
                Toast.makeText(context, "Campos inválidos", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}