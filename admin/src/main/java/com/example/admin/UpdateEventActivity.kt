package com.example.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.admin.dao.EventDao
import com.example.admin.data.EventModel
import com.example.admin.database.EventAppDb
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UpdateEventActivity : AppCompatActivity() {
    private var selectedDate: Date? = null
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var eventImageModified: String? = null
    private var eventDateModified: String? = null
    private var eventTimeModified: String? = null
    private var eventTitleModified: String? = null
    private var eventDecsModified: String? = null
    var url: Uri? = null
    var eventKey: String? = null
    var imagePath : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_event)
        val eventTitle = intent.getStringExtra("eventTitle")

        // Initialize Firebase database reference
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val eventsRef: DatabaseReference = database.getReference("events")
        val eventImage = findViewById<ImageView>(R.id.uploadEventImage)
        val eventTitleTextView = findViewById<EditText>(R.id.uploadEventTitle)
        val eventDateTextView = findViewById<Button>(R.id.datePickerButton)
        val eventDescriptionTextView = findViewById<EditText>(R.id.uploadEventDesc)
        val eventTime = findViewById<Button>(R.id.timePickerButton)
        val saveBtn = findViewById<Button>(R.id.saveButton)


        // Check if eventTitle is not null and fetch event data
        if (eventTitle != null) {
            eventsRef.orderByChild("eventTitle").equalTo(eventTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (eventSnapshot in snapshot.children) {
                                eventKey = eventSnapshot.key

                                val eventModel = eventSnapshot.getValue(EventModel::class.java)
                                if (eventModel != null) {
                                    // Populate your layout views with event data
                                    // For example:

                                    if (!eventModel.eventImage.isNullOrEmpty()) {
                                        Glide.with(applicationContext)
                                            .load(eventModel.eventImage)
                                            .into(eventImage)
                                    }

                                    eventTitleTextView.text = Editable.Factory.getInstance().newEditable(eventModel.eventTitle)
                                    eventDateTextView.text = eventModel.eventDate
                                    eventDescriptionTextView.text = Editable.Factory.getInstance().newEditable(eventModel.eventDecs)
                                    eventTime.text = eventModel.eventTime

                                    // Load and display the event image using Glide
                                    if (!eventModel.eventImage.isNullOrEmpty()) {
                                        Glide.with(applicationContext)
                                            .load(eventModel.eventImage)
                                            .into(eventImage)
                                    }
                                    eventImageModified = eventModel.eventImage
                                    eventDateModified = eventModel.eventDate
                                    eventTimeModified = eventModel.eventTime
                                    eventTitleModified = eventTitleTextView.text.toString()
                                    eventDecsModified = eventDescriptionTextView.text.toString()


                                }
                            }


                        } else {
                            // Handle the case where no event with the given title was found
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle any database errors here
                    }
                })
        }

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                url = data!!.data
                eventImage.setImageURI(url)
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
            eventImageModified = url.toString()
            Toast.makeText(this, eventImageModified, Toast.LENGTH_SHORT).show()
        }

        eventImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        eventDateTextView.setOnClickListener{
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    selectedDate = calendar.time

                    val selectedDateText = String.format(
                        Locale.getDefault(),
                        "%s %02d %d",
                        DateFormatSymbols().months[selectedMonth],
                        selectedDay,
                        selectedYear
                    )
                    eventDateTextView.text = selectedDateText
                    eventDateModified = eventDateTextView.text.toString()

                },
                year, month, day
            )
            datePickerDialog.show()
        }

        eventTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minuteOfHour ->
                    selectedHour = hourOfDay
                    selectedMinute = minuteOfHour
                    eventTime.text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hourOfDay,
                        minuteOfHour
                    )
                    eventTimeModified = eventTime.text.toString()
                },
                hour, minute, true
            )
            timePickerDialog.show()
        }

        saveBtn.setOnClickListener{
            Toast.makeText(this, eventImageModified.toString(),Toast.LENGTH_SHORT).show()
            updateData(eventDateModified.toString(),eventTimeModified.toString(),eventTitleTextView.text.toString(),eventDescriptionTextView.text.toString())

        }





    }


    fun getImageFilePath(uri: Uri): String{
        return saveImageToFile(uri)
    }

    private fun saveImageToFile(uri: Uri): String {
        val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
        val file = File(this.cacheDir, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        try {
            inputStream?.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024) // 4K buffer size
                    while (true) {
                        val bytesRead = input.read(buffer)
                        if (bytesRead == -1) break
                        output.write(buffer, 0, bytesRead)
                    }
                    output.flush()
                }
            }} catch (e: Exception) {
            Log.e("Error", "Error message", e)
        }
        Log.i("Testing", "saveImageFile1")
        return file.absolutePath
    }

    private fun updateData(eventDate: String, eventTime: String, eventTitle: String, eventDecs: String) {
        if (url != null) {
            storageRef = FirebaseStorage.getInstance().reference.child("Task Images")
                .child(url!!.lastPathSegment!!)
            imagePath = getImageFilePath(url!!)

            storageRef.putFile(url!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, now retrieve the download URL
                    storageRef.downloadUrl.addOnSuccessListener { uRL ->
                        eventImageModified = uRL.toString()

                        // Update the Firebase Realtime Database with the image URL
                        database = FirebaseDatabase.getInstance().getReference("events")
                        val user = mapOf(
                            "eventImage" to eventImageModified.toString(),
                            "eventDate" to eventDate,
                            "eventTime" to eventTime,
                            "eventTitle" to eventTitle,
                            "eventDecs" to eventDecs
                        )
                        var eventDao = EventAppDb.getAppDatabase(applicationContext)?.eventDao()
                        val existingEvent = eventDao?.getEventByTitle(eventTitle)
                        eventDao = EventAppDb.getAppDatabase(application)?.eventDao()
                        val updatedEvent = EventModel(
                            id = existingEvent!!.id,
                            eventTitle = eventTitle,
                            eventDate = eventDate,
                            eventTime = eventTime,
                            eventImage = imagePath!!,
                            eventDecs = eventDecs
                        )
                        eventDao?.updateEvent(updatedEvent)
                        database.child(eventKey!!).updateChildren(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT)
                                    .show()
                            }

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error if image upload fails
                    Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {

            // Update the Firebase Realtime Database with the image URL
            database = FirebaseDatabase.getInstance().getReference("events")
            val user = mapOf(
                "eventImage" to eventImageModified.toString(),
                "eventDate" to eventDate,
                "eventTime" to eventTime,
                "eventTitle" to eventTitle,
                "eventDecs" to eventDecs
            )
            database.child(eventKey!!).updateChildren(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }




}