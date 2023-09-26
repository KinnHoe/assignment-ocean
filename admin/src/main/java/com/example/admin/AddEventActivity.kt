package com.example.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.admin.data.EventModel
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.admin.util.FirebaseUtil
import java.util.Random
import java.text.SimpleDateFormat
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.storage.FirebaseStorage


class AddEventActivity : AppCompatActivity() {
    private var selectedDate: Date? = null
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var imageURL: String? = null
    var url: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        val saveBtn = findViewById<Button>(R.id.saveButton)
        val datePickerButton = findViewById<Button>(R.id.datePickerButton)
        val timePickerButton = findViewById<Button>(R.id.timePickerButton)
        val uploadEventImage = findViewById<ImageView>(R.id.uploadEventImage)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                url = data!!.data
                uploadEventImage.setImageURI(url)
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }


        uploadEventImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }


        datePickerButton.setOnClickListener{
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
                    datePickerButton.text = selectedDateText
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        timePickerButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minuteOfHour ->
                    selectedHour = hourOfDay
                    selectedMinute = minuteOfHour
                    timePickerButton.text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hourOfDay,
                        minuteOfHour
                    )
                },
                hour, minute, true
            )
            timePickerDialog.show()
        }



        saveBtn.setOnClickListener{
            saveEventDataAndImage()
        }

        datePickerButton.text = "-"


    }
    private fun generateReferenceNo(): String {
        val randomNumber = (Random().nextDouble() * (999999999999L)).toLong()
        return "E${String.format("%3d", randomNumber)}"
    }

    private fun saveEventDataAndImage(){
        val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
            .child(url!!.lastPathSegment!!)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()
        storageReference.putFile(url!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageURL = urlImage.toString()
            saveEventData()
            dialog.dismiss()

        }.addOnFailureListener {
            dialog.dismiss()
        }


    }
    private fun saveEventData(){

        val eventTitle = findViewById<TextView>(R.id.uploadEventTitle).text.toString()
        val eventDecs = findViewById<TextView>(R.id.uploadEventDesc).text.toString()

        // Check if any of the required fields is empty
        if (eventTitle.isBlank() || eventDecs.isBlank() || selectedDate == null ) {
            Toast.makeText(this,"Please fill in all required fields.", Toast.LENGTH_SHORT).show()

            return
        }

        val eventRef = FirebaseUtil().getEventsReference()
        val eventKey = eventRef.push().key
        val referenceNo = generateReferenceNo()

        val calendar = Calendar.getInstance()

        // Set the calendar to the user's selected date and time
        calendar.time = selectedDate!!
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour!!)
        calendar.set(Calendar.MINUTE, selectedMinute!!)


        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)



        val event = EventModel(
            firebaseId = eventKey!!,
            referenceNo = referenceNo,
            eventTitle = eventTitle,
            eventDecs = eventDecs,
            eventDate = formattedDate,
            eventTime = formattedTime,
            eventImage = imageURL.toString(),
        )

        val eventMap = HashMap<String, Any>()
        eventMap["referenceNo"] = referenceNo
        eventMap["eventTitle"] = eventTitle
        eventMap["eventDate"] = formattedDate
        eventMap["eventDecs"] = eventDecs
        eventMap["eventTime"] = formattedTime
        eventMap["eventImage"] = imageURL.toString()

        eventRef.child(eventKey!!).setValue(eventMap).addOnSuccessListener {
            // Data was successfully written to Firebase
            Toast.makeText(this,"Event submitted successfully", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e ->
                // An error occurred while writing data
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", "Error writing to Firebase: ${e.message}", e)
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The operation completed successfully
                } else {
                    // The operation failed, and you can check task.exception for more details
                    Log.e(
                        "FirebaseError",
                        "Error writing to Firebase: ${task.exception?.message}",
                        task.exception
                    )
                }
            }

    }



}


