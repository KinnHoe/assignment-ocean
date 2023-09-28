package com.example.assignment_ocean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.assignment_ocean.databinding.FragmentUpdatePostBinding
import com.example.assignment_ocean.models.Post

class UpdatePostFragment : Fragment() {

    private lateinit var binding: FragmentUpdatePostBinding // Create a binding object for your XML layout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getString("postId") // Get the postId from arguments (passed from MomentFragment)

        // Set click listener for the update button
        binding.updateButton.setOnClickListener {
            val updatedCaption = binding.reuploadCaption.text.toString()
            // Assuming you have an updated image URL as well
            val updatedPhoto = "your_updated_image_url_here"

            // Check if both caption and photo are not empty
            if (updatedCaption.isNotEmpty() && updatedPhoto.isNotEmpty() && postId != null) {
                // Create a new Post object with updated data
                val updatedPost = Post(id = postId, caption = updatedCaption, photo = updatedPhoto)

                // Find the NavController from the parent fragment (MomentFragment)
                val parentNavController = parentFragment?.findNavController()

                // Use the parent NavController to navigate back to the previous screen (MomentFragment)
                parentNavController?.popBackStack()

                // Use ViewModel to update the post
                val viewModel = ViewModelProvider(requireActivity()).get(MomentViewModel::class.java)
                viewModel.updatePost(
                    updatedPost,
                    onSuccess = {
                        // Handle successful update
                        Toast.makeText(requireContext(), "Post updated", Toast.LENGTH_SHORT).show()
                    },
                    onError = { errorMessage ->
                        // Handle update error, show error message
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // Handle empty fields and show an error message
                Toast.makeText(requireContext(), "Caption and photo cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
