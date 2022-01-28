package pt.ipp.estg.cmu_exerciseyourself

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private val REQUEST_MAIN_MENU = 1
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]


        var loginButton = findViewById<Button>(R.id.loginButton)
        var registerButton = findViewById<Button>(R.id.registerButton)

        var mailText = findViewById<TextInputEditText>(R.id.emailText)
        var passwordText = findViewById<TextInputEditText>(R.id.passwordText)

        registerButton.setOnClickListener {
            //Fazer validação
            Log.d("Data", mailText.text.toString() + passwordText.text.toString())
            if (!mailText.text.isNullOrBlank() && !passwordText.text.isNullOrBlank()) {
                createAccount(mailText.text.toString(), passwordText.text.toString())
            } else {
                Toast.makeText(this,"Preencha todos os campos!", Toast.LENGTH_SHORT).show()

            }
        }

        loginButton.setOnClickListener {
            if (!mailText.text.isNullOrBlank() && !passwordText.text.isNullOrBlank()) {
                signIn(mailText.text.toString(), passwordText.text.toString())
            } else {
                Toast.makeText(this,"Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
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
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
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
            var intent = Intent(this, MainActivity::class.java)
            startActivityForResult(intent, REQUEST_MAIN_MENU)
        }
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}