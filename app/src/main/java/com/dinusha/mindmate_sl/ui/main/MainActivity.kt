package com.dinusha.mindmate_sl.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.ui.activities.ActivitiesFragment
import com.dinusha.mindmate_sl.ui.chat.ChatFragment
import com.dinusha.mindmate_sl.ui.journey.JourneyFragment
import com.dinusha.mindmate_sl.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var firebaseAuth: FirebaseAuth
    companion object {
        private const val TAG_CHAT = "CHAT_FRAGMENT"
        private const val TAG_JOURNEY = "JOURNEY_FRAGMENT"
        private const val TAG_ACTIVITIES = "ACTIVITIES_FRAGMENT"
        private const val TAG_PROFILE = "PROFILE_FRAGMENT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FIREBASE AUTHENTICATION
        firebaseAuth = FirebaseAuth.getInstance()
        initializeFirebaseUser()

        // BOTTOM NAVIGATION
        bottomNavigation =findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_chat -> {
                        switchFragment(TAG_CHAT) {
                            ChatFragment()
                        }
                        true
                    }
                    R.id.nav_journey -> {
                        switchFragment(TAG_JOURNEY) {
                            JourneyFragment()
                        }
                        true
                    }
                    R.id.nav_activities -> {
                        switchFragment(TAG_ACTIVITIES) {
                            ActivitiesFragment()
                        }
                        true
                    }
                    R.id.nav_profile -> {
                        switchFragment(TAG_PROFILE) {
                            ProfileFragment()
                        }
                        true
                    }
                    else -> false
                }
            }

        // First app launch only
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_chat
        }

        // Test Firebase Realtime Database connection
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("connection_test")
        ref.setValue("MindMate-SL connected")
            .addOnSuccessListener {
                println("FIREBASE SUCCESS")
            }
            .addOnFailureListener { e ->
                println("FIREBASE ERROR: ${e.message}")
            }
    }

    // FIREBASE ANONYMOUS AUTH
    private fun initializeFirebaseUser() {
        val currentUser =firebaseAuth.currentUser
        // Existing anonymous user
        if (currentUser != null) {
            Log.d(
                "FirebaseAuth",
                "Existing Firebase user authenticated"
            )
            return
        }

        // First-time anonymous sign-in
        firebaseAuth.signInAnonymously().addOnCompleteListener(this) { task ->
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

    // FRAGMENT NAVIGATION
    // KEEP FRAGMENT INSTANCES ALIVE
    private fun switchFragment(
        tag: String,
        createFragment: () -> Fragment
    ) {
        val fragmentManager = supportFragmentManager
        var targetFragment = fragmentManager.findFragmentByTag(tag)
        val transaction = fragmentManager.beginTransaction()

        // Create fragment only once
        if (targetFragment == null) {
            targetFragment = createFragment()
            transaction.add(R.id.fragmentContainer,targetFragment,tag)
        }

        // Hide other fragments
        fragmentManager
            .fragments
            .forEach { fragment ->
                if (
                    fragment.isAdded &&
                    fragment != targetFragment
                ) {
                    transaction.hide(
                        fragment
                    )
                }
            }

        // Show selected fragment
        transaction.show(
            targetFragment
        )
        transaction
            .setPrimaryNavigationFragment(
                targetFragment
            )
        transaction.commit()
    }
}