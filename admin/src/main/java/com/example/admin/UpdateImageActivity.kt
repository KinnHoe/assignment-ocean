package com.example.admin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.admin.data.ImageClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateImageActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var dataImageName: String? = null
    private var url: Uri? = null
    var data: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_image)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Image")

        // Get the dataImageName from the intent if it exists
        dataImageName = intent.getStringExtra("Name")
        Toast.makeText(this, dataImageName.toString(), Toast.LENGTH_SHORT).show()

        if (dataImageName != null) {
            fetchAndUpdateData(dataImageName!!)
        }

        val updateImageButton = findViewById<Button>(R.id.updateImageButton)
        val updateNameImage = findViewById<TextView>(R.id.updateNameImage)
        val updateImage = findViewById<ImageView>(R.id.updateImage)


        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                data = result.data
                url = data?.data
                updateImage.setImageURI(url)
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
            url = data?.data
            Toast.makeText(this, url.toString(), Toast.LENGTH_SHORT).show()
        }


        updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        updateImageButton.setOnClickListener {
            // Handle the image update here
            if (url != null) {
                storageRef = FirebaseStorage.getInstance().reference.child("Image")
                    .child(url!!.lastPathSegment!!)

                storageRef.putFile(url!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image uploaded successfully, now retrieve the download URL
                        storageRef.downloadUrl.addOnSuccessListener { uRL ->
                            val updatedImageURL = uRL.toString()

                            // Update the Firebase Realtime Database with the updated image URL
                            updateData(dataImageName!!, updateNameImage.text.toString(), updatedImageURL)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle the error if image upload fails
                        Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                // Update the Firebase Realtime Database without changing the image URL
                updateData(dataImageName!!, updateNameImage.text.toString(), "")
            }
        }
    }

    private fun fetchAndUpdateData(dataImageName: String) {
        database.orderByChild("dataImageName").equalTo(dataImageName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (dataSnapshot in snapshot.children) {
                            val imageData = dataSnapshot.getValue(ImageClass::class.java)
                            if (imageData != null) {
                                // Populate your layout views with data
                                val updateNameImage = findViewById<TextView>(R.id.updateNameImage)
                                val updateImage = findViewById<ImageView>(R.id.updateImage)

                                updateNameImage.text =
                                    Editable.Factory.getInstance().newEditable(imageData.dataImageName)

                                // Load and display the image using Glide or any other image loading library
                                if (!imageData.dataImageG.isNullOrEmpty()) {
                                    Glide.with(this@UpdateImageActivity)
                                        .load(imageData.dataImageG)
                                        .into(updateImage)
                                }
                            }
                        }
                    } else {
                        // Handle the case where no data with the given dataImageName was found
                        Toast.makeText(this@UpdateImageActivity, "No data found for $dataImageName", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any database errors here
                    Toast.makeText(this@UpdateImageActivity, "Database error: $error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateData(dataImageName: String, updateNameImage: String, updatedImageURL: String?) {
        val dataValues = mutableMapOf<String, Any>()
        dataValues["dataImageName"] = updateNameImage
        if (!updatedImageURL.isNullOrEmpty()) {
            dataValues["dataImageG"] = updatedImageURL
        }

        database.child(dataImageName).updateChildren(dataValues)
            .addOnSuccessListener {
                // Clear input fields and show a success message
                val updateNameImage = findViewById<TextView>(R.id.updateNameImage)
                updateNameImage.text

                Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT).show()
            }
    }

}
