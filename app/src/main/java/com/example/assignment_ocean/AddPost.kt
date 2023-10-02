package com.example.assignment_ocean

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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.assignment_ocean.databinding.FragmentAddPostBinding
import com.example.assignment_ocean.data.Post
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddPost : Fragment() {

    private lateinit var binding: FragmentAddPostBinding
    private var imageURL: String? = null
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val addPost = binding.uploadImage
        val shareButton = binding.shareButton

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                uri = data?.data
                addPost.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        addPost.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        shareButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        if (uri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isNetworkConnected(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = createProgressDialog()
        dialog.show()

        val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
            .child(uri!!.lastPathSegment ?: "")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val taskSnapshot = storageReference.putFile(uri!!).await()
                val urlImage = taskSnapshot.storage.downloadUrl.await()
                imageURL = urlImage.toString()

                // Upload data only after the image upload is complete
                uploadData()

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

    private fun uploadData() {
        val caption = binding.uploadCaption.text.toString()

        if (caption.isEmpty()) {
            Toast.makeText(requireContext(), "Caption cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique key for the post
        val id = FirebaseDatabase.getInstance().getReference("Posts").push().key

        // Create a Post object with the generated ID, caption, imageURL, and Firebase timestamp
        val post = Post(id, caption, imageURL ?: "")

        if (id != null) {
            FirebaseDatabase.getInstance().getReference("Posts").child(id)
                .setValue(post)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Post shared", Toast.LENGTH_SHORT).show()
                        clearFields()
                    } else {
                        showError("Failed to save post: ${task.exception?.message}")
                    }
                }
        } else {
            showError("Failed to generate a unique ID for the post")
        }
    }

    private fun clearFields() {
        binding.uploadCaption.text.clear()
        binding.uploadImage.setImageResource(R.drawable.uploadimg)
    }

    private fun showError(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
