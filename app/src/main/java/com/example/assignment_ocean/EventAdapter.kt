package com.example.assignment_ocean

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.Shapeable


class EventAdapter(private val eventList: ArrayList<EventModel>) : RecyclerView.Adapter<EventAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_list_view,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentEvent = eventList[position]
        holder.eventImage.setImageResource(currentEvent.eventImage)
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



