package com.example.assignment_ocean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


class ShareFragment : Fragment() {


    private val imageSelectionViewModel by activityViewModels<ImageSelectionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_share, container, false)

        // Retrieve selected images from the ViewModel
        val selectedImages = imageSelectionViewModel.selectedImages

        // Initialize and populate ImageView or RecyclerView with selectedImages
        val imageView = view.findViewById<ImageView>(R.id.post_image)
        // Load and display selectedImages here

        // Handle EditText and caption input
        val captionInput = view.findViewById<EditText>(R.id.caption_input)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up OnClickListener for the "Next" button
        val nextButton = view.findViewById<Button>(R.id.nextButton)
        nextButton.setOnClickListener {
            if (imageSelectionViewModel.selectedImages.isNotEmpty()) {
                // Navigate to ShareFragment
                val shareFragment = ShareFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, shareFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // Handle the case where no image is selected
                // Show a message or take appropriate action
            }
        }
    }
}
