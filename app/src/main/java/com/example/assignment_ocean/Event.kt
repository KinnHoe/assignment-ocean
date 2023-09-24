package com.example.assignment_ocean

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_ocean.adapter.EventAdapter
import com.example.assignment_ocean.data.EventModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions



class Event : Fragment() {

    private lateinit var dbref : DatabaseReference
    private lateinit var adapter : EventAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList : ArrayList<EventModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventArrayList = arrayListOf<EventModel>()
        getUserData()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = GridLayoutManager(context, 2)
        recyclerView = view.findViewById(R.id.recycleView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventAdapter(eventArrayList)
        recyclerView.adapter = adapter


    }


    private fun getUserData(){
        dbref = FirebaseDatabase.getInstance().getReference("events")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (eventSnapshot in snapshot.children){

                        val event = eventSnapshot.getValue(EventModel::class.java)
                        eventArrayList.add(event!!)

                    }

                    recyclerView.adapter = EventAdapter(eventArrayList)


                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }



        })
    }


}