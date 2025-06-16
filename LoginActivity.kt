package com.example.localloop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username == "admin" && password == "XPI76SZUqyCjVxgnUjm0") {
                val intent = Intent(this, AdminDashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
