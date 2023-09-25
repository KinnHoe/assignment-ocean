package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uplaodBtn = findViewById<Button>(R.id.mainUpload)
        val topNavEventAdmin = findViewById<ImageView>(R.id.top_nav_event)
        val topNavPersonAdmin = findViewById<ImageView>(R.id.top_nav_person)
        val topNavOceanlifeAdmin = findViewById<ImageView>(R.id.top_nav_oceanlife)

        topNavPersonAdmin.setOnClickListener {
            Toast.makeText(this,"Set Intent at admin main activity",Toast.LENGTH_SHORT).show()
        }

        topNavOceanlifeAdmin.setOnClickListener {
            Toast.makeText(this,"Set Intent at admin main activity",Toast.LENGTH_SHORT).show()
        }

        topNavEventAdmin.setOnClickListener {
            val intent = Intent(this@MainActivity, RetrieveEventActivity::class.java)
            startActivity(intent)
        }

        uplaodBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEventActivity::class.java)
            startActivity(intent)
        }

    }


}