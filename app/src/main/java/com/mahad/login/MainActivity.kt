package com.mahad.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mahad.login.data.AppDatabase
import com.mahad.login.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Login"

        updateButtonState()

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            lifecycleScope.launch {
                val user = database.userDao().getUserByEmail(email)
                if (user != null && user.password == password) {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.tvDatabaseLabel.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateButtonState() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        val isValid = isValidEmail(email) && isValidPassword(password)
        binding.btnLogin.isEnabled = isValid
    }

    private fun isValidEmail(email: String): Boolean {

        val emailRegex =
            Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$")

        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {

        val hasUpperCase =
            password.any { it.isUpperCase() }

        return password.length >= 8 && hasUpperCase
    }
}