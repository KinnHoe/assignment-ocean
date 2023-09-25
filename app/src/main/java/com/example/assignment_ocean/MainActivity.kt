package com.example.assignment_ocean

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.assignment_ocean.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private val imageSelectionViewModel by viewModels<ImageSelectionViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var topNavBar: MaterialToolbar
    private val PERMISSION_REQUEST_CODE = 123
    private val GALLERY_REQUEST_CODE = 456
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ViewModel and other UI components
        initUI()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check for and request storage permissions if needed
            if (!hasStoragePermissions()) {
                requestStoragePermissions()
            }
        }
    }

    private fun initUI() {
        // Initialize the bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.event -> replaceFragment(Event())
                R.id.addPost -> {
                    if (hasStoragePermissions()) {
                        openGalleryPicker()
                    } else {
                        requestStoragePermissions()
                    }
                }
                R.id.moment -> replaceFragment(Moment())
                R.id.oceanLife -> replaceFragment(CatogoriesBoat())
                else -> {
                }
            }
            true
        }

        // Initialize the "Next" button
        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (imageSelectionViewModel.selectedImages.isNotEmpty()) {
                val shareFragment = ShareFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, shareFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // Handle the case where no image is selected
            }
        }

        // Initialize the top navigation bar
        topNavBar = findViewById(R.id.topnavbar)
        topNavBar.setNavigationOnClickListener {
            // Handle navigation icon click
        }

        topNavBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_person -> {
                    // Handle the menu item click
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun requestStoragePermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker()
            } else {
                // Handle permission denial
            }
        }
    }

    private fun openGalleryPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        imageSelectionViewModel.selectedImages.add(uri)
                    }
                }
            } else if (data?.data != null) {
                val uri = data.data
                if (uri != null) {
                    imageSelectionViewModel.selectedImages.add(uri)
                }
            }

            nextButton.visibility = View.VISIBLE
        }
    }

    private fun hasStoragePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}
