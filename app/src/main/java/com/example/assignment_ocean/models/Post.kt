package com.example.assignment_ocean.models

import com.google.firebase.database.ServerValue

class Post(
    var caption: String,
    var photo: String?,
    val timestamp: Any = ServerValue.TIMESTAMP
) {
    constructor() : this("", null)
}

