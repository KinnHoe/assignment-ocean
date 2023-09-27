package com.example.assignment_ocean

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.assignment_ocean.models.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MomentRepository {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")

    fun getMomentData(): LiveData<List<Post>> {
        val liveData = MutableLiveData<List<Post>>()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val momentList = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { momentList.add(it) }
                }
                liveData.value = momentList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error, e.g., log or show a message
            }
        })

        return liveData
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val postUpdateData = mapOf(
            "caption" to post.caption,
            "photo" to post.photo
        )

        databaseReference.child(post.id).updateChildren(postUpdateData)
            .addOnSuccessListener {
                onSuccess.invoke() // Success callback
            }
            .addOnFailureListener { e ->
                val errorMessage = "Update failed: ${e.message ?: "Unknown error"}"
                onError.invoke(errorMessage) // Error callback with message
            }
    }

    fun deletePost(post: Post, onSuccess: () -> Unit, onError: (String) -> Unit) {
        databaseReference.child(post.id).removeValue()
            .addOnSuccessListener {
                onSuccess.invoke() // Success callback
            }
            .addOnFailureListener { e ->
                val errorMessage = "Deletion failed: ${e.message ?: "Unknown error"}"
                onError.invoke(errorMessage) // Error callback with message
            }
    }

}
