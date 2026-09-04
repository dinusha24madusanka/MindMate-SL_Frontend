package com.dinusha.mindmate_sl.ui.journey

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dinusha.mindmate_sl.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.dinusha.mindmate_sl.data.model.firebase.FirebaseSyncRepository
import android.util.Log

class JourneyFragment : Fragment() {

    private lateinit var moodVeryBad: TextView
    private lateinit var moodBad: TextView
    private lateinit var moodOkay: TextView
    private lateinit var moodGood: TextView
    private lateinit var moodGreat: TextView

    private lateinit var selectedMoodText: TextView
    private lateinit var saveMoodButton: MaterialButton

    private lateinit var moodHistoryContainer: LinearLayout
    private lateinit var weeklyStressView: WeeklyStressView
    private lateinit var averageStressText: TextView
    private lateinit var weeklyAverageText: TextView
    private lateinit var highestStressText: TextView
    private lateinit var stressTrendText: TextView
    private lateinit var dataCoverageText: TextView
    private lateinit var aiInsightText: TextView
    private lateinit var firebaseSync: FirebaseSyncRepository

    private var selectedMood: String? = null
    private var selectedEmoji: String? = null
    private var selectedStressScore: Int = 0

    private val preferences by lazy {
        requireContext().getSharedPreferences(
            "mindmate_journey",
            0
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_journey,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        moodVeryBad =
            view.findViewById(R.id.moodVeryBad)

        moodBad =
            view.findViewById(R.id.moodBad)

        moodOkay =
            view.findViewById(R.id.moodOkay)

        moodGood =
            view.findViewById(R.id.moodGood)

        moodGreat =
            view.findViewById(R.id.moodGreat)

        selectedMoodText =
            view.findViewById(R.id.selectedMoodText)

        saveMoodButton =
            view.findViewById(R.id.saveMoodButton)

        moodHistoryContainer =
            view.findViewById(R.id.moodHistoryContainer)

        weeklyStressView =
            view.findViewById(R.id.weeklyStressView)

        weeklyAverageText =
            view.findViewById(R.id.weeklyAverageText)

        highestStressText =
            view.findViewById(R.id.highestStressText)

        stressTrendText =
            view.findViewById(R.id.stressTrendText)

        dataCoverageText =
            view.findViewById(R.id.dataCoverageText)

        aiInsightText =
            view.findViewById(R.id.aiInsightText)

        firebaseSync = FirebaseSyncRepository(requireContext())
        syncJourneyFromFirebase()

        setupMoodButtons()

        saveMoodButton.setOnClickListener {
            saveTodayMood()
        }

        refreshJourney()
    }

    private fun setupMoodButtons() {

        moodVeryBad.setOnClickListener {
            selectMood(
                "Very low",
                "😞",
                90,
                moodVeryBad
            )
        }

        moodBad.setOnClickListener {
            selectMood(
                "Low",
                "😕",
                70,
                moodBad
            )
        }

        moodOkay.setOnClickListener {
            selectMood(
                "Okay",
                "😐",
                50,
                moodOkay
            )
        }

        moodGood.setOnClickListener {
            selectMood(
                "Good",
                "🙂",
                30,
                moodGood
            )
        }

        moodGreat.setOnClickListener {
            selectMood(
                "Great",
                "😄",
                15,
                moodGreat
            )
        }
    }

    private fun selectMood(
        mood: String,
        emoji: String,
        stressScore: Int,
        selectedView: TextView
    ) {

        selectedMood = mood
        selectedEmoji = emoji
        selectedStressScore = stressScore

        resetMoodSelection()

        selectedView.setBackgroundColor(
            Color.parseColor("#D5F1EB")
        )

        selectedMoodText.text =
            "$emoji You're feeling $mood"
    }

    private fun resetMoodSelection() {

        val views = listOf(
            moodVeryBad,
            moodBad,
            moodOkay,
            moodGood,
            moodGreat
        )

        views.forEach {
            it.setBackgroundColor(
                Color.TRANSPARENT
            )
        }
    }

    private fun saveTodayMood() {

        if (selectedMood == null) {

            Toast.makeText(
                requireContext(),
                "Please select your mood first.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val dateKey =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

        preferences.edit()
            .putString(
                "${dateKey}_mood",
                selectedMood
            )
            .putString(
                "${dateKey}_emoji",
                selectedEmoji
            )
            .putInt(
                "${dateKey}_mood_stress",
                selectedStressScore
            )
            .apply()

        Toast.makeText(
            requireContext(),
            "Today's check-in saved.",
            Toast.LENGTH_SHORT
        ).show()

        refreshJourney()
    }

    private fun refreshJourney() {

        loadWeeklyStressData()

        loadMoodHistory()

        generateInsight()
    }

    private fun syncJourneyFromFirebase() {

        firebaseSync.loadJourney(
            onComplete = {
                if (!isAdded) return@loadJourney

                Log.d("JourneyFragment", "Journey synced from Firebase")

                refreshJourney()
            },
            onFailure = { e ->
                Log.e("JourneyFragment", "Journey sync failed", e)
            }
        )
    }

    private fun loadWeeklyStressData() {

        val values =
            MutableList<Float?>(7) {
                null
            }


        val calendar =
            Calendar.getInstance()


        // Current week starts on Monday
        calendar.set(
            Calendar.DAY_OF_WEEK,
            Calendar.MONDAY
        )


        for (i in 0 until 7) {

            val dateKey =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(
                    calendar.time
                )


            // Only real backend NLP stress data
            if (
                preferences.contains(
                    "${dateKey}_stress"
                )
            ) {

                val score =
                    preferences.getInt(
                        "${dateKey}_stress",
                        0
                    )


                // Safety-bypassed / invalid values
                // should not appear as real stress data.
                if (score in 1..100) {

                    values[i] =
                        score.toFloat()

                } else {

                    values[i] =
                        null
                }

            } else {

                values[i] =
                    null
            }


            calendar.add(
                Calendar.DAY_OF_MONTH,
                1
            )
        }


        weeklyStressView.setStressData(
            values
        )
    }

    private fun loadMoodHistory() {

        moodHistoryContainer.removeAllViews()

        var hasHistory = false

        val calendar =
            Calendar.getInstance()

        for (i in 0 until 7) {

            val dateKey =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(calendar.time)

            val mood =
                preferences.getString(
                    "${dateKey}_mood",
                    null
                )

            val emoji =
                preferences.getString(
                    "${dateKey}_emoji",
                    null
                )

            if (mood != null) {

                hasHistory = true

                addHistoryItem(
                    calendar.time,
                    emoji ?: "😐",
                    mood
                )
            }

            calendar.add(
                Calendar.DAY_OF_MONTH,
                -1
            )
        }

        if (!hasHistory) {

            val emptyText =
                TextView(requireContext())

            emptyText.text =
                "No mood history yet.\nComplete your first daily check-in."

            emptyText.textSize = 14f

            emptyText.setTextColor(
                Color.parseColor("#78909C")
            )

            emptyText.setPadding(
                0,
                20,
                0,
                20
            )

            moodHistoryContainer.addView(
                emptyText
            )
        }
    }

    private fun addHistoryItem(
        date: Date,
        emoji: String,
        mood: String
    ) {

        val row =
            LinearLayout(requireContext())

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            android.view.Gravity.CENTER_VERTICAL

        row.setPadding(
            0,
            16,
            0,
            16
        )

        val emojiView =
            TextView(requireContext())

        emojiView.text = emoji
        emojiView.textSize = 28f

        val textContainer =
            LinearLayout(requireContext())

        textContainer.orientation =
            LinearLayout.VERTICAL

        textContainer.setPadding(
            18,
            0,
            0,
            0
        )

        val moodText =
            TextView(requireContext())

        moodText.text = mood
        moodText.textSize = 16f

        moodText.setTypeface(
            null,
            Typeface.BOLD
        )

        moodText.setTextColor(
            Color.parseColor("#263238")
        )

        val dateText =
            TextView(requireContext())

        dateText.text =
            SimpleDateFormat(
                "EEE, dd MMM",
                Locale.getDefault()
            ).format(date)

        dateText.textSize = 13f

        dateText.setTextColor(
            Color.parseColor("#78909C")
        )

        textContainer.addView(
            moodText
        )

        textContainer.addView(
            dateText
        )

        row.addView(
            emojiView
        )

        row.addView(
            textContainer
        )

        moodHistoryContainer.addView(
            row
        )
    }

    private fun generateInsight() {

        val calendar =
            Calendar.getInstance()

        // Current week starts on Monday
        calendar.set(
            Calendar.DAY_OF_WEEK,
            Calendar.MONDAY
        )


        val scores =
            mutableListOf<Pair<Int, Int>>()


        var highestScore = -1

        var highestScoreDay = ""

        var totalScore = 0


        for (dayIndex in 0 until 7) {

            val dateKey =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(
                    calendar.time
                )


            // Only backend AI-derived daily averages
            if (
                preferences.contains(
                    "${dateKey}_stress"
                )
            ) {

                val score =
                    preferences.getInt(
                        "${dateKey}_stress",
                        0
                    )
                        .coerceIn(
                            0,
                            100
                        )


                scores.add(
                    Pair(
                        dayIndex,
                        score
                    )
                )


                totalScore +=
                    score


                if (
                    score > highestScore
                ) {

                    highestScore =
                        score


                    highestScoreDay =
                        SimpleDateFormat(
                            "EEEE",
                            Locale.getDefault()
                        ).format(
                            calendar.time
                        )
                }
            }


            calendar.add(
                Calendar.DAY_OF_MONTH,
                1
            )
        }


        // =====================================================
        // NO DATA
        // =====================================================

        if (
            scores.isEmpty()
        ) {

            weeklyAverageText.text =
                "Weekly Average: --"

            highestStressText.text =
                "Highest Daily Score: --"

            stressTrendText.text =
                "Trend: Not enough data"

            dataCoverageText.text =
                "AI chat data: 0 of 7 days"

            aiInsightText.text =
                "No AI-derived stress score data is available for this week yet. " +
                        "Chat with MindMate during the week to build your weekly score summary."

            return
        }


        // =====================================================
        // WEEKLY AVERAGE
        // =====================================================

        val averageScore =
            totalScore.toDouble() /
                    scores.size.toDouble()


        val roundedAverage =
            kotlin.math.round(
                averageScore
            ).toInt()


        weeklyAverageText.text =
            "Weekly Average: $roundedAverage/100"


        highestStressText.text =
            "Highest Daily Score: $highestScore/100 on $highestScoreDay"


        dataCoverageText.text =
            "AI chat data: ${scores.size} of 7 days"


        // =====================================================
        // TREND
        // =====================================================

        val trendSlope =
            calculateTrendSlope(
                scores
            )


        val trendText =
            when {

                scores.size < 2 ->
                    "Not enough data"

                trendSlope <= -3 ->
                    "Decreasing ↓"

                trendSlope >= 3 ->
                    "Increasing ↑"

                else ->
                    "Stable →"
            }


        stressTrendText.text =
            "Trend: $trendText"


        // =====================================================
        // DYNAMIC INSIGHT
        // =====================================================

        aiInsightText.text =
            buildDynamicInsight(
                averageStress = averageScore,
                highestStress = highestScore,
                highestStressDay = highestScoreDay,
                trendSlope = trendSlope,
                dataCount = scores.size
            )
    }
    private fun calculateTrendSlope(
        scores: List<Pair<Int, Int>>
    ): Double {

        if (scores.size < 2) {
            return 0.0
        }

        val meanX =
            scores.map { it.first }
                .average()

        val meanY =
            scores.map { it.second }
                .average()

        var numerator = 0.0
        var denominator = 0.0

        for ((day, score) in scores) {

            val xDifference =
                day - meanX

            val yDifference =
                score - meanY

            numerator +=
                xDifference * yDifference

            denominator +=
                xDifference * xDifference
        }

        if (denominator == 0.0) {
            return 0.0
        }

        return numerator / denominator
    }
    private fun buildDynamicInsight(
        averageStress: Double,
        highestStress: Int,
        highestStressDay: String,
        trendSlope: Double,
        dataCount: Int
    ): String {

        val roundedAverage =
            kotlin.math.round(
                averageStress
            ).toInt()


        // =====================================================
        // ONE DAY ONLY
        // =====================================================

        if (
            dataCount == 1
        ) {

            return (
                    "Only one day of AI-analyzed chat data is available this week. " +
                            "The recorded daily average model score is $roundedAverage/100. " +
                            "More days of chat data are needed before a weekly trend can be described."
                    )
        }


        // =====================================================
        // SCORE RANGE SUMMARY
        // =====================================================

        val averageMessage =
            when {

                averageStress >= 75 ->

                    "Your available AI-derived scores are currently in a higher range."


                averageStress >= 50 ->

                    "Your available AI-derived scores are currently in a moderate range."


                averageStress >= 30 ->

                    "Your available AI-derived scores are currently in a lower-to-moderate range."


                else ->

                    "Your available AI-derived scores are currently in a lower range."
            }


        // =====================================================
        // TREND SUMMARY
        // =====================================================

        val trendMessage =
            when {

                trendSlope <= -3 ->

                    "The available daily scores show a decreasing pattern across the week."


                trendSlope >= 3 ->

                    "The available daily scores show an increasing pattern across the week."


                else ->

                    "The available daily scores have remained relatively stable."
            }


        // =====================================================
        // HIGHEST DAILY VALUE
        // =====================================================

        val highestMessage =
            "The highest recorded daily average model score is " +
                    "$highestStress/100 on $highestStressDay."


        // =====================================================
        // SUPPORTIVE NON-CLINICAL MESSAGE
        // =====================================================

        val recommendation =
            when {

                averageStress >= 75 ->

                    "If you would like a short pause, you can use one of MindMate's supportive activities or continue chatting."


                averageStress >= 50 ->

                    "Regular check-ins may help you observe how these scores change over time."


                trendSlope >= 3 ->

                    "It may be useful to keep checking in over the next few days to see whether this pattern continues."


                else ->

                    "Continue checking in to build a clearer weekly picture."
            }


        return (
                "$averageMessage " +
                        "$trendMessage " +
                        "$highestMessage " +
                        "$recommendation " +
                        " These values are AI model outputs and are not a clinical assessment."
                )
    }
}
