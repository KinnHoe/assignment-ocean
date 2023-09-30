package com.example.admin.database

import android.content.Context
import android.provider.CalendarContract.EventsEntity
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.admin.dao.EventDao
import com.example.admin.data.EventModel

@Database(entities = [EventModel::class], version = 1)
abstract class EventAppDb : RoomDatabase() {

    abstract fun eventDao(): EventDao?

    companion object{
        private var INSTANCE: EventAppDb? = null

        fun getAppDatabase(context: Context): EventAppDb?{
            if(INSTANCE == null ){
                INSTANCE = Room.databaseBuilder<EventAppDb>(
                    context.applicationContext,EventAppDb::class.java,"EventDB"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }
}