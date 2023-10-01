package com.example.assignment_ocean

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.assignment_ocean.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    lateinit var topNavBar : MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")
        replaceFragment(Home())
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home -> replaceFragment(Home())
                R.id.event -> {
                    val username1 = intent.getStringExtra("username")
                    val eventFragment = Event()
                    eventFragment.username = username1 // Set the username property in the Event fragment
                    replaceFragment(eventFragment)
                }
                R.id.addPost -> replaceFragment(AddPost())
                R.id.moment -> replaceFragment(Moment())
                R.id.oceanLife -> replaceFragment(CatogoriesBoat())

                else ->{

                }

            }
            true
        }

        topNavBar = findViewById(R.id.topnavbar)

        topNavBar.setNavigationOnClickListener{

        }

        topNavBar.setOnMenuItemClickListener{ menuItem ->

            when(menuItem.itemId){
                R.id.nav_person -> {



                    val profileIntent = Intent(this@MainActivity, ProfileActivity::class.java)
                    profileIntent.putExtra("username", username)
                    startActivity(profileIntent)

                    true
                }
                else -> {
                    false
            }
        }


        }
    }



    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}