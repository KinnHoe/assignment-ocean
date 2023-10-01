package com.example.assignment_ocean

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.assignment_ocean.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {
    var imageUrl = ""
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle != null) {
            binding.detailDesc.text = bundle.getString("Description")
            binding.detailTitle.text = bundle.getString("Title")
            binding.detailPriority.text = bundle.getString("Priority")
            binding.detailSize.text = bundle.getString("Size")
            binding.detailRange.text = bundle.getString("Range")
            imageUrl = bundle.getString("Image")!!
            Glide.with(this@DetailActivity).load(bundle.getString("Image")).into(binding.detailImage)

        }

    }
}