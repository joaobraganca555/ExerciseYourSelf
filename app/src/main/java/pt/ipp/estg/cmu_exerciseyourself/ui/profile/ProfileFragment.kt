package pt.ipp.estg.cmu_exerciseyourself.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentProfileBinding
import pt.ipp.estg.cmu_exerciseyourself.model.models.UserProfile
import pt.ipp.estg.cmu_exerciseyourself.ui.authentication.AuthenticationActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var saveChanges: Button
    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText
    private lateinit var nameText: TextInputEditText
    private lateinit var birthDate: TextInputEditText
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    private lateinit var myContext: Context
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        saveChanges = binding.btnUpdateUser
        emailText = binding.email
        nameText = binding.name
        passwordText = binding.password
        birthDate = binding.birthDate

        val docRef = db.collection("users").document(auth.currentUser!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    emailText.setText(document.get("email").toString())
                    nameText.setText(document.get("name").toString())
                    birthDate.setText(document.get("birthDate").toString())
                } else {
                    Toast.makeText(myContext, "Este utilizador não existe!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        saveChanges.setOnClickListener {
            if (!passwordText.text.isNullOrBlank()) {
                if (passwordText.text.toString().length < 6) {
                    Toast.makeText(
                        myContext,
                        "Password deve conter mais de 6 caracteres!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    updateCredentials(emailText.text.toString(), passwordText.text.toString())
                }
            } else {
                updateCredentials(emailText.text.toString(), "")
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCredentials(newEmail: String, newPassword: String) {
        // [START update_email]
        val user = Firebase.auth.currentUser

        user!!.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                }
            }
        // [END update_email]

        if (!newPassword.isNullOrBlank()) {
            // [START update_password]
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                    }
                }
            // [END update_password]
        }

        val newUser = UserProfile(nameText.text.toString(), birthDate.text.toString(), newEmail)
        updateUserInFirestore(newUser)
    }

    private fun updateUserInFirestore(newUser: UserProfile) {
        db.collection("users").document(auth.currentUser!!.uid)
            .set(newUser)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot updated with ID: ${auth.currentUser!!.uid}")
                Toast.makeText(
                    myContext,
                    "Dados atualizados com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}