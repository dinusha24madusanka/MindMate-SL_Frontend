package com.dinusha.mindmate_sl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // ඇප් එක ඔන් වෙද්දීම මුලින්ම ChatFragment එක ලෝඩ් කරනවා
        if (savedInstanceState == null) {
            loadFragment(ChatFragment())
        }

        // ටැබ් ක්ලික් කරද්දී වෙනස් වෙන්න ඕන ලොජික් එක
        bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_chat -> selectedFragment = ChatFragment()
                R.id.nav_journey -> selectedFragment = ChatFragment() // දැනට Chat එකම දමු, පස්සේ Fragments හදලා මාරු කරමු
                R.id.nav_activities -> selectedFragment = ChatFragment()
                R.id.nav_community -> selectedFragment = ChatFragment()
                R.id.nav_profile -> selectedFragment = ChatFragment()
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }
    }

    // Fragment එක Frame එක ඇතුළට දාන පොදු Function එක
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}