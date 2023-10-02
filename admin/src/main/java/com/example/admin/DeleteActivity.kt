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
            if (validateDeleteTitle()) {
                val title = binding.deleteTitle.text.toString().trim()
                deleteData(title)
            } else {
                Toast.makeText(this, "Please enter the title name to delete", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun deleteData(title: String) {
        val categoriesDatabase = FirebaseDatabase.getInstance().getReference("Categories")
        val imageDatabase = FirebaseDatabase.getInstance().getReference("Image")

        // Check if the title exists in the "Categories" database
        categoriesDatabase.child(title).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Title exists in "Categories" database, proceed with deletion
                    categoriesDatabase.child(title).removeValue().addOnSuccessListener {
                        binding.deleteTitle.text.clear()
                        Toast.makeText(
                            this@DeleteActivity,
                            "Deleted from Categories",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@DeleteActivity,
                            "Unable to delete from Categories",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // if Title doesn't exist in the "Categories" database
                    // thebn check is there in the "Image" database
                    imageDatabase.child(title)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Title exists in "Image" database, proceed with deletion
                                    imageDatabase.child(title).removeValue().addOnSuccessListener {
                                        binding.deleteTitle.text.clear()
                                        Toast.makeText(
                                            this@DeleteActivity,
                                            "Deleted from Image",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            this@DeleteActivity,
                                            "Unable to delete from Image",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // Title doesn't exist in either database
                                    Toast.makeText(
                                        this@DeleteActivity,
                                        "Title not found in the database",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }


    private fun validateDeleteTitle(): Boolean {
        val deleteTitle = binding.deleteTitle.text.toString().trim()
        if (deleteTitle.isEmpty()) {
            binding.deleteTitle.error = "Title is required"
            return false
        } else if (!deleteTitle.matches(Regex("^[a-zA-Z ]+\$"))) {
            binding.deleteTitle.error = "Title should contain only letters"
            return false
        }
        return true
    }
}