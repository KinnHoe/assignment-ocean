package com.example.admin

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.adapter.ImageAdapter
import com.example.admin.data.ImageClass
import com.example.admin.databinding.ActivityImageRetrieveBinding
import com.google.firebase.database.*

class ImageRetrieveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageRetrieveBinding
    private lateinit var databaseReference: DatabaseReference
    private var imageList = ArrayList<ImageClass>()

    private lateinit var imageAdapter: ImageAdapter
    private var eventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageRetrieveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up a GridLayoutManager with the desired number of columns
        val gridLayoutManager = GridLayoutManager(this@ImageRetrieveActivity, 2)
        binding.recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@ImageRetrieveActivity)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        imageList = ArrayList()
        imageAdapter = ImageAdapter(this@ImageRetrieveActivity, imageList)
        binding.recyclerView.adapter = imageAdapter
        databaseReference = FirebaseDatabase.getInstance().getReference("Image")
        dialog.show()

        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imageList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(ImageClass::class.java)
                    if (dataClass != null) {
                        imageList.add(dataClass)
                    }
                }
                imageAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        eventListener?.let {
            databaseReference.removeEventListener(it)
        }
    }
}
