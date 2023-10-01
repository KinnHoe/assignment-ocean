package com.example.assignment_ocean

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChangePWActivity : AppCompatActivity() {

    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmNewPasswordEditText: EditText
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pwactivity)

        newPasswordEditText = findViewById(R.id.changedPW)
        confirmNewPasswordEditText = findViewById(R.id.connchgedPW)
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val resetButton = findViewById<Button>(R.id.oripwButton)
        resetButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmNewPasswordEditText.text.toString()

            if (isValidPassword(newPassword)) {
                if (newPassword == confirmPassword) {
                    val username = intent.getStringExtra("username")
                    if (username != null) {
                        updatePasswordInDatabase(username, newPassword)
                    } else {
                        Toast.makeText(
                            this@ChangePWActivity,
                            "Invalid username. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ChangePWActivity,
                        "Confirm password did not match with password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@ChangePWActivity,
                    "Password should have at least 1 uppercase, 1 lowercase, 1 number, 1 symbol, and be at least 8 characters long",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex()
        return passwordPattern.matches(password)
    }

    private fun updatePasswordInDatabase(username: String, newPassword: String) {
        val userReference = databaseReference.child(username)

        userReference.child("password").setValue(newPassword)

        Toast.makeText(this@ChangePWActivity, "Password updated successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@ChangePWActivity, ProfileActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
        finish()
    }
}
