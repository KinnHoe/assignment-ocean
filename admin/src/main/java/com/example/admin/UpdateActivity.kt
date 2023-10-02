package com.example.admin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.admin.data.DataClass
import com.example.admin.databinding.ActivityUpdateBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var database: DatabaseReference
    private var dataTitle: String? = null
    private var url: Uri? = null

    private var data: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Categories")

        // Get the dataTitle from the intent if it exists
        dataTitle = intent.getStringExtra("Title")
        Toast.makeText(this, dataTitle.toString(), Toast.LENGTH_SHORT).show()

        if (dataTitle != null) {
            fetchAndUpdateData(dataTitle!!)
        }

        binding.updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            startActivityForResult(photoPicker, 1)
        }

        binding.updateButton.setOnClickListener {
            val validationError = validateInputs()
            if (validationError == null) {
                // If inputs are valid, proceed with updating data
                val updateTitle = binding.updateTitle.text.toString()
                val updateDesc = binding.updateDesc.text.toString()
                val updatePriority = binding.updatePriority.text.toString()
                val updateNum = binding.updateNum.text.toString()
                val updateSize = binding.updateSize.text.toString()
                val updateRange = binding.updateRange.text.toString()

                if (dataTitle != null) {
                    updateData(dataTitle!!, updateTitle, updateDesc, updatePriority, updateNum, updateSize, updateRange)
                } else {
                    // Handle the case where dataTitle is not available
                    Toast.makeText(this, "Invalid Data Title", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Display validation message
                Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show()
            }
        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.data = data
            url = data?.data
            binding.updateImage.setImageURI(url)
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAndUpdateData(dataTitle: String) {
        database.orderByChild("dataTitle").equalTo(dataTitle)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        for (dataSnapshot in snapshot.children) {

                            val data = dataSnapshot.getValue(DataClass::class.java)
                            if (data != null) {
                                // Populate your layout views with data
                                binding.updateTitle.text = Editable.Factory.getInstance().newEditable(data.dataTitle)
                                binding.updateDesc.text = Editable.Factory.getInstance().newEditable(data.dataDesc)
                                binding.updatePriority.text = Editable.Factory.getInstance().newEditable(data.dataPriority)
                                binding.updateNum.text = Editable.Factory.getInstance().newEditable(data.dataNum)
                                binding.updateSize.text = Editable.Factory.getInstance().newEditable(data.dataSize)
                                binding.updateRange.text = Editable.Factory.getInstance().newEditable(data.dataRange)

                                // Load and display the image using Glide or any other image loading library
                                if (!data.dataImage.isNullOrEmpty()) {
                                    Glide.with(this@UpdateActivity)
                                        .load(data.dataImage)
                                        .into(binding.updateImage)
                                }
                            }
                        }
                    } else {
                        // Handle the case where no data with the given title was found
                        Toast.makeText(this@UpdateActivity, "No data found for $dataTitle", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UpdateActivity, "Database error: $error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateData(
        dataTitle: String,
        updateTitle: String,
        updateDesc: String,
        updatePriority: String,
        updateNum: String,
        updateSize: String,
        updateRange: String
    ) {
        val dataValues = mutableMapOf<String, Any>()
        dataValues["dataTitle"] = updateTitle
        dataValues["dataDesc"] = updateDesc
        dataValues["dataPriority"] = updatePriority
        dataValues["dataNum"] = updateNum
        dataValues["dataSize"] = updateSize
        dataValues["dataRange"] = updateRange

        if (url != null) {
            // Upload the selected image and get the download URL
            val storageRef = FirebaseStorage.getInstance().reference.child("Task Images")
                .child(url!!.lastPathSegment!!)

            storageRef.putFile(url!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, now retrieve the download URL
                    storageRef.downloadUrl.addOnSuccessListener { uRL ->
                        dataValues["dataImage"] = uRL.toString()
                        updateDatabase(dataTitle, dataValues)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error if image upload fails
                    Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            // Update the Firebase Realtime Database without changing the image URL
            updateDatabase(dataTitle, dataValues)
        }
    }

    private fun updateDatabase(dataTitle: String, dataValues: Map<String, Any>) {
        database.child(dataTitle).updateChildren(dataValues)
            .addOnSuccessListener {
                // Clear input fields and show a success message
                binding.updateTitle.text
                binding.updateDesc.text.clear()
                binding.updatePriority.text.clear()
                binding.updateNum.text.clear()
                binding.updateSize.text.clear()
                binding.updateRange.text.clear()
                Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT).show()
            }
    }


    private fun validateInputs(): String? {
        // Validate Category Name (updateTitle)
        val categoryName = binding.updateTitle.text.toString().trim()
        if (categoryName.isEmpty()) {
            return "Category Name is required"
        } else if (!categoryName.matches(Regex("^[a-zA-Z ]+\$"))) {
            return "Category Name should contain only letters"
        }

        // Validate Category Description (updateDesc)
        val categoryDesc = binding.updateDesc.text.toString().trim()
        if (categoryDesc.isEmpty()) {
            return "Category Description is required"
        }

        // Validate Category Type (updatePriority)
        val categoryType = binding.updatePriority.text.toString().trim()
        if (categoryType.isEmpty()) {
            return "Category Type is required"
        } else if (!categoryType.matches(Regex("^(mammals|marine fish|cetaceans|sharks|sea turtle|coral|seals|octopus|dugong|squid|jellyfish|crustacean|seahorse|eel|turtle|ocean fish)$", RegexOption.IGNORE_CASE))) {
            return "Invalid Category Type. Please enter a valid ocean animal type."
        }

        // Validate Category Number (updateNum)
        val categoryNumber = binding.updateNum.text.toString().trim()
        if (categoryNumber.isEmpty()) {
            return "Category Number is required"
        } else if (!categoryNumber.matches(Regex("^[0-9]+\$"))) {
            return "Category Number should contain only numbers"
        }

        // Validate Category Size (updateSize)
        val categorySize = binding.updateSize.text.toString().trim()
        if (categorySize.isEmpty()) {
            return "Category Size is required"
        } else if (!categorySize.matches(Regex("^[0-9]+(\\.[0-9]+)?(cm|kg)$"))) {
            return "Category Size should be a decimal number followed by 'cm' or 'kg'"
        }

        // Validate Category Range (updateRange)
        val categoryRange = binding.updateRange.text.toString().trim()
        if (categoryRange.isEmpty()) {
            return "Category Range is required"
        } else if (!categoryRange.matches(Regex("^[a-zA-Z ]+\$"))) {
            return "Category Range should contain only letters"
        }

        return null
       }
}