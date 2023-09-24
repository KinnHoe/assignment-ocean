package com.example.admin.util
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
class FirebaseUtil {
    private val firebase: FirebaseDatabase

    init {
        firebase = getFirebaseInstance()
    }

    companion object {
        private var firebase: FirebaseDatabase? = null
        private const val EVENTS = "events"

        @Synchronized
        private fun getFirebaseInstance(): FirebaseDatabase {
            if (firebase == null) {
                firebase = FirebaseDatabase.getInstance()
            }
            return firebase!!
        }
    }

    fun getEventsReference(): DatabaseReference {
        return getDatabaseReference(EVENTS)
    }

    private fun getDatabaseReference(path: String): DatabaseReference {
        return firebase.reference.child(path)
       }

}