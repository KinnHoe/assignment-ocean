package com.example.assignment_ocean.data

data class EventModel(

    var eventImage : String,
    var eventDate : String,
    var eventTitle: String,
    var eventDecs: String,
    var eventTime: String
){
    constructor() : this("", "", "", "", "")
}
