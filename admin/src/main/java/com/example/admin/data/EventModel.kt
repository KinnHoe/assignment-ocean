package com.example.admin.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Event")
data class EventModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "firebaseId")
    var firebaseId: String = "",

    @ColumnInfo(name = "reference_no")
    var referenceNo: String = "",

    @ColumnInfo(name = "eventTitle")
    var eventTitle: String = "",

    @ColumnInfo(name = "eventDecs")
    var eventDecs: String = "",

    @ColumnInfo(name = "eventDate")
    var eventDate: String = "",

    @ColumnInfo(name = "eventTime")
    var eventTime: String = "",

    @ColumnInfo(name = "eventImage")
    var eventImage: String = "",





    )
