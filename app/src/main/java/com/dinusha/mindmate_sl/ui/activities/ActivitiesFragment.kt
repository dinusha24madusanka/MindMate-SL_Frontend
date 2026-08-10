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
            in 0..30 -> {
                tvStressStateTitle.text = "Perfectly Relaxed"
                tvStressStateDesc.text = "Your mental state is excellent! Keep maintaining this balance with light activities."
            }
            in 31..70 -> {
                tvStressStateTitle.text = "Slightly Stressed"
                tvStressStateDesc.text = "Based on your recent chats, we recommend a 5-minute casual gaming break to lower cortisol levels."
            }
            else -> {
                tvStressStateTitle.text = "Highly Stressed"
                tvStressStateDesc.text = "Deep breathing exercises and alpha waves are highly recommended to calm your nervous system right now."
            }
        }
    }

    private fun setupGamesRecyclerView() {

        val gamesList = listOf(

            GameItem(
                "Mind Reset",
                "A short focus game for a quick mental break.",
                "2–5 Mins",
                R.drawable.ic_activities,
                "file:///android_asset/games/mind_reset.html"
            ),

            GameItem(
                "Zen Blocks",
                "A simple concentration game.",
                "5 Mins",
                R.drawable.ic_activities,
                ""
            ),

            GameItem(
                "Focus Flight",
                "A light attention game.",
                "5 Mins",
                R.drawable.ic_activities,
                ""
            )
        )

        val gamesAdapter =
            GamesAdapter(gamesList) { game ->

                if (game.webUrl.isBlank()) {

                    Toast.makeText(
                        requireContext(),
                        "${game.title} is coming soon.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@GamesAdapter
                }

                val intent =
                    Intent(
                        requireContext(),
                        GameWebViewActivity::class.java
                    )

                intent.putExtra(
                    GameWebViewActivity.EXTRA_TITLE,
                    game.title
                )

                intent.putExtra(
                    GameWebViewActivity.EXTRA_URL,
                    game.webUrl
                )

                startActivity(intent)
            }

        rvGames.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvGames.adapter =
            gamesAdapter
    }

    private fun setupExercisesRecyclerView() {
        // Mock data for Exercises (Figma එකේ තියෙන ඒවා)
        val exerciseList = listOf(
            ExerciseItem(
                "4 - 7 - 8 Breathing Exercise",
                "A natural tranquilizer for the nervous system to reduce instant anxiety.",
                "2 Mins | Guided",
                R.drawable.ic_robot_welcome
            ),
            ExerciseItem(
                "Binaural Beats (Alpha Waves)",
                "Calm your overactive brain waves and improve focus using acoustic therapy.",
                "Audio Session | 5 Mins",
                R.drawable.ic_robot_welcome
            ),
            ExerciseItem(
                "5-4-3-2-1 Grounding Exercise",
                "Re-anchor your mind to the present moment during a high-stress situation.",
                "Cognitive | Interactive",
                R.drawable.ic_robot_welcome
            )
        )

        val exerciseAdapter =
            ExercisesAdapter(exerciseList) { exercise ->

                when {

                    exercise.title.contains(
                        "4 - 7 - 8",
                        ignoreCase = true
                    ) -> {

                        startActivity(
                            Intent(
                                requireContext(),
                                BreathingExerciseActivity::class.java
                            )
                        )
                    }

                    exercise.title.contains(
                        "5-4-3-2-1",
                        ignoreCase = true
                    ) -> {

                        startActivity(
                            Intent(
                                requireContext(),
                                GroundingExerciseActivity::class.java
                            )
                        )
                    }

                    else -> {

                        Toast.makeText(
                            requireContext(),
                            "Audio session will be added in the next step.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        rvExercises.layoutManager = LinearLayoutManager(requireContext())
        rvExercises.adapter = exerciseAdapter
    }
}