package com.dinusha.mindmate_sl.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


object FirebaseRepository {

    private const val TAG =
        "FirebaseRepository"

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    // =============================================
    // STRESS HISTORY
    // =============================================

    fun saveStressHistory(
        stressScore: Int,
        stressLevel: String,
        emotion: String,
        intent: String
    ) {

        val user =
            auth.currentUser


        // Firebase user should normally already exist
        // because MainActivity performs anonymous auth.
        if (user == null) {

            Log.e(
                TAG,
                "Stress history not saved: Firebase user is not authenticated"
            )

            return
        }


        // IMPORTANT:
        // Do not save the user's raw chat message here.
        //
        // Only pseudonymous model/routing metadata
        // required for Journey/research history is stored.
        val stressData =
            hashMapOf<String, Any>(

                "stressScore" to
                        stressScore,

                "stressLevel" to
                        stressLevel,

                "emotion" to
                        emotion,

                "intent" to
                        intent,

                "createdAt" to
                        FieldValue.serverTimestamp()
            )


        firestore
            .collection("users")
            .document(user.uid)
            .collection("stress_history")
            .add(stressData)

            .addOnSuccessListener { document ->

                Log.d(
                    TAG,
                    "Stress history saved: ${document.id}"
                )
            }

            .addOnFailureListener { error ->

                Log.e(
                    TAG,
                    "Stress history save failed",
                    error
                )
            }
    }

    // =============================================
// MOOD CHECK-INS
// =============================================

    fun saveMoodCheckIn(
        mood: String,
        source: String,
        stressScore: Int? = null
    ) {

        val user =
            auth.currentUser


        if (user == null) {

            Log.e(
                TAG,
                "Mood check-in not saved: Firebase user is not authenticated"
            )

            return
        }


        val moodData =
            hashMapOf<String, Any>(

                "mood" to mood,

                "source" to source,

                "createdAt" to
                        FieldValue.serverTimestamp()
            )


        // Optional current stress routing score
        if (stressScore != null) {

            moodData["stressScore"] =
                stressScore
        }


        firestore
            .collection("users")
            .document(user.uid)
            .collection("mood_checkins")
            .add(moodData)

            .addOnSuccessListener { document ->

                Log.d(
                    TAG,
                    "Mood check-in saved: ${document.id}"
                )
            }

            .addOnFailureListener { error ->

                Log.e(
                    TAG,
                    "Mood check-in save failed",
                    error
                )
            }
    }
}