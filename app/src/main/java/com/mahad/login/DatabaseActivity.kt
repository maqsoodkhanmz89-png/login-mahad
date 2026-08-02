package com.mahad.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahad.login.data.AppDatabase
import com.mahad.login.data.User
import com.mahad.login.databinding.ActivityDatabaseBinding
import kotlinx.coroutines.launch

class DatabaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatabaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Database Records"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database = AppDatabase.getDatabase(this)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val users = database.userDao().getAllUsers()
            if (users.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvUsers.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvUsers.visibility = View.VISIBLE
                binding.rvUsers.adapter = UserAdapter(users)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private class UserAdapter(private val users: List<User>) :
        RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvUsername: TextView = view.findViewById(R.id.tvUsername)
            val tvEmail: TextView = view.findViewById(R.id.tvEmail)
            val tvPassword: TextView = view.findViewById(R.id.tvPassword)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.tvUsername.text = "Username: ${user.username}"
            holder.tvEmail.text = "Email: ${user.email}"
            holder.tvPassword.text = "Password: ${user.password}"
        }

        override fun getItemCount(): Int = users.size
    }
}