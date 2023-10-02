package com.example.assignment_ocean.data

import com.google.firebase.database.ServerValue

data class Post(
   /* val username: String = "",
    val image: String = "",*/
    var id: String? = null,
    var caption: String = "",
    var photo: String? = null, // Assuming `photo` is a URL string
    val timestamp: Any = ServerValue.TIMESTAMP
)
