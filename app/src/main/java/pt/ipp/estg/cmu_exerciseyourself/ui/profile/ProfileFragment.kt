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
        var oldEmail = ""

        var user = auth.currentUser

        val docRef = db.collection("users").document(user!!.email.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    emailText.setText(document.get("email").toString())
                    oldEmail = document.get("email").toString()
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
            updateCredentials(oldEmail, emailText.text.toString(), passwordText.text.toString())
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCredentials(oldEmail: String, newEmail: String, newPassword: String) {
        //Delete old document User
        deleteOldDocumentUser(oldEmail)

        // [START update_email]
        val user = Firebase.auth.currentUser

        user!!.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                }
            }
        // [END update_email]



        // [START update_password]
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                }
            }
        // [END update_password]
        var newUser = UserProfile(nameText.text.toString(), birthDate.text.toString(), newEmail)
        createUpdatedDocumentUser(newUser)
    }

    private fun createUpdatedDocumentUser(newUser: UserProfile) {
        db.collection("users").document(newUser.email)
            .set(newUser)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${newUser.email}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun deleteOldDocumentUser(oldEmail: String) {
        db.collection("users").document(oldEmail)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

}