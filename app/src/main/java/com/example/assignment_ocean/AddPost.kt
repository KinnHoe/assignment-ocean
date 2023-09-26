package com.example.assignment_ocean

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.assignment_ocean.databinding.FragmentAddPostBinding
import com.example.assignment_ocean.models.Post
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AddPost : Fragment() {

    private lateinit var binding: FragmentAddPostBinding
    private var imageURL: String? = null
    private var uri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addPost = view.findViewById<ImageView>(R.id.uploadImage)
        val captionEditText = view.findViewById<EditText>(R.id.uploadCaption)
        val shareButton = view.findViewById<Button>(R.id.shareButton)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
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

        val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
            .child(uri!!.lastPathSegment!!)
        val builder = AlertDialog.Builder(requireContext())
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
            Toast.makeText(
                requireContext(),
                "Failed to upload image: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadData() {
        val caption = binding.uploadCaption.text.toString()

        if (caption.isEmpty()) {
            Toast.makeText(requireContext(), "Caption cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val post: Post

        if (imageURL != null) {
            post = Post(caption, imageURL!!, getMalaysiaTimestamp()) // Add Malaysia timestamp here
        } else {
            // Handle the case when imageURL is null, e.g., provide a default image URL.
            post = Post(caption, "", getMalaysiaTimestamp()) // Add Malaysia timestamp here
        }

        FirebaseDatabase.getInstance().getReference("Posts").push()
            .setValue(post).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Post Saved", Toast.LENGTH_SHORT).show()
                    // Clear UI components or navigate to a different screen as needed.
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to save post: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun getMalaysiaTimestamp(): String {
        val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
        val malaysiaCalendar = Calendar.getInstance(malaysiaTimeZone)
        val malaysiaDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        malaysiaDateFormat.timeZone = malaysiaTimeZone
        return malaysiaDateFormat.format(malaysiaCalendar.time)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }
}
