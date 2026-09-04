package com.dinusha.mindmate_sl.data.model.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
object FirebaseRepository {
    private const val TAG = "FirebaseRepository"
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // STRESS HISTORY
    fun saveStressHistory(
        stressScore: Int,
        stressLevel: String,
        emotion: String,
        intent: String
    ) {
        withAuthenticatedUser { user ->
            /*
             * Raw chat messages are intentionally not stored.
             * Only pseudonymous model/routing metadata
             * required for stress history is saved.
             */
            val stressData =
                hashMapOf<String, Any>(
                    "stressScore" to stressScore,
                    "stressLevel" to stressLevel,
                    "emotion" to emotion,
                    "intent" to intent,
                    "createdAt" to FieldValue.serverTimestamp()
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
    }

    private fun withAuthenticatedUser(
        onReady: (FirebaseUser) -> Unit
    ) {
        val currentUser = auth.currentUser
        // Already authenticated
        if (currentUser != null) {
            onReady(currentUser)
            return
        }
        // Authentication may still be starting in MainActivity.
        // Complete anonymous authentication here before saving.
        auth
            .signInAnonymously()
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    Log.d(TAG, "Anonymous Firebase user ready for save")
                    onReady(user)
                } else {
                    Log.e(TAG, "Anonymous sign-in succeeded but user is null")
                }
            }
            .addOnFailureListener { error ->
                Log.e(
                    TAG,
                    "Anonymous Firebase authentication failed",
                    error
                )
            }
    }

    // MOOD CHECK-INS
    fun saveMoodCheckIn(
        mood: String,
        source: String,
        stressScore: Int? = null
    ) {
        withAuthenticatedUser { user ->
            val moodData = hashMapOf<String, Any>(
                "mood" to mood,
                "source" to source,
                "createdAt" to FieldValue.serverTimestamp()
            )
            if (stressScore != null) {
                moodData["stressScore"] = stressScore
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
}
