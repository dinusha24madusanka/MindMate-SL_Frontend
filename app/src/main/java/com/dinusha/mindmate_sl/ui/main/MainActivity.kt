package com.dinusha.mindmate_sl.ui.main

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.ui.activities.ActivitiesFragment
import com.dinusha.mindmate_sl.ui.chat.ChatFragment
import com.dinusha.mindmate_sl.ui.community.CommunityFragment
import com.dinusha.mindmate_sl.ui.journey.JourneyFragment
import com.dinusha.mindmate_sl.ui.profile.ProfileFragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        // =========================================
        // FIREBASE AUTHENTICATION
        // =========================================

        firebaseAuth = FirebaseAuth.getInstance()

        initializeFirebaseUser()


        // =========================================
        // BOTTOM NAVIGATION
        // =========================================

        bottomNavigation =
            findViewById(R.id.bottomNavigation)


        if (savedInstanceState == null) {

            loadFragment(
                ChatFragment()
            )

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

                loadFragment(
                    selectedFragment
                )

                true

            } else {

                false
            }
        }
    }


    // =============================================
    // FIREBASE ANONYMOUS AUTH
    // =============================================

    private fun initializeFirebaseUser() {

        val currentUser =
            firebaseAuth.currentUser


        // Existing anonymous user
        if (currentUser != null) {

            Log.d(
                "FirebaseAuth",
                "Existing Firebase user authenticated"
            )

            return
        }


        // First-time anonymous sign-in
        firebaseAuth
            .signInAnonymously()
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    Log.d(
                        "FirebaseAuth",
                        "Anonymous Firebase sign-in successful"
                    )

                } else {

                    Log.e(
                        "FirebaseAuth",
                        "Anonymous Firebase sign-in failed",
                        task.exception
                    )
                }
            }
    }


    // =============================================
    // FRAGMENT NAVIGATION
    // =============================================

    private fun loadFragment(
        fragment: Fragment
    ) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()
    }
}