package com.example.assignment_ocean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment_ocean.databinding.FragmentMomentBinding

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
                // Handle "Update Post" action here
                return true
            }
            R.id.menu_delete_post -> {
                // Handle "Delete Post" action here
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
