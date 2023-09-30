package com.example.assignment_ocean

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.assignment_ocean.models.Post

class MomentViewModel : ViewModel() {
    private val momentRepository = MomentRepository()

    fun getMomentData(): LiveData<List<Post>> {
        return momentRepository.getMomentData()
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onError: (String) -> Unit) {
        momentRepository.updatePost(post, onSuccess, onError)
    }

    fun deletePost(post: Post, onSuccess: () -> Unit, onError: (String) -> Unit) {
        momentRepository.deletePost(post, onSuccess, onError)
    }
}




