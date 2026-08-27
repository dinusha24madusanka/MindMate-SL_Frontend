package com.dinusha.mindmate_sl.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.data.model.ExerciseItem
import com.dinusha.mindmate_sl.data.model.GameItem
import android.content.Intent
class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    private lateinit var tvStressStateTitle: TextView
    private lateinit var tvStressStateDesc: TextView
    private lateinit var ivStatusRobot: ImageView
    private lateinit var rvGames: RecyclerView
    private lateinit var rvExercises: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize Views
        tvStressStateTitle = view.findViewById(R.id.tvStressStateTitle)
        tvStressStateDesc = view.findViewById(R.id.tvStressStateDesc)
        ivStatusRobot = view.findViewById(R.id.ivStatusRobot)
        rvGames = view.findViewById(R.id.rvGames)
        rvExercises = view.findViewById(R.id.rvExercises)

        // 2. Chat එකෙන් ලැබුණු Stress Score එක මත පදනම්ව Header එක Update කිරීම
        loadInferredStressState()

        // 3. Setup Lists
        setupGamesRecyclerView()
        setupExercisesRecyclerView()
    }

    private fun loadInferredStressState() {
        val sharedPreferences = requireContext().getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
        // Chat එකේදී මොඩල් එකෙන් සෙට් කරපු ලකුණ (Default 50)
        val currentStressScore = sharedPreferences.getInt("LAST_STRESS_SCORE", 50)
        val selectedAvatarId = sharedPreferences.getString("SELECTED_AVATAR_ID", "bot_gizmo") ?: "bot_gizmo"

        // තෝරාගත් රොබෝවා අනුව උඩ Icon එක සැකසීම
        val robotIcon = when (selectedAvatarId) {
            "bot_astro" -> R.drawable.ic_astro_icon
            "bot_neo" -> R.drawable.ic_neo_icon
            else -> R.drawable.ic_gizmo_icon
        }
        ivStatusRobot.setImageResource(robotIcon)

        // මොඩල් එකේ අගය අනුව නිර්දේශය වෙනස් කිරීම
        when (currentStressScore) {

            in 0..49 -> {
                tvStressStateTitle.text = "Recent Stress: Low"
                tvStressStateDesc.text = "Your recent AI-derived stress score is in the low range. You can choose a light activity if you would like a short break."
            }

            in 50..74 -> {
                tvStressStateTitle.text = "Recent Stress: Moderate"
                tvStressStateDesc.text = "Your recent AI-derived stress score is in the moderate range. Calm Bubbles is available as a short supportive break."
            }

            else -> {
                tvStressStateTitle.text = "Recent Stress: High"
                tvStressStateDesc.text = "Your recent AI-derived stress score is in the high range. Mandala Paint Flow is available as a short structured activity."
            }
        }
    }

    private fun setupGamesRecyclerView() {
        rvGames.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        val gamesList = listOf(
            GameItem(
                title = "Calm Bubbles",
                description = "A short focus game for a quick mental break.",
                durationText = "1 Min",
                imageResId = R.drawable.calm_bubbles_cover,
                webUrl = "file:///android_asset/games/calm_bubbles.html"
            ),
            GameItem(
                title = "Mandala Paint Flow",
                description = "A short mindful coloring activity.",
                durationText = "2 Mins",
                imageResId = R.drawable.mandala_paint_cover,
                webUrl = "file:///android_asset/games/mandala_paint_flow.html"
            )
        )
        val gamesAdapter = GamesAdapter(gamesList) { game ->
                if (game.webUrl.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        "${game.title} is coming soon.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@GamesAdapter
                }
                val intent = Intent(
                        requireContext(),
                        GameWebViewActivity::class.java
                    ).apply {
                        putExtra(
                            GameWebViewActivity.EXTRA_TITLE,
                            game.title
                        )
                        putExtra(
                            GameWebViewActivity.EXTRA_URL,
                            game.webUrl
                        )
                    }
                startActivity(intent)
            }
        rvGames.adapter = gamesAdapter
        rvGames.post {
            rvGames.scrollToPosition(0)
        }
    }
    private fun setupExercisesRecyclerView() {
        val exerciseList = listOf(
            ExerciseItem(
                "4 - 7 - 8 Breathing Exercise",
                "A guided breathing exercise for a short, structured pause.",
                "2 Mins | Guided",
                R.drawable.breathing_cover
            ),
            ExerciseItem(
                "5-4-3-2-1 Grounding Exercise",
                "A sensory grounding exercise to help you focus on the present moment.",
                "Cognitive | Interactive",
                R.drawable.grounding_cover
            )
        )

        val exerciseAdapter = ExercisesAdapter(exerciseList) { exercise ->
            when {
                exercise.title.contains("4 - 7 - 8", ignoreCase = true) -> {
                    startActivity(Intent(requireContext(), BreathingExerciseActivity::class.java))
                }
                exercise.title.contains("5-4-3-2-1", ignoreCase = true) -> {
                    startActivity(Intent(requireContext(), GroundingExerciseActivity::class.java))
                }
                else -> {
                    Toast.makeText(requireContext(), "Audio session will be added in the next step.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        rvExercises.layoutManager =
            LinearLayoutManager(requireContext())

        rvExercises.adapter =
            exerciseAdapter
    }
}