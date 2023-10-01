package com.example.admin

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.admin.adapter.MyAdapter
import com.example.admin.data.DataClass
import com.example.admin.databinding.ActivityOceanMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import java.util.Locale

class OceanMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOceanMainBinding


    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var adapter: MyAdapter
    private lateinit var storageRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOceanMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //RV ->Start<-
        val gridLayoutManager = GridLayoutManager(this@OceanMainActivity, 1)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.search.clearFocus()

        val builder = AlertDialog.Builder(this@OceanMainActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()
        adapter = MyAdapter(this@OceanMainActivity, dataList)
        binding.recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories")
        dialog.show()

        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(DataClass::class.java)
                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })
        //RV ->END<-

        //create
        binding.insertCatBtn.setOnClickListener{
            val intent = Intent(this@OceanMainActivity, UploadActivity::class.java)
            startActivity(intent)


        }
        //update
        binding.toUpdateDetailsBtn.setOnClickListener{
            val intent = Intent(this@OceanMainActivity, UpdateActivity::class.java)
            startActivity(intent)

        }


        //delete
        binding.toDeleteBtn.setOnClickListener(View.OnClickListener{
            val intent = Intent(this@OceanMainActivity, DeleteActivity::class.java)
            startActivity(intent)
        })

        //insert image
        binding.toUploadImageBtn.setOnClickListener(View.OnClickListener{
            val intent = Intent(this@OceanMainActivity, ImagesActivity::class.java)
            startActivity(intent)
        })

        //show image
        binding.showImage.setOnClickListener(View.OnClickListener{
            val intent = Intent(this@OceanMainActivity, ImageRetrieveActivity::class.java)
            startActivity(intent)
        })

        //TO HOME CAT
        binding.toHomeCat.setOnClickListener(View.OnClickListener{
            val intent = Intent(this@OceanMainActivity, MainActivity::class.java)
            startActivity(intent)
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
    fun searchList(text: String) {
        val searchList = java.util.ArrayList<DataClass>()
        for (dataClass in dataList) {
            if (dataClass.dataTitle?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }
}