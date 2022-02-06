package pt.ipp.estg.cmu_exerciseyourself.ui.authentication

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipp.estg.cmu_exerciseyourself.MainActivity
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IAuthentication
import pt.ipp.estg.cmu_exerciseyourself.model.models.UserProfile
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Measurements
import java.time.LocalDateTime
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class AuthenticationActivity : AppCompatActivity(), IAuthentication {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private lateinit var db: FirebaseFirestore
    private val REQUEST_MAIN_MENU = 1
    lateinit var repository : FitnessRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("asd","Authentication Activity as been CREATED")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication_activity)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]
        db = Firebase.firestore

        repository = FitnessRepository(this.application)

        val loginFragment = LoginFragment()

        // Start login fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.authenticationFrame, loginFragment)
            .addToBackStack(null)
            .commit()
    }


    public override fun onStart() {
        Log.d("AuthenticationActivity","Authentication Activity just STARTED")
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }

    override fun onRestart() {
        Log.d("AuthenticationActivity","Authentication Activity RESTARTED")
        super.onRestart()
    }

    override fun onResume() {
        Log.d("AuthenticationActivity","Authentication Activity RESUMED")
        super.onResume()
    }

    override fun onPause() {
        Log.d("AuthenticationActivity","Authentication Activity is on PAUSE")
        super.onPause()
    }

    override fun onStop() {
        Log.d("AuthenticationActivity","Authentication Activity just STOPPED")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("AuthenticationActivity","Authentication Activity as been DESTROYED")
        super.onDestroy()
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val sharedPreferences = baseContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("user", user.email)
            editor.apply()

            var intent = Intent(this, MainActivity::class.java)
            startActivityForResult(intent, REQUEST_MAIN_MENU)
            finish()
        }
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    override fun login(email: String, password: String, passwordLayout: TextInputLayout, emailLayout: TextInputLayout) {
            if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
            // [START sign_in_with_email]
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        if (task.exception is FirebaseAuthEmailException || task.exception is FirebaseAuthInvalidCredentialsException) {
                            emailLayout.error = task.exception!!.message
                        } else {
                            passwordLayout.error = task.exception!!.message
                        }
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
            // [END sign_in_with_email]
        } else {
            Toast.makeText(this,"Preencha todos os campos!", Toast.LENGTH_SHORT).show()
        }
        //var intent = Intent(this, MainActivity::class.java)
        //startActivityForResult(intent, REQUEST_MAIN_MENU)
        //finish()
    }

    override fun startRegisterFragment() {
        // Start RegisterFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.authenticationFrame, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun register(email: String, password: String, user: UserProfile, firstWeight: Double, firstHeight: Double) {
        if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
            // [START create_user_with_email]
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val userLogin = auth.currentUser
                        addUserToFireStore(user)
                        insertNewMeasurement(firstWeight, firstHeight)
                        updateUI(userLogin)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Não foi possivel efetuar o registo!",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
            // [END create_user_with_email]
        } else {
            Toast.makeText(this,"Preencha todos os campos!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertNewMeasurement(weight: Double, height: Double) {
        try {
            val date = LocalDateTime.now().toString()
            var newMeasurement = Measurements(null, height, weight, 0.0, 0.0, 0.0, date)

            Executors.newFixedThreadPool(1).execute{
                repository.insertMeasurement(newMeasurement)
            }
        }catch (e:NumberFormatException){
            Toast.makeText(this, "Campos inválidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserToFireStore(user: UserProfile) {
        // Add a new document with a generated ID
        db.collection("users").document(auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${auth.currentUser!!.uid}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}