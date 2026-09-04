package com.dinusha.mindmate_sl.data.model.firebase

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import android.util.Log


class FirebaseSyncRepository(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun ensureAuthenticated(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            onSuccess(currentUser.uid)
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid
                    if (uid != null) {
                        onSuccess(uid)
                    } else {
                        onFailure(Exception("Firebase user is null after anonymous sign-in"))
                    }
                }
                .addOnFailureListener {
                    Log.e("FirebaseSync", "Anonymous sign-in failed", it)
                    onFailure(it)
                }
        }
    }

    private fun getUserRef(uid: String) = database.reference
        .child("users")
        .child(uid)

    // MindPoints
    fun saveMindPoints(points: Int) {
        ensureAuthenticated(onSuccess = { uid ->
            getUserRef(uid).child("mindpoints")
                .setValue(points)
        })
    }

    // Stress History
    fun saveStressRecord(
        stressScore: Int,
        stressLevel: String,
        intent: String,
        emotion: String
    ) {
        ensureAuthenticated(onSuccess = { uid ->
            val recordRef = getUserRef(uid)
                .child("stress_history")
                .push()
            val data = mapOf(
                "stressScore" to stressScore,
                "stressLevel" to stressLevel,
                "intent" to intent,
                "emotion" to emotion,
                "timestamp" to System.currentTimeMillis()
            )
            recordRef.setValue(data)
        })
    }

    // Mood / Feeling Check
    fun saveMoodRecord(feeling: String) {
        ensureAuthenticated(onSuccess = { uid ->
            val recordRef = getUserRef(uid)
                .child("mood_checkins")
                .push()
            val data = mapOf(
                "feeling" to feeling,
                "timestamp" to System.currentTimeMillis()
            )
            recordRef.setValue(data)
        })
    }

    // Last NLP State
    fun saveLastAnalysis(
        stressScore: Int,
        stressLevel: String,
        intent: String,
        emotion: String
    ) {
        ensureAuthenticated(onSuccess = { uid ->
            val data = mapOf(
                "stressScore" to stressScore,
                "stressLevel" to stressLevel,
                "intent" to intent,
                "emotion" to emotion,
                "timestamp" to System.currentTimeMillis()
            )
            getUserRef(uid).child("last_analysis")
                .setValue(data)
        })
    }

    fun saveNlpResult(
        intent: String?,
        intentConfidence: Double?,
        emotion: String?,
        emotionConfidence: Double?,
        stressScore: Double?,
        stressLevel: String?,
        riskLevel: String?,
        recommendedActivity: String?,
        allowGamification: Boolean
    ) {
        ensureAuthenticated(onSuccess = { uid ->
            val resultRef = getUserRef(uid)
                .child("nlp_results")
                .push()

            val data = mutableMapOf<String, Any>(
                "allowGamification" to allowGamification,
                "timestamp" to ServerValue.TIMESTAMP
            )

            intent?.let { data["intent"] = it }
            intentConfidence?.let { data["intentConfidence"] = it }
            emotion?.let { data["emotion"] = it }
            emotionConfidence?.let { data["emotionConfidence"] = it }
            stressScore?.let { data["stressScore"] = it }
            stressLevel?.let { data["stressLevel"] = it }
            riskLevel?.let { data["riskLevel"] = it }
            recommendedActivity?.let { data["recommendedActivity"] = it }

            resultRef.setValue(data)

            getUserRef(uid).child("last_analysis")
                .setValue(data)
        })
    }

    fun saveNlpResult(
        intent: String?,
        emotion: String?,
        stressScore: Int,
        stressLevel: String?,
        riskLevel: String,
        recommendedActivity: String,
        allowGamification: Boolean
    ) {
        ensureAuthenticated(onSuccess = { uid ->
            val resultRef = getUserRef(uid)
                .child("nlp_results")
                .push()

            val data = mapOf(
                "intent" to (intent ?: "UNKNOWN"),
                "emotion" to (emotion ?: "UNKNOWN"),
                "stressScore" to stressScore,
                "stressLevel" to (stressLevel ?: "UNKNOWN"),
                "riskLevel" to riskLevel,
                "recommendedActivity" to recommendedActivity,
                "allowGamification" to allowGamification,
                "timestamp" to System.currentTimeMillis()
            )

            resultRef.setValue(data)

            getUserRef(uid)
                .child("last_analysis")
                .setValue(data)
        })
    }

    fun saveJourneyDay(
        date: String,
        stressSum: Int,
        observationCount: Int,
        dailyAverage: Int
    ) {
        ensureAuthenticated(onSuccess = { uid ->
            val data = mapOf(
                "date" to date,
                "stressSum" to stressSum,
                "observationCount" to observationCount,
                "dailyAverage" to dailyAverage,
                "updatedAt" to System.currentTimeMillis()
            )

            getUserRef(uid)
                .child("journey")
                .child(date)
                .setValue(data)
                .addOnSuccessListener {
                    Log.d("FirebaseSync", "Journey saved: $date")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseSync", "Journey save failed", e)
                }
        })
    }

    fun loadMindPoints(
        onSuccess: (Int) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        ensureAuthenticated(onSuccess = { uid ->
            getUserRef(uid)
                .child("mindpoints")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val points = snapshot.getValue(Int::class.java)
                                ?: snapshot.getValue(Long::class.java)?.toInt()
                                ?: 0

                        prefs.edit()
                            .putInt("MIND_POINTS", points)
                            .apply()

                        onSuccess(points)
                    } else {
                        val localPoints = prefs.getInt("MIND_POINTS", 0)
                        if (localPoints > 0) {
                            saveMindPoints(localPoints)
                        }
                        onSuccess(localPoints)
                    }
                }
                .addOnFailureListener { error ->
                    Log.e("FirebaseSync", "MindPoints load failed", error)
                    val localPoints = prefs.getInt("MIND_POINTS", 0)
                    onSuccess(localPoints)
                    onFailure(error)
                }
        }, onFailure = onFailure)
    }

    fun loadJourney(
        onComplete: () -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        ensureAuthenticated(onSuccess = { uid ->
            getUserRef(uid)
                .child("journey")
                .get()
                .addOnSuccessListener { snapshot ->
                    val journeyPrefs = appContext.getSharedPreferences(
                        "mindmate_journey",
                        Context.MODE_PRIVATE
                    )
                    val editor = journeyPrefs.edit()

                    for (daySnapshot in snapshot.children) {
                        val date = daySnapshot.key ?: continue
                        val stressSum = daySnapshot.child("stressSum").getValue(Int::class.java)
                                ?: daySnapshot.child("stressSum").getValue(Long::class.java)?.toInt()
                                ?: continue
                        val observationCount = daySnapshot.child("observationCount").getValue(Int::class.java)
                                ?: daySnapshot.child("observationCount").getValue(Long::class.java)?.toInt()
                                ?: continue
                        val dailyAverage = daySnapshot.child("dailyAverage").getValue(Int::class.java)
                                ?: daySnapshot.child("dailyAverage").getValue(Long::class.java)?.toInt()
                                ?: continue

                        editor.putInt("${date}_ai_stress_sum", stressSum)
                        editor.putInt("${date}_ai_stress_count", observationCount)
                        editor.putInt("${date}_stress", dailyAverage)
                    }
                    editor.apply()
                    Log.d("FirebaseSync", "Journey restored from Firebase")
                    onComplete()
                }
                .addOnFailureListener { error ->
                    Log.e("FirebaseSync", "Journey load failed - using local cache", error)
                    onComplete()
                    onFailure(error)
                }
        }, onFailure = onFailure)
    }
}