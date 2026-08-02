package com.mahad.login

import android.os.Bundle
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mahad.login.data.AppDatabase
import com.mahad.login.data.User
import com.mahad.login.databinding.ActivitySignupBinding
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.sign_up)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        updateButtonState()

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etUsername.addTextChangedListener(textWatcher)
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            lifecycleScope.launch {
                val existingUserByEmail = database.userDao().getUserByEmail(email)
                val existingUserByUsername = database.userDao().getUserByUsername(username)

                if (existingUserByEmail != null) {
                    showErrorDialog("Email already exists, try different email")
                } else if (existingUserByUsername != null) {
                    showErrorDialog("Username already exists, try different username")
                } else {
                    val user = User(username = username, email = email, password = password)
                    val id = database.userDao().insertUser(user)
                    val allUsers = database.userDao().getAllUsers()
                    Log.d("SignUpActivity", "User inserted with ID: $id. Current users in DB:")
                    allUsers.forEach { u ->
                        Log.d("SignUpActivity", " - ID: ${u.id}, Username: ${u.username}, Email: ${u.email}")
                    }
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignUpActivity, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun updateButtonState() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        val isValid = username.isNotEmpty() && isValidEmail(email) && isValidPassword(password)
        binding.btnSignUp.isEnabled = isValid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$")
        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        return password.length >= 8 && hasUpperCase
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Registration Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}