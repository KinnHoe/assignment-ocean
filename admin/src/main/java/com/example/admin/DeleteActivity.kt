package com.example.admin


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.admin.databinding.ActivityDeleteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.deleteButton.setOnClickListener {
            val title = binding.deleteTitle.text.toString().trim() // Trim to remove leading/trailing spaces
            if (title.isNotEmpty()) {
                deleteData(title)
            } else {
                Toast.makeText(this, "Please enter the title name to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteData(title: String) {
        database = FirebaseDatabase.getInstance().getReference("Categories")
        database = FirebaseDatabase.getInstance().getReference("Image")
        // Check if the title exists as a key in the database
        database.child(title).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Title exists, proceed with deletion
                    database.child(title).removeValue().addOnSuccessListener {
                        binding.deleteTitle.text.clear()
                        Toast.makeText(this@DeleteActivity, "Deleted", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@DeleteActivity, "Unable to delete", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Title doesn't exist in the database
                    Toast.makeText(this@DeleteActivity, "Title not found in the database", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }
}