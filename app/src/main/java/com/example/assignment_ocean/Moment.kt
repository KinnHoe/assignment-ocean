package com.example.assignment_ocean


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_ocean.models.Post
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Moment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var momentAdapter: MomentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_moment, container, false)
        recyclerView = view.findViewById(R.id.feed_recycler)
        momentAdapter = MomentAdapter()

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("posts")

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = momentAdapter

        // Listen for changes in the database and update the RecyclerView
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(Post::class.java)
                if (post != null) {
                    val momentData = Post(post.caption, post.photo ?: "")
                    momentAdapter.addMoment(momentData)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle data changes if needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle data removal if needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle data movement if needed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors if needed
            }
        })

        return view
    }
}
