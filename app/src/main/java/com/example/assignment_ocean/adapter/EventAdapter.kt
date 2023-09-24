package com.example.assignment_ocean.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_ocean.data.EventModel
import com.example.assignment_ocean.R
import com.google.android.material.imageview.ShapeableImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions



class EventAdapter(private val eventList: ArrayList<EventModel>) : RecyclerView.Adapter<EventAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_list_view,parent,false)
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

    }
    override fun getItemCount(): Int {
        return eventList.size
    }



    class MyViewHolder(itemView : View ) : RecyclerView.ViewHolder(itemView){

        val eventImage = itemView.findViewById<ShapeableImageView>(R.id.EventImg)
        val eventDate : TextView = itemView.findViewById(R.id.EventDate)
        val eventTitle : TextView = itemView.findViewById(R.id.EventTitle)
    }

}



