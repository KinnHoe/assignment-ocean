package com.example.assignment_ocean

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.assignment_ocean.databinding.FragmentUpdatePostBinding
import com.example.assignment_ocean.models.Post
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class UpdatePostFragment : Fragment() {

    private lateinit var binding: FragmentUpdatePostBinding
    private var updatedImageUri: Uri? = null
    private var postId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getString("postId")

        setupViews()
    }

    private fun setupViews() {
        binding.reuploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            imagePicker.launch(photoPicker)
        }

        binding.updateButton.setOnClickListener {
            updatePost()
        }
    }

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            updatedImageUri = data?.data
            binding.reuploadImage.setImageURI(updatedImageUri)
        } else {
            Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePost() {
        if (postId == null) {
            Toast.makeText(requireContext(), "Invalid Post ID", Toast.LENGTH_SHORT).show()
            return
        }

        if (updatedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isNetworkConnected()) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = createProgressDialog()
        dialog.show()

        val storageReference = FirebaseStorage.getInstance().reference
            .child("Task Images")
            .child(updatedImageUri!!.lastPathSegment ?: "")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val taskSnapshot = storageReference.putFile(updatedImageUri!!).await()
                val urlImage = taskSnapshot.storage.downloadUrl.await()
                val imageURL = urlImage.toString()

                // Update data only after the image upload is complete
                updateData(imageURL)

                dialog.dismiss()
            } catch (e: Exception) {
                dialog.dismiss()
                showError("Failed to upload image: ${e.message}")
            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun createProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        return builder.create()
    }

    private fun getMalaysiaTimestamp(): String {
        val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
        val malaysiaCalendar = Calendar.getInstance(malaysiaTimeZone)
        val malaysiaDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        malaysiaDateFormat.timeZone = malaysiaTimeZone
        return malaysiaDateFormat.format(malaysiaCalendar.time)
    }

    private fun updateData(imageURL: String) {
        val updatedCaption = binding.reuploadCaption.text.toString()

        if (updatedCaption.isEmpty()) {
            Toast.makeText(requireContext(), "Caption cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTimestamp = getMalaysiaTimestamp()

        val updatedPost = Post(id = postId!!, caption = updatedCaption, photo = imageURL, timestamp = updatedTimestamp)

        FirebaseDatabase.getInstance().getReference("Posts").child(postId!!)
            .setValue(updatedPost)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Post updated", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    showError("Failed to update post: ${task.exception?.message}")
                }
            }
    }

    private fun clearFields() {
        binding.reuploadCaption.text.clear()
        binding.reuploadImage.setImageResource(R.drawable.image_placeholder) // Replace with your default image resource
    }

    private fun showError(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
