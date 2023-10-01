package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.adapter.EventAdapterAdmin
import com.example.admin.data.EventModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {


    private lateinit var dbref: DatabaseReference
    private lateinit var adapter: EventAdapterAdmin
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList: ArrayList<EventModel>

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_event)
        val uplaodBtn = findViewById<Button>(R.id.mainUpload)
        val topNavEventAdmin = findViewById<ImageView>(R.id.top_nav_event1)
        val topNavPersonAdmin = findViewById<ImageView>(R.id.top_nav_person1)
        val topNavOceanlifeAdmin = findViewById<ImageView>(R.id.top_nav_oceanlife1)

        eventArrayList = arrayListOf<EventModel>()
        getUserData()

        val layoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.recycleView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventAdapterAdmin(this, eventArrayList)
        recyclerView.adapter = adapter

        topNavPersonAdmin.setOnClickListener {
            Toast.makeText(this,"Set Intent at admin main activity",Toast.LENGTH_SHORT).show()
        }

        topNavOceanlifeAdmin.setOnClickListener {
            Toast.makeText(this,"Set Intent at admin main activity",Toast.LENGTH_SHORT).show()
        }

        topNavEventAdmin.setOnClickListener {

            val intent = Intent(this@MainActivity, RetrieveEventActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        uplaodBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEventActivity::class.java)
            startActivity(intent)
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