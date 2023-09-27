package com.example.assignment_ocean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment_ocean.databinding.FragmentMomentBinding
import com.example.assignment_ocean.models.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Moment : Fragment() {

    private lateinit var binding: FragmentMomentBinding
    private lateinit var momentAdapter: MomentPostAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMomentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and MomentAdapter
        momentAdapter = MomentPostAdapter()
        binding.momentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = momentAdapter
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts")

        // Add ValueEventListener to fetch and display data
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val momentList = mutableListOf<Post>()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        momentList.add(post)
                    }
                }

                // Update the adapter's data on the main thread
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        momentAdapter.submitList(momentList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })
    }
}
