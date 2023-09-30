package com.example.admin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.admin.data.EventModel
import com.example.admin.database.EventAppDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel(app:Application):AndroidViewModel(app) {
    var allEvents : MutableLiveData<List<EventModel>>

    init{
        allEvents = MutableLiveData()
    }

    fun getAllEventsObserver(): MutableLiveData<List<EventModel>>{
        return allEvents
    }

    fun getAllEvents(){
        val eventDao = EventAppDb.getAppDatabase((getApplication()))?.eventDao()
        val list = eventDao?.getAllEventInfo()

        allEvents.postValue(list)
    }


    fun insertEvent(entity: EventModel){
        val eventDao = EventAppDb.getAppDatabase((getApplication()))?.eventDao()
        eventDao?.insertEvent(entity)
        getAllEvents()
    }

    fun updateEvent(entity: EventModel){
        val eventDao = EventAppDb.getAppDatabase((getApplication()))?.eventDao()
        eventDao?.insertEvent(entity)
        getAllEvents()
    }


}