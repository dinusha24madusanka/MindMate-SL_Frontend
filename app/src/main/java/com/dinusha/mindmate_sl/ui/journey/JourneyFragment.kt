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
    private lateinit var aiInsightText: TextView

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

        aiInsightText =
            view.findViewById(R.id.aiInsightText)

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
                "${dateKey}_stress",
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

    private fun loadWeeklyStressData() {

        val values =
            MutableList<Float?>(7) {
                null
            }

        val calendar =
            Calendar.getInstance()

        // Start from Monday
        calendar.set(
            Calendar.DAY_OF_WEEK,
            Calendar.MONDAY
        )

        for (i in 0 until 7) {

            val dateKey =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(calendar.time)

            if (
                preferences.contains(
                    "${dateKey}_stress"
                )
            ) {

                values[i] =
                    preferences.getInt(
                        "${dateKey}_stress",
                        0
                    ).toFloat()
            }

            calendar.add(
                Calendar.DAY_OF_MONTH,
                1
            )
        }

        weeklyStressView.setStressData(values)
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

        var total = 0
        var count = 0
        var highestStress = -1
        var highestStressDay = ""

        for (i in 0 until 7) {

            val dateKey =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(calendar.time)

            if (
                preferences.contains(
                    "${dateKey}_stress"
                )
            ) {

                val stress =
                    preferences.getInt(
                        "${dateKey}_stress",
                        0
                    )

                total += stress
                count++

                if (stress > highestStress) {

                    highestStress = stress

                    highestStressDay =
                        SimpleDateFormat(
                            "EEEE",
                            Locale.getDefault()
                        ).format(calendar.time)
                }
            }

            calendar.add(
                Calendar.DAY_OF_MONTH,
                -1
            )
        }

        if (count == 0) {

            aiInsightText.text =
                "Complete your daily check-in to start receiving wellbeing insights."

            return
        }

        val average =
            total / count

        aiInsightText.text =
            when {

                average >= 70 ->
                    "Your recent check-ins suggest a higher stress pattern. " +
                            "Your highest recorded stress was on $highestStressDay. " +
                            "Consider taking a short break, doing a breathing exercise, " +
                            "or using the MindMate chat for support."

                average >= 45 ->
                    "Your mood has been mixed recently. " +
                            "Stress was highest on $highestStressDay. " +
                            "Regular check-ins can help you identify what affects your wellbeing."

                else ->
                    "Your recent mood trend looks relatively positive. " +
                            "Keep maintaining the habits that are helping you feel well."
            }
    }
}