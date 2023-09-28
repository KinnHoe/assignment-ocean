package com.example.assignment_ocean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment_ocean.databinding.FragmentMomentBinding
import com.example.assignment_ocean.models.Post

class Moment : Fragment(), MomentPostAdapterListener {

    private lateinit var binding: FragmentMomentBinding
    private lateinit var momentAdapter: MomentPostAdapter
    private lateinit var viewModel: MomentViewModel
    private lateinit var navController: NavController

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
        momentAdapter = MomentPostAdapter(this)

        // Set up RecyclerView
        binding.momentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = momentAdapter
        }

        // Obtain the NavController
    /*    navController = Navigation.findNavController(view)*/

        // Observe LiveData from ViewModel
        viewModel.getMomentData().observe(viewLifecycleOwner, { momentDataList ->
            // Update the adapter with the new data
            momentAdapter.submitList(momentDataList)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_update_post -> {
                val postIdString = momentAdapter.getItemId(0)?.toString()
                val post = momentAdapter.currentList.find {
                    val postIdLong = it.id?.toLongOrNull()
                    postIdLong != null && postIdString != null && postIdLong == postIdString.toLongOrNull()
                }
                post?.let {
                    viewModel.updatePost(
                        it,
                        onSuccess = {
                            // Handle successful update
                            Toast.makeText(requireContext(), "Post updated", Toast.LENGTH_SHORT).show()
                        },
                        onError = { errorMessage ->
                            // Handle update error, show error message
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                return true
            }
            R.id.menu_delete_post -> {
                val postIdString = momentAdapter.getItemId(0)?.toString()
                val post = momentAdapter.currentList.find {
                    val postIdLong = it.id?.toLongOrNull()
                    postIdLong != null && postIdString != null && postIdLong == postIdString.toLongOrNull()
                }
                post?.let {
                    viewModel.deletePost(
                        it,
                        onSuccess = {
                            // Handle successful deletion
                            Toast.makeText(requireContext(), "Post deleted", Toast.LENGTH_SHORT).show()
                        },
                        onError = { errorMessage ->
                            // Handle deletion error, show error message
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onUpdatePostClicked(post: Post) {
        val updateFragment = UpdatePostFragment()

        // Pass the postId to the UpdatePostFragment using arguments
        val bundle = Bundle()
        bundle.putString("postId", post.id) // Assuming post.id contains the postId
        updateFragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // Replace R.id.fragment_container with the actual ID of the container where you want to replace the fragment
        transaction.replace(R.id.frame_layout, updateFragment)
        transaction.addToBackStack(null) // Optional: Add to back stack for back navigation
        transaction.commit()
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
