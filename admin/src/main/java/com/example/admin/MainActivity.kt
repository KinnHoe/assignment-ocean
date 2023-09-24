package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uplaodBtn = findViewById<Button>(R.id.mainUpload)

        uplaodBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, EventActivity::class.java)
            startActivity(intent)
        }

    }


}