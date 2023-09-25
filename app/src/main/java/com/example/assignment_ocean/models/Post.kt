package com.example.assignment_ocean.models

import com.google.firebase.database.ServerValue

data class FeedPost(/*val uid: String = "", val username: String = "",*/
                    val image: String = "", val caption: String = "",
                    val timestamp: Any = ServerValue.TIMESTAMP, val photo: String? = null
)