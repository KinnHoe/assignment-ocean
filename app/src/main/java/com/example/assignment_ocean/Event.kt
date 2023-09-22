package com.example.assignment_ocean

import android.os.Bundle
import android.provider.CalendarContract.Events
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Event : Fragment() {


    private lateinit var adapter : EventAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList : ArrayList<EventModel>

    lateinit var imageId : Array<Int>
    lateinit var title : Array<String>
    lateinit var date : Array<String>
    lateinit var events : Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        dataInitialize()
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView = view.findViewById(R.id.recycleView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventAdapter(eventArrayList)
        recyclerView.adapter = adapter

    }


    private fun dataInitialize(){
        eventArrayList = arrayListOf<EventModel>()

        imageId = arrayOf(
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img
        )

        title = arrayOf(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",

            )
        date = arrayOf(
            "12/12/2022",
            "12/12/2022",
            "12/12/2022",
            "12/12/2022",
            "12/12/2022",
            "12/12/2022",
            "12/12/2022",
            "12/12/2022",
            "12/12/2022"

        )

        for(i in imageId.indices){
            val events = EventModel(imageId[i],date[i],title[i],"")
            eventArrayList.add(events)
        }
    }



}