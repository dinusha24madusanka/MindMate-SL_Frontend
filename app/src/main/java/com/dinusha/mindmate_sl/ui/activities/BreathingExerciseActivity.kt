package com.dinusha.mindmate_sl.ui.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dinusha.mindmate_sl.R
import android.content.Context
import android.content.Intent

class BreathingExerciseActivity : AppCompatActivity() {

    private lateinit var tvPhase: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvRound: TextView
    private lateinit var btnStart: Button
    private lateinit var breathingCircle: android.view.View

    private var currentTimer: CountDownTimer? = null
    private var running = false
    private var currentRound = 1

    private val totalRounds = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breathing_exercise)

        tvPhase = findViewById(R.id.tvBreathingPhase)
        tvTimer = findViewById(R.id.tvBreathingTimer)
        tvRound = findViewById(R.id.tvBreathingRound)
        btnStart = findViewById(R.id.btnStartBreathing)
        breathingCircle = findViewById(R.id.breathingCircle)

        btnStart.setOnClickListener {

            if (!running) {
                startExercise()
            } else {
                stopExercise()
            }
        }
    }

    private fun startExercise() {

        running = true
        currentRound = 1

        btnStart.text = "Stop"

        startInhale()
    }

    private fun startInhale() {

        if (!running) return

        tvRound.text =
            "Round $currentRound of $totalRounds"

        runPhase(
            title = "Breathe In",
            seconds = 4,
            scale = 1.35f
        ) {
            startHold()
        }
    }

    private fun startHold() {

        runPhase(
            title = "Hold",
            seconds = 7,
            scale = 1.35f
        ) {
            startExhale()
        }
    }

    private fun startExhale() {

        runPhase(
            title = "Breathe Out",
            seconds = 8,
            scale = 0.85f
        ) {

            if (currentRound < totalRounds) {

                currentRound++

                startInhale()

            } else {

                finishExercise()
            }
        }
    }

    private fun runPhase(
        title: String,
        seconds: Int,
        scale: Float,
        onComplete: () -> Unit
    ) {

        if (!running) return

        tvPhase.text = title

        breathingCircle.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(seconds * 1000L)
            .start()

        currentTimer?.cancel()

        currentTimer =
            object : CountDownTimer(
                seconds * 1000L,
                1000L
            ) {

                override fun onTick(
                    millisUntilFinished: Long
                ) {

                    val secondsLeft =
                        (millisUntilFinished + 999) / 1000

                    tvTimer.text =
                        secondsLeft.toString()
                }

                override fun onFinish() {

                    tvTimer.text = "0"

                    if (running) {
                        onComplete()
                    }
                }
            }.start()
    }

    private fun finishExercise() {

        running = false

        currentTimer?.cancel()

        tvPhase.text = "Completed"
        tvTimer.text = "✓"

        btnStart.text = "Completed"
        btnStart.isEnabled = false

        // Add MindPoints
        addMindPoints(15)

        // Return result to ChatFragment
        val resultIntent = Intent().apply {

            putExtra(
                RESULT_ACTIVITY_TYPE,
                "BREATHING"
            )

            putExtra(
                RESULT_POINTS,
                15
            )
        }

        setResult(
            RESULT_OK,
            resultIntent
        )

        Toast.makeText(
            this,
            "Breathing session completed. +15 MindPoints",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun addMindPoints(points: Int) {

        val prefs =
            getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )

        val currentPoints =
            prefs.getInt(
                "MIND_POINTS",
                0
            )

        prefs.edit()
            .putInt(
                "MIND_POINTS",
                currentPoints + points
            )
            .apply()
    }

    private fun stopExercise() {

        running = false

        currentTimer?.cancel()

        breathingCircle.animate()
            .cancel()

        breathingCircle.scaleX = 1f
        breathingCircle.scaleY = 1f

        tvPhase.text = "Ready"
        tvTimer.text = "4"
        tvRound.text = "4 guided rounds"

        btnStart.text = "Start"
    }

    override fun onDestroy() {

        currentTimer?.cancel()

        super.onDestroy()
    }

    companion object {

        const val RESULT_ACTIVITY_TYPE =
            "result_activity_type"

        const val RESULT_POINTS =
            "result_points"
    }
}