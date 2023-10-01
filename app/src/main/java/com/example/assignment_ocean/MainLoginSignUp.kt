package com.example.assignment_ocean

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button

class MainLoginSignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login_sign_up)

        val signUpButton = findViewById<Button>(R.id.signup_button)

        signUpButton.setOnClickListener {

            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}