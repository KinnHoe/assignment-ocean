package com.example.assignment_ocean

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.assignment_ocean.databinding.FragmentUpdatePostBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UpdatePostFragment : Fragment() {

    private lateinit var binding: FragmentUpdatePostBinding
    private var updatedImageUri: Uri? = null
    private var postId: String? = null
    private var caption: String? = null
    private var imageURL: String? = null

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
        caption = arguments?.getString("caption")
        imageURL = arguments?.getString("imageURL")

        setupViews()

        // Populate the reuploadCaption field with the selected post's caption
        binding.reuploadCaption.setText(caption)

        // Load the image from the imageURL and set it in reuploadImage ImageView
        Glide.with(requireContext())
            .load(imageURL)
            .placeholder(R.drawable.uploadimg) // Replace with your placeholder image
            .into(binding.reuploadImage)
    }

    private fun setupViews() {
        binding.reuploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            imagePicker.launch(photoPicker)
        }

        binding.updateButton.setOnClickListener {
            showConfirmationDialog()
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

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Update")
        builder.setMessage("Are you sure you want to update this post?")
        builder.setPositiveButton("Update") { _, _ ->
            // User confirmed the update, proceed with the updatePost() function
            updatePost()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            // User canceled the update, do nothing
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun updatePost() {
        if (postId == null) {
            Toast.makeText(requireContext(), "Invalid Post ID", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isNetworkConnected(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = createProgressDialog()
        dialog.show()

        val updatedImageUri = updatedImageUri ?: run {
            // If no new image is selected, use the previous image URL
            updateData(imageURL, binding.reuploadCaption.text.toString())
            dialog.dismiss()
            return
        }

        val lastPathSegment = updatedImageUri.lastPathSegment
        if (lastPathSegment == null || lastPathSegment.isEmpty()) {
            dialog.dismiss()
            showError("Invalid image URI")
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference
            .child("Task Images")
            .child(lastPathSegment)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                var updatedImageURL = imageURL // Initialize with the previous image URL

                val taskSnapshot = storageReference.putFile(updatedImageUri).await()
                val urlImage = taskSnapshot.storage.downloadUrl.await()
                updatedImageURL = urlImage.toString()

                val updatedCaption = binding.reuploadCaption.text.toString()

                if (updatedCaption.isEmpty() || updatedImageURL == imageURL) {
                    dialog.dismiss()
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Photo and caption are the same as the previous ones",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                // Update data only if there are changes
                updateData(updatedImageURL, updatedCaption)

                dialog.dismiss()
            } catch (e: Exception) {
                dialog.dismiss()
                showError("Failed to upload image: ${e.message}")
            }
        }
    }



    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun createProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        return builder.create()
    }

    private fun updateData(updatedImageURL: String?, updatedCaption: String?) {
        if (postId == null) {
            Toast.makeText(requireContext(), "Invalid Post ID", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "timestamp" to ServerValue.TIMESTAMP
        )

        if (updatedImageURL != imageURL) {
            updates["photo"] = updatedImageURL!!
        }

        if (updatedCaption != caption) {
            updates["caption"] = updatedCaption!!
        }

        if (updates.isEmpty()) {
            // Nothing to update
            Toast.makeText(requireContext(), "Nothing to update", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseDatabase.getInstance().getReference("Posts").child(postId!!)
            .updateChildren(updates)
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
