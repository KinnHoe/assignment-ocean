package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.adapter.EventAdapterAdmin
import com.example.admin.data.EventModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RetrieveEventActivity : AppCompatActivity() {

    private lateinit var dbref: DatabaseReference
    private lateinit var adapter: EventAdapterAdmin
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList: ArrayList<EventModel>

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_event)

        val topNavEventAdmin = findViewById<ImageView>(R.id.top_nav_event1)
        val topNavPersonAdmin = findViewById<ImageView>(R.id.top_nav_person1)
        val topNavOceanlifeAdmin = findViewById<ImageView>(R.id.top_nav_oceanlife1)

        eventArrayList = arrayListOf<EventModel>()
        getUserData()

        val layoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.recycleView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventAdapterAdmin(eventArrayList)
        recyclerView.adapter = adapter

        topNavPersonAdmin.setOnClickListener {
            Toast.makeText(this,"Set Intent at admin main activity",Toast.LENGTH_SHORT).show()
        }

        topNavOceanlifeAdmin.setOnClickListener {
            Toast.makeText(this,"Set Intent at admin main activity",Toast.LENGTH_SHORT).show()
        }

        topNavEventAdmin.setOnClickListener {
            Toast.makeText(this,"Set Intent at admin main activity",Toast.LENGTH_SHORT).show()
        }


    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("events")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(EventModel::class.java)
                        eventArrayList.add(event!!)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error as needed
            }
        })
    }
}
