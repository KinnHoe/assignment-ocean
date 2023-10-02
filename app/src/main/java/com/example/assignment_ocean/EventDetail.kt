package com.example.assignment_ocean

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.assignment_ocean.data.EventModel
import com.example.assignment_ocean.data.UserJoinEventModel
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
        val joinEventBtn = findViewById<Button>(R.id.joinEvent)
        // Retrieve the event title from the intent extras
        val eventTitle = intent.getStringExtra("eventTitle")
        val username = intent.getStringExtra("username")

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


        checkJoin(eventTitle, username)

        // Set the click listener for the join event button
        joinEventBtn.setOnClickListener {
            val joinEventRef: DatabaseReference = database.getReference("JoinEvent")
            joinEventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isAlreadyJoined = false
                    var joinEventKey: String? = null
                    for (eventSnapshot in snapshot.children) {
                        val joinEvent = eventSnapshot.getValue(UserJoinEventModel::class.java)
                        if (joinEvent != null) {
                            val eventId = joinEvent.eventName
                            val existingUsername = joinEvent.username
                            Log.d("Debug", "eventId: $eventId, eventTitle: $eventTitle, existingUsername: $existingUsername, username: $username")



                            // Check if both eventId and username match
                            if (eventId == eventTitle && existingUsername == username) {
                                // You have already joined the event, set the flag and break
                                isAlreadyJoined = true
                                joinEventKey = eventSnapshot.key
                                break
                            }
                        }
                    }

                    if (isAlreadyJoined) {
                        // You have already joined the event, show a message
                        deleteEvent(joinEventKey.toString())
                        joinEventBtn.text = "join Event"

                    } else {
                        // User has not joined the event, so you can join here
                        val joinEvent = UserJoinEventModel(username, eventTitle)

                        // Set the value at the generated unique ID
                        val userEventRef = joinEventRef.push()
                        userEventRef.setValue(joinEvent).addOnSuccessListener {
                            Toast.makeText(this@EventDetail, "Joined the event", Toast.LENGTH_SHORT).show()
                            joinEventBtn.text = "Unjoin Event"
                        }.addOnFailureListener {
                            Toast.makeText(this@EventDetail, "Failed to join", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any database errors here
                    Toast.makeText(this@EventDetail, "Database error", Toast.LENGTH_SHORT).show()
                }
            })
        }






        backNav.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)

        }
    }




    private fun deleteEvent(joinEventKey: String){
        val joinEventBtn = findViewById<Button>(R.id.joinEvent)
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete, null)
        builder.setView(dialogView)
        val dialog = builder.create()

        val confirmButton = dialogView.findViewById<Button>(R.id.confirmDeleteButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelDeleteButton)

        confirmButton.setOnClickListener {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val joinEventRef: DatabaseReference = database.getReference("JoinEvent")
            joinEventRef.child(joinEventKey).removeValue().addOnSuccessListener {

                Toast.makeText(this, "You have unjoined this event", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Unable to delete", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            joinEventBtn.text = "Unjoin Event"
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkJoin(eventTitle: String?, username: String?) {
        if (eventTitle != null && username != null) {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val joinEventRef: DatabaseReference = database.getReference("JoinEvent")
            joinEventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isAlreadyJoined = false // Flag to check if the user has already joined
                    for (eventSnapshot in snapshot.children) {
                        val joinEvent = eventSnapshot.getValue(UserJoinEventModel::class.java)
                        if (joinEvent != null) {
                            val eventId = joinEvent.eventName
                            val existingUsername = joinEvent.username
                            Log.d("Debug", "eventId: $eventId, eventTitle: $eventTitle, existingUsername: $existingUsername, username: $username")

                            // Check if both eventId and username match
                            if (eventId == eventTitle && existingUsername == username) {
                                // You have already joined the event, set the flag and break
                                isAlreadyJoined = true
                                break
                            }
                        }
                    }
                    val joinEventBtn = findViewById<Button>(R.id.joinEvent)

                    // Update the button text based on whether the user has joined the event or not
                    if (isAlreadyJoined) {
                        joinEventBtn.text = "Unjoin Event"
                    } else {
                        joinEventBtn.text = "Join Event"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any database errors here
                }
            })
        }
    }


}

