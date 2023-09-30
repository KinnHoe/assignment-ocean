package com.example.admin.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.admin.data.EventModel

@Dao
interface EventDao {

    @Query("SELECT * FROM Event")
    fun getAllEventInfo(): List<EventModel>?

    @Query("SELECT * FROM Event WHERE eventTitle = :title LIMIT 1")
    fun getEventByTitle(title: String): EventModel?
    @Insert
    fun insertEvent(event: EventModel?)

    @Update
    fun updateEvent(event: EventModel?)

    @Delete
    fun deleteEvent(event: EventModel?)
}