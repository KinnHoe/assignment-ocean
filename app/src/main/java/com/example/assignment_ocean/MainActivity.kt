package com.example.assignment_ocean

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        replaceFragment(Home())
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home -> replaceFragment(Home())
                R.id.event -> replaceFragment(Event())
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
                    Toast.makeText(this,"link to setting file using intent in MainActivity.kt",Toast.LENGTH_SHORT).show()
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
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}