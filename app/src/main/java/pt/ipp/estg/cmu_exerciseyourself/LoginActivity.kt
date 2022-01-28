package pt.ipp.estg.cmu_exerciseyourself

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        var loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}