package com.example.assignment_ocean

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.assignment_ocean.data.EventModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class EventDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        val backNav = findViewById<ImageView>(R.id.BackIcon)
        // Retrieve the event title from the intent extras
        val eventTitle = intent.getStringExtra("eventTitle")

        // Initialize Firebase database reference
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val eventsRef: DatabaseReference = database.getReference("events")

        // Check if eventTitle is not null and fetch event data
        if (eventTitle != null) {
            eventsRef.orderByChild("eventTitle").equalTo(eventTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (eventSnapshot in snapshot.children) {
                                val eventModel = eventSnapshot.getValue(EventModel::class.java)
                                if (eventModel != null) {
                                    // Populate your layout views with event data
                                    // For example:
                                    val eventImage = findViewById<ImageView>(R.id.eventImage)
                                    val eventTitleTextView = findViewById<TextView>(R.id.eventTitle)
                                    val eventDateTextView = findViewById<TextView>(R.id.eventDate)
                                    val eventDescriptionTextView = findViewById<TextView>(R.id.eventDescription)
                                    val eventTime = findViewById<TextView>(R.id.eventTime)

                                    eventTitleTextView.text = eventModel.eventTitle
                                    eventDateTextView.text = eventModel.eventDate
                                    eventDescriptionTextView.text = eventModel.eventDecs
                                    eventTime.text = eventModel.eventTime

                                    // Load and display the event image using Glide
                                    if (!eventModel.eventImage.isNullOrEmpty()) {
                                        Glide.with(applicationContext)
                                            .load(eventModel.eventImage)
                                            .into(eventImage)
                                    }
                                }
                            }
                        } else {
                            // Handle the case where no event with the given title was found
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle any database errors here
                    }
                })
        }

        backNav.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }


}

