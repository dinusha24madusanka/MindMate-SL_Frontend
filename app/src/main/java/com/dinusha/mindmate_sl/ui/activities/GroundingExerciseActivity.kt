package com.dinusha.mindmate_sl.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dinusha.mindmate_sl.R

class GroundingExerciseActivity : AppCompatActivity() {

    private lateinit var tvStepNumber: TextView
    private lateinit var tvPrompt: TextView
    private lateinit var etAnswer: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNext: Button

    private var currentStep = 0

    private val prompts = listOf(

        "Name 5 things you can see around you.",

        "Name 4 things you can physically feel or touch.",

        "Name 3 things you can hear right now.",

        "Name 2 things you can smell or notice around you.",

        "Name 1 thing you can taste, or simply notice one slow breath."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_grounding_exercise
        )

        tvStepNumber =
            findViewById(R.id.tvGroundingStep)

        tvPrompt =
            findViewById(R.id.tvGroundingPrompt)

        etAnswer =
            findViewById(R.id.etGroundingAnswer)

        progressBar =
            findViewById(R.id.groundingProgress)

        btnNext =
            findViewById(R.id.btnGroundingNext)

        updateStep()

        btnNext.setOnClickListener {

            if (
                etAnswer.text
                    .toString()
                    .trim()
                    .isEmpty()
            ) {

                etAnswer.error =
                    "Write something you notice first."

                return@setOnClickListener
            }

            if (
                currentStep <
                prompts.lastIndex
            ) {

                currentStep++

                etAnswer.text.clear()

                updateStep()

            } else {

                completeExercise()
            }
        }
    }

    private fun updateStep() {

        tvStepNumber.text =
            "Step ${currentStep + 1} of ${prompts.size}"

        tvPrompt.text =
            prompts[currentStep]

        progressBar.progress =
            currentStep + 1

        btnNext.text =
            if (
                currentStep ==
                prompts.lastIndex
            ) {
                "Finish"
            } else {
                "Next"
            }
    }

    private fun completeExercise() {

        Toast.makeText(
            this,
            "Grounding exercise completed.",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}