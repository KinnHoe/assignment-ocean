    package com.example.assignment_ocean.adapter

    import android.content.Intent
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.fragment.app.FragmentActivity
    import androidx.recyclerview.widget.RecyclerView
    import com.example.assignment_ocean.data.EventModel
    import com.example.assignment_ocean.R
    import com.google.android.material.imageview.ShapeableImageView
    import com.bumptech.glide.Glide
    import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
    import com.example.assignment_ocean.EventDetail
    import com.example.assignment_ocean.data.UserJoinEventModel
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener


    class EventAdapter(private var eventList: ArrayList<EventModel>, private val username: String) : RecyclerView.Adapter<EventAdapter.MyViewHolder>(){

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val eventsRef: DatabaseReference = database.getReference("events")
        val joinEventRef: DatabaseReference = database.getReference("JoinEvent")
        var retreiveEventTitle : String = ""
        var retreiveUserName : String = username
        private var currentHolder: MyViewHolder? = null


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_list_view,parent,false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentEvent = eventList[position]
            currentHolder = holder
            // Load and display the image using Picasso
            if (!currentEvent.eventImage.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(currentEvent.eventImage)
                    .transition(DrawableTransitionOptions.withCrossFade()) // Optional transition animation
                    .into(holder.eventImage)
            }
            //holder.eventImage.setImageResource(currentEvent.eventImage)
            holder.eventTitle.text = currentEvent.eventTitle
            holder.eventDate.text = currentEvent.eventDate
            retreiveEventTitle = currentEvent.eventTitle

            checkJoint(currentEvent.eventTitle)






            holder.interestedEvent.setOnClickListener {
                Toast.makeText(holder.itemView.context, username,Toast.LENGTH_SHORT).show()
                val context = holder.itemView.context
                val intent = Intent(context, EventDetail::class.java)
                intent.putExtra("eventTitle", currentEvent.eventTitle)
                intent.putExtra("username", username)
                intent.putExtra("eventImage",holder.eventImage.toString())
                context.startActivity(intent)
            }


        }

        override fun getItemCount(): Int {
            return eventList.size
        }

        fun searchDataList(searchList: List<EventModel>) {
            eventList = ArrayList(searchList)
            notifyDataSetChanged()
        }

        fun checkJoint(eventTitle: String) {
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

                    // Update the button text based on whether the user has joined the event or not
                    if (isAlreadyJoined) {
                        currentHolder?.interestedEvent?.text = "Joined Event"
                    } else {
                        currentHolder?.interestedEvent?.text = "Join Event"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }




        class MyViewHolder(itemView : View ) : RecyclerView.ViewHolder(itemView){

            val eventImage = itemView.findViewById<ShapeableImageView>(R.id.EventImg)
            val eventDate : TextView = itemView.findViewById(R.id.EventDate)
            val eventTitle : TextView = itemView.findViewById(R.id.EventTitle)
            val interestedEvent : Button = itemView.findViewById(R.id.interestedEventBtn)
        }

    }