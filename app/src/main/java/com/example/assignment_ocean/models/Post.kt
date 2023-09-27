package com.example.assignment_ocean.models

data class Post(
    var id: String? = null,
    var caption: String = "",
    var photo: String? = null,
    val timestamp: Map<String, String> = mapOf("timestamp" to "true")
)
