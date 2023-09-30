package com.example.admin.adapter

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.google.android.material.imageview.ShapeableImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.admin.UpdateEventActivity
import com.example.admin.data.EventModel
import com.example.admin.database.EventAppDb
import com.example.admin.viewmodel.EventViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventAdapterAdmin(private val context: Context, private val eventList: ArrayList<EventModel>) :
    RecyclerView.Adapter<EventAdapterAdmin.MyViewHolder>() {
    private lateinit var database: DatabaseReference
    var eventKey: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_event_list_view_admin, parent, false)
        return MyViewHolder(itemView)
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val networkInfo = connectivityManager?.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting
        Log.d("InternetStatus", "Internet connected: $isConnected")
        return isConnected
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentEvent = eventList[position]
        var eventTitle = currentEvent.eventTitle

        if (isInternetConnected(holder.itemView.context)) {
            // Load and display the image using Glide
            if (!currentEvent.eventImage.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(currentEvent.eventImage)
                    .transition(DrawableTransitionOptions.withCrossFade()) // Optional transition animation
                    .into(holder.eventImage)
                holder.eventTitle.text = currentEvent.eventTitle
                holder.eventDate.text = currentEvent.eventDate

                holder.modifyEventBtn.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, UpdateEventActivity::class.java)
                    intent.putExtra("eventTitle", currentEvent.eventTitle)
                    context.startActivity(intent)
                }
                holder.deleteEventBtn.setOnClickListener {
                    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val eventsRef: DatabaseReference = database.getReference("events")

                    if (eventTitle != null) {
                        eventsRef.orderByChild("eventTitle").equalTo(eventTitle)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (eventSnapshot in snapshot.children) {
                                            eventKey = eventSnapshot.key
                                            eventsRef.child(eventKey.toString()).removeValue()
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        holder.itemView.context,
                                                        "Deleted",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }.addOnFailureListener {
                                                    Toast.makeText(
                                                        holder.itemView.context,
                                                        "Unable to delete",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
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
                }
            } else {
                // Handle the case where eventImage is empty or null
            }
        } else {
            // Show "You do not have internet" toast immediately
            Toast.makeText(holder.itemView.context, "You do not have internet", Toast.LENGTH_SHORT).show()
            fetchEventDataFromRoom(holder, currentEvent)
        }
    }

    private fun fetchEventDataFromRoom(holder: MyViewHolder, currentEvent: EventModel) {
        // Create a coroutine scope
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Perform Room database query
                val eventDatabase = EventAppDb.getAppDatabase(context)
                val eventDao = eventDatabase?.eventDao()

                // Retrieve event data from the Room database
                val eventData = eventDao?.getAllEventInfo()

                eventData?.let { roomDataList ->
                    // Find the matching event in the Room data
                    val roomData = roomDataList.find { it.eventTitle == currentEvent.eventTitle }

                    // Update the UI with data from Room Database
                    withContext(Dispatchers.Main) {
                        roomData?.let { data ->
                            // Populate your UI elements with data from roomData
                            holder.eventTitle.text = data.eventTitle
                            holder.eventDate.text = data.eventDate

                            if (!data.eventImage.isNullOrEmpty()) {
                                Glide.with(holder.itemView.context)
                                    .load(data.eventImage)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(holder.eventImage)
                            }


                            // Log a message to check if the function is called
                            Log.d("MyTag", "fetchEventDataFromRoom: Data retrieved from Room")

                            // Handle modifyEventBtn and deleteEventBtn click listeners for Room data
                            holder.modifyEventBtn.setOnClickListener {
                                Toast.makeText(context, "You need to have internet to update", Toast.LENGTH_SHORT).show()
                            }
                            holder.deleteEventBtn.setOnClickListener {
                                Toast.makeText(context, "You need to have internet to delete", Toast.LENGTH_SHORT).show()

                            }
                        } ?: run {
                            // Handle the case where no matching data was found in Room
                            Log.d("MyTag", "fetchEventDataFromRoom: No matching data found in Room")
                        }
                    }
                } ?: run {
                    // Handle the case where no data was retrieved from the Room database
                    Log.d("MyTag", "fetchEventDataFromRoom: No data retrieved from Room")
                }
            } catch (e: Exception) {
                // Handle any exceptions or errors
                Log.e("Database", "Error fetching event data: ${e.message}", e)
            }
        }
    }


    override fun getItemCount(): Int {
        return eventList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val eventImage = itemView.findViewById<ShapeableImageView>(R.id.EventImg)
        val eventDate: TextView = itemView.findViewById(R.id.EventDate)
        val eventTitle: TextView = itemView.findViewById(R.id.EventTitle)
        val modifyEventBtn: ImageView = itemView.findViewById(R.id.modifyEventBtn)
        val deleteEventBtn: ImageView = itemView.findViewById(R.id.deleteEventBtn)
    }
}
