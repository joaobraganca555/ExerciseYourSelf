package pt.ipp.estg.cmu_exerciseyourself.ui.authentication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IAuthentication
import java.lang.ClassCastException

class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var loginButton : Button
    lateinit var registerButton: Button
    lateinit var mailText : TextInputEditText
    lateinit var passwordText : TextInputEditText
    lateinit var parentActivity : IAuthentication
    lateinit var passwordLayout : TextInputLayout
    lateinit var emailLayout : TextInputLayout

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
            Log.d("LoginFragment", "parentActivity deve implementar IAuthentication")
            Log.d("LoginFragment", e.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        loginButton = view.findViewById(R.id.loginButton)
        registerButton = view.findViewById(R.id.registerButton)
        mailText = view.findViewById(R.id.emailText)
        passwordText = view.findViewById(R.id.passwordText)
        passwordLayout = view.findViewById(R.id.passwordField)
        emailLayout = view.findViewById(R.id.emailField)

        loginButton.setOnClickListener {
            passwordLayout.error = null
            emailLayout.error = null
            parentActivity.login(mailText.text.toString(), passwordText.text.toString(), passwordLayout, emailLayout)
        }

        registerButton.setOnClickListener {
            passwordLayout.error = null
            emailLayout.error = null
            parentActivity.startRegisterFragment()
        }

        return view
    }
}