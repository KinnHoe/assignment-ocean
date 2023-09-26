package com.example.admin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.google.android.material.imageview.ShapeableImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.admin.UpdateEventActivity
import com.example.admin.data.EventModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EventAdapterAdmin(private val eventList: ArrayList<EventModel>) : RecyclerView.Adapter<EventAdapterAdmin.MyViewHolder>(){
    private lateinit var database: DatabaseReference
    var eventKey: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_list_view_admin,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentEvent = eventList[position]
        // Load and display the image using Picasso
        if (!currentEvent.eventImage.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentEvent.eventImage)
                .transition(DrawableTransitionOptions.withCrossFade()) // Optional transition animation
                .into(holder.eventImage)
        } else {
            // Handle the case where there is no image URL or display a placeholder
            // You can set a placeholder image or handle this case as needed.
            // Picasso provides methods for setting placeholders and error images.
            // Example: Picasso.get().load(R.drawable.placeholder_image).into(holder.eventImage)
        }
        //holder.eventImage.setImageResource(currentEvent.eventImage)
        holder.eventTitle.text = currentEvent.eventTitle
        holder.eventDate.text = currentEvent.eventDate
        var eventTitle = currentEvent.eventTitle

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
                                    eventsRef.child(eventKey.toString()).removeValue().addOnSuccessListener {
                                        Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(holder.itemView.context, "Unable to delete", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            }else {
                                // Handle the case where no event with the given title was found

                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            // Handle any database errors here
                        }
                    })


            }

        }



    }

    override fun getItemCount(): Int {
        return eventList.size
    }



    class MyViewHolder(itemView : View ) : RecyclerView.ViewHolder(itemView){

        val eventImage = itemView.findViewById<ShapeableImageView>(R.id.EventImg)
        val eventDate : TextView = itemView.findViewById(R.id.EventDate)
        val eventTitle : TextView = itemView.findViewById(R.id.EventTitle)
        val modifyEventBtn : ImageView = itemView.findViewById(R.id.modifyEventBtn)
        val deleteEventBtn : ImageView = itemView.findViewById(R.id.deleteEventBtn)
    }


}



