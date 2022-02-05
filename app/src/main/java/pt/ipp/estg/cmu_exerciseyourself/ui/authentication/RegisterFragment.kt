package pt.ipp.estg.cmu_exerciseyourself.ui.authentication

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IAuthentication
import pt.ipp.estg.cmu_exerciseyourself.model.models.UserProfile
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class RegisterFragment : Fragment() {
    lateinit var registerButton : Button
    lateinit var birthDateText : TextInputEditText
    lateinit var birthDateImage : ImageView
    lateinit var mailText : TextInputEditText
    lateinit var passwordText : TextInputEditText
    lateinit var nameText : TextInputEditText
    lateinit var weightText : TextInputEditText
    lateinit var heightText: TextInputEditText
    lateinit var parentActivity : IAuthentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            parentActivity = context as IAuthentication
        } catch (e: ClassCastException) {
            Log.d("LoginFragment", "Atividade mãe deve implementar IAuthentication")
            Log.d("LoginFragment", e.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        registerButton = view.findViewById(R.id.registerButton)
        birthDateText = view.findViewById(R.id.dateText)
        mailText = view.findViewById(R.id.emailText)
        passwordText = view.findViewById(R.id.passwordText)
        nameText = view.findViewById(R.id.nameText)
        weightText = view.findViewById(R.id.pesoText)
        heightText = view.findViewById(R.id.alturaText)
        birthDateImage = view.findViewById(R.id.birthDateImageButton)
        registerButton = view.findViewById(R.id.registerButton)

        var myCalendar = Calendar.getInstance();

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar)
        }

        birthDateImage.setOnClickListener {
            activity?.let { activity ->
                DatePickerDialog(
                    activity, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }


        registerButton.setOnClickListener {
            val weight = weightText.text.toString()
            val height = heightText.text.toString()
            var newUser = UserProfile(nameText.text.toString(), birthDateText.text.toString() , mailText.text.toString())
            parentActivity.register(mailText.text.toString(), passwordText.text.toString(), newUser, weight.toDouble(), height.toDouble())
        }
        return view
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        birthDateText.setText(sdf.format(myCalendar.time))
    }

}