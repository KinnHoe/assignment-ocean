package com.example.assignment_ocean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment_ocean.databinding.FragmentMomentBinding
import com.example.assignment_ocean.models.Post

class Moment : Fragment() {

    private lateinit var binding: FragmentMomentBinding
    private lateinit var momentAdapter: MomentPostAdapter
    private lateinit var viewModel: MomentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMomentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel and RecyclerView Adapter
        viewModel = ViewModelProvider(this).get(MomentViewModel::class.java)
        momentAdapter = MomentPostAdapter()

        // Set up RecyclerView
        binding.momentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = momentAdapter
        }

        // Observe LiveData from ViewModel
        viewModel.getMomentData().observe(viewLifecycleOwner, { momentDataList ->
            // Update the adapter with the new data
            momentAdapter.submitList(momentDataList)
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_update_post -> {
                val post = // Get the selected post here
                    viewModel.updatePost(post,
                        onSuccess = {
                            // Handle successful update
                        },
                        onError = { errorMessage ->
                            // Handle update error, show error message
                        }
                    )
                return true
            }
            R.id.menu_delete_post -> {
                val post = // Get the selected post here
                    viewModel.deletePost(post,
                        onSuccess = {
                            // Handle successful deletion
                        },
                        onError = { errorMessage ->
                            // Handle deletion error, show error message
                        }
                    )
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onUpdatePostClicked(post: Post) {
        // Use Navigation Component to navigate to fragment_update_post.xml
        findNavController().navigate(R.id.action_moment_to_updatePost)
    }


    override fun onDeletePostClicked(post: Post) {
        viewModel.deletePost(
            post,
            onSuccess = {
                // Show a Toast message for successful deletion
                Toast.makeText(requireContext(), "Post deleted", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                // Show a Toast message for deletion error
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }


}
