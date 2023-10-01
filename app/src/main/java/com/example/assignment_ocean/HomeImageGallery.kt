package com.example.assignment_ocean

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignment_ocean.adapter.ImagesGalleryAdapter
import com.example.assignment_ocean.data.ImageClass
import com.example.assignment_ocean.databinding.ActivityHomeImageGalleryBinding
import com.google.firebase.database.*
import com.google.firebase.database.R
import java.util.Locale

class HomeImageGallery : AppCompatActivity() {
    private lateinit var binding: ActivityHomeImageGalleryBinding
    private lateinit var databaseReference: DatabaseReference
    private var imagesGalleryAdapterList = ArrayList<ImageClass>()

    private lateinit var imageAdapter: ImagesGalleryAdapter
    private var eventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeImageGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up a GridLayoutManager with the desired number of columns
        val gridLayoutManager = GridLayoutManager(this@HomeImageGallery, 2)
        binding.recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@HomeImageGallery)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        imagesGalleryAdapterList = ArrayList()
        imageAdapter = ImagesGalleryAdapter(this@HomeImageGallery, imagesGalleryAdapterList)
        binding.recyclerView.adapter = imageAdapter
        databaseReference = FirebaseDatabase.getInstance().getReference("Image")
        dialog.show()

        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imagesGalleryAdapterList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(ImageClass::class.java)
                    if (dataClass != null) {
                        imagesGalleryAdapterList.add(dataClass)
                    }
                }
                imageAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })
        //search
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        eventListener?.let {
            databaseReference.removeEventListener(it)
        }
    }

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<ImageClass>()
        for (imageClass in imagesGalleryAdapterList) {
            if (imageClass.dataImageName?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(imageClass)
            }
        }
        imageAdapter.searchDataList(searchList)
    }
}
