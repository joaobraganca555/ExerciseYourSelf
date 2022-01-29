package pt.ipp.estg.cmu_exerciseyourself.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import pt.ipp.estg.cmu_exerciseyourself.R

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var registerButton : Button
    lateinit var birthDateLayout : TextInputLayout
    lateinit var birthDateText : TextView
    lateinit var birthDateImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        registerButton = view.findViewById(R.id.registerButton)
        birthDateLayout = view.findViewById(R.id.birthDateLayout)
        birthDateText = view.findViewById(R.id.birthDateText)
        birthDateImage = view.findViewById(R.id.birthDateImageButton)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        birthDateImage.setOnClickListener {
            datePicker.show(parentFragmentManager,datePicker.toString())
        }

        return view
    }

}