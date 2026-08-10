package com.dinusha.mindmate_sl.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.ui.activities.ActivitiesFragment
import com.dinusha.mindmate_sl.ui.chat.ChatFragment
import com.dinusha.mindmate_sl.ui.community.CommunityFragment
import com.dinusha.mindmate_sl.ui.journey.JourneyFragment
import com.dinusha.mindmate_sl.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigation =
            findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {

            loadFragment(ChatFragment())

            bottomNavigation.selectedItemId =
                R.id.nav_chat
        }

        bottomNavigation.setOnItemSelectedListener { item ->

            val selectedFragment: Fragment? =
                when (item.itemId) {

                    R.id.nav_chat ->
                        ChatFragment()

                    R.id.nav_journey ->
                        JourneyFragment()

                    R.id.nav_activities ->
                        ActivitiesFragment()

                    R.id.nav_community ->
                        CommunityFragment()

                    R.id.nav_profile ->
                        ProfileFragment()

                    else ->
                        null
                }

            if (selectedFragment != null) {

                loadFragment(selectedFragment)

                true

            } else {

                false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()
    }
}