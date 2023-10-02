package com.example.admin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.admin.data.DataClass
import com.example.admin.databinding.ActivityUploadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.text.DateFormat
import java.util.Calendar

class UploadActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityUploadBinding
    var imageURL: String? = null
    var uri: Uri? = null
    private lateinit var uploadTitle: EditText
    private lateinit var uploadDesc: EditText
    private lateinit var uploadPriority: EditText
    private lateinit var uploadNum: EditText
    private lateinit var uploadSize: EditText
    private lateinit var uploadRange: EditText
    private lateinit var saveButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize your views
        uploadTitle = findViewById(R.id.uploadTitle)
        uploadDesc = findViewById(R.id.uploadDesc)
        uploadPriority = findViewById(R.id.uploadPriority)
        uploadNum = findViewById(R.id.uploadNum)
        uploadSize = findViewById(R.id.uploadSize)
        uploadRange = findViewById(R.id.uploadRange)
        saveButton = findViewById(R.id.saveButton)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data!!.data
                binding.uploadImage.setImageURI(uri)
            } else {
                Toast.makeText(this@UploadActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }
        binding.uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.saveButton.setOnClickListener {
            val validationError = validateInputs()
            if (validationError == null) {
                // If inputs are valid, proceed with saving data
                saveData()
            } else {
                // Display the validation error message to the user
                Toast.makeText(this@UploadActivity, validationError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData(){
        //Save data to the firebase storage of Task Images
        val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
            .child(uri!!.lastPathSegment!!)
        //generate progress dialog
        val builder = AlertDialog.Builder(this@UploadActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageURL = urlImage.toString()
            uploadData()
            dialog.dismiss()
        }.addOnFailureListener {
            dialog.dismiss()
        }
    }

    private fun uploadData(){
        val title = binding.uploadTitle.text.toString()
        val desc = binding.uploadDesc.text.toString()
        val priority = binding.uploadPriority.text.toString()
        val num = binding.uploadNum.text.toString()
        val size = binding.uploadSize.text.toString()
        val range = binding.uploadRange.text.toString()

        database=FirebaseDatabase.getInstance().getReference("Categories")
        val dataClass = DataClass(title, desc, priority, imageURL, num, size, range)

        database.child(title).setValue(dataClass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@UploadActivity, "Saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@UploadActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): String? {
        // Validate Category Name (Title)
        val categoryName = uploadTitle.text.toString().trim()
        if (categoryName.isEmpty()) {
            return "Category Name is required"
        } else if (!categoryName.matches(Regex("^[a-zA-Z ]+\$"))) {
            return "Category Name should contain only letters no numbers and symbol"
        }

        // Validate Category Description
        val categoryDesc = uploadDesc.text.toString().trim()
        if (categoryDesc.isEmpty()) {
            return "Category Description is required"
        }

        // Validate Category Type (Priority)
        val categoryType = uploadPriority.text.toString().trim()
        if (categoryType.isEmpty()) {
            return "Category Type is required"
        } else if (!categoryType.matches(Regex("^[a-zA-Z ]+\$"))) {
            return "Category Type should contain only letters"
        }

        // Validate Category Number (uploadNum)
        val categoryNumber = uploadNum.text.toString().trim()
        if (categoryNumber.isEmpty()) {
            return "Category Number is required"
        } else if (!categoryNumber.matches(Regex("^[0-9]+\$"))) {
            return "Category Number should contain only numbers"
        }

        // Validate Category Size (uploadSize)
        val categorySize = uploadSize.text.toString().trim()
        if (categorySize.isEmpty()) {
            return "Category Size is required"
        } else if (!categorySize.matches(Regex("^[a-zA-Z0-9 ]+\$"))) {
            return "Category Size should contain only letters and numbers"
        }

        // Validate Category Range (uploadRange)
        val categoryRange = uploadRange.text.toString().trim()
        if (categoryRange.isEmpty()) {
            return "Category Range is required"
        } else if (!categoryRange.matches(Regex("^[a-zA-Z ]+\$"))) {
            return "Category Range should contain only letters"
        }

        return null
    }


}