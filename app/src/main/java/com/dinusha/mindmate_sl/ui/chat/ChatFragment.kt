package com.dinusha.mindmate_sl.ui.chat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.dinusha.mindmate_sl.data.model.ChatMessage
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.model.ChatRequest
import com.dinusha.mindmate_sl.model.ChatResponse
import com.dinusha.mindmate_sl.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import com.dinusha.mindmate_sl.ui.activities.GameWebViewActivity
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import com.dinusha.mindmate_sl.ui.activities.BreathingExerciseActivity
import com.dinusha.mindmate_sl.ui.activities.GroundingExerciseActivity
class ChatFragment : Fragment(R.layout.fragment_chat) {

    // Views
    private lateinit var lottieRobotAvatar: LottieAnimationView
    private lateinit var avatarBackgroundContainer: View
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageView
    private lateinit var rvChatMessages: RecyclerView
    private lateinit var cardGameSuggestion: MaterialCardView
    private lateinit var btnPlaySuggestedGame: MaterialButton
    private lateinit var btnDismissGame: MaterialButton
    // Top Header Views (Green Circle Area)
    private lateinit var tvTopBarAvatarName: TextView
    private lateinit var ivTopBarAvatar: ImageView
    // Chat Data
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var cardFeelingCheck: MaterialCardView
    private lateinit var btnFeelingBetter: MaterialButton
    private lateinit var btnFeelingSame: MaterialButton
    private lateinit var btnStillStressed: MaterialButton
    private lateinit var cardSupportActivity: MaterialCardView
    private lateinit var btnSuggestedBreathing: MaterialButton
    private lateinit var btnSuggestedGrounding: MaterialButton
    private lateinit var btnKeepChatting: MaterialButton
    private lateinit var tvGameSuggestion: TextView

    private var suggestedGameType = "CALM_BUBBLES"
    private var suggestedMandalaLevel = 1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. View Initialization
        lottieRobotAvatar = view.findViewById(R.id.lottieRobotAvatar)
        avatarBackgroundContainer = view.findViewById(R.id.avatarBackgroundContainer)
        etMessageInput = view.findViewById(R.id.etMessageInput)
        btnSendMessage = view.findViewById(R.id.btnSendMessage)
        rvChatMessages = view.findViewById(R.id.rvChatMessages)
        tvTopBarAvatarName = view.findViewById(R.id.tvTopBarAvatarName)
        ivTopBarAvatar = view.findViewById(R.id.ivTopBarAvatar)
        cardGameSuggestion = view.findViewById(R.id.cardGameSuggestion)
        btnPlaySuggestedGame = view.findViewById(R.id.btnPlaySuggestedGame)
        btnDismissGame = view.findViewById(R.id.btnDismissGame)
        cardGameSuggestion.visibility = View.GONE
        cardFeelingCheck = view.findViewById(R.id.cardFeelingCheck)
        btnFeelingBetter = view.findViewById(R.id.btnFeelingBetter)
        btnFeelingSame = view.findViewById(R.id.btnFeelingSame)
        btnStillStressed = view.findViewById(R.id.btnStillStressed)
        cardFeelingCheck.visibility = View.GONE
        cardSupportActivity = view.findViewById(R.id.cardSupportActivity)
        btnSuggestedBreathing = view.findViewById(R.id.btnSuggestedBreathing)
        btnSuggestedGrounding = view.findViewById(R.id.btnSuggestedGrounding)
        btnKeepChatting =view.findViewById(R.id.btnKeepChatting)
        cardSupportActivity.visibility = View.GONE

        // 2. Configuring the top header based on the selected robot
        setupTopHeader()
        tvGameSuggestion =view.findViewById(R.id.tvGameSuggestion)

        // 3. Setup RecyclerView
        chatAdapter = ChatAdapter(messageList)
        rvChatMessages.layoutManager = LinearLayoutManager(requireContext())
        rvChatMessages.adapter = chatAdapter

        // 4. Send Message Action
        btnSendMessage.setOnClickListener {
            val messageText = etMessageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Adding the user's message
                messageList.add(ChatMessage(messageText, isUser = true))

                // Optimized update: The correct method instead of notifyDataSetChanged()
                chatAdapter.notifyItemInserted(messageList.size - 1)
                rvChatMessages.scrollToPosition(messageList.size - 1)
                etMessageInput.text.clear()

                // API Request එක යැවීම
                val request = ChatRequest(messageText)
                RetrofitClient.getApiService().sendChatMessage(request).enqueue(object : Callback<ChatResponse> {
                    override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                        if (response.isSuccessful && response.body() != null) {

                            val aiReply = response.body()!!.reply
                            val stressScore = response.body()!!.stressScore

                            // AI stress score එක Journey එකට save කරනවා
                            saveStressScoreToJourney(stressScore)

                            // Chat UI එකට reply එක දානවා
                            receiveBotResponse(aiReply, stressScore)

                        } else {
                            receiveBotResponse("Server error, please try again later.", 50)
                        }
                    }

                    override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                        receiveBotResponse("Error: ${t.message}", 50)
                    }
                })
            }
        }

        btnPlaySuggestedGame.setOnClickListener {

            cardGameSuggestion.visibility = View.GONE

            val intent =
                Intent(
                    requireContext(),
                    GameWebViewActivity::class.java
                )

            when (suggestedGameType) {

                "MANDALA" -> {

                    intent.putExtra(
                        GameWebViewActivity.EXTRA_TITLE,
                        "Mandala Paint Flow"
                    )

                    intent.putExtra(
                        GameWebViewActivity.EXTRA_URL,
                        "file:///android_asset/games/mandala_paint_flow.html"
                    )
                }

                else -> {

                    intent.putExtra(
                        GameWebViewActivity.EXTRA_TITLE,
                        "Calm Bubbles"
                    )

                    intent.putExtra(
                        GameWebViewActivity.EXTRA_URL,
                        "file:///android_asset/games/calm_bubbles.html"
                    )
                }
            }

            gameLauncher.launch(intent)
        }

        btnSuggestedBreathing.setOnClickListener {

            cardSupportActivity.visibility =
                View.GONE

            val intent =
                Intent(
                    requireContext(),
                    BreathingExerciseActivity::class.java
                )

            supportActivityLauncher.launch(intent)
        }

        btnSuggestedGrounding.setOnClickListener {

            cardSupportActivity.visibility =
                View.GONE

            val intent =
                Intent(
                    requireContext(),
                    GroundingExerciseActivity::class.java
                )

            supportActivityLauncher.launch(intent)
        }

        btnDismissGame.setOnClickListener {

            cardGameSuggestion.visibility =
                View.GONE

            addBotMessage(
                "No problem 🌿 We can keep chatting."
            )
        }

        btnFeelingBetter.setOnClickListener {

            saveActivityFeedback(
                "BETTER"
            )

            cardFeelingCheck.visibility =
                View.GONE

            addBotMessage(
                "I'm glad the short break felt helpful 🌿 Whenever you're ready, we can keep chatting."
            )
        }


        btnFeelingSame.setOnClickListener {

            saveActivityFeedback(
                "SAME"
            )

            cardFeelingCheck.visibility =
                View.GONE

            addBotMessage(
                "That's okay 🌿 A short break doesn't always change how we feel immediately. We can keep chatting or try another activity."
            )
        }


        btnStillStressed.setOnClickListener {

            saveActivityFeedback(
                "STILL_STRESSED"
            )

            // Feeling card hide
            cardFeelingCheck.visibility =
                View.GONE

            // MindMate response
            addBotMessage(
                "Thanks for telling me 🌿 Let's try another short activity that feels comfortable for you."
            )

            // Show support activity options
            cardSupportActivity.visibility =
                View.VISIBLE
        }

        btnKeepChatting.setOnClickListener {

            cardSupportActivity.visibility =
                View.GONE

            addBotMessage(
                "Of course 💬 I'm here. Tell me what's on your mind."
            )
        }

        // Default ආරම්භක තත්ත්වය (පරණ ස්කෝර් එකක් නැත්නම් මැද අගය 50 ගනී)
        val sharedPreferences = requireContext().getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
        val lastSavedScore = sharedPreferences.getInt("LAST_STRESS_SCORE", 50)
        updateAvatarAndMood(lastSavedScore)
    }

    private val gameLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data

                val gameId =
                    data?.getStringExtra(
                        GameWebViewActivity.RESULT_GAME_ID
                    ) ?: ""

                val score =
                    data?.getIntExtra(
                        GameWebViewActivity.RESULT_GAME_SCORE,
                        0
                    ) ?: 0

                val points =
                    data?.getIntExtra(
                        GameWebViewActivity.RESULT_POINTS,
                        0
                    ) ?: 0

                if (gameId.isNotEmpty()) {

                    if (
                        gameId ==
                        "mandala_paint_flow"
                    ) {

                        val status =
                            data?.getStringExtra(
                                GameWebViewActivity.RESULT_GAME_STATUS
                            ) ?: "PAUSED"


                        val mandalaLevel =
                            data?.getIntExtra(
                                GameWebViewActivity.RESULT_MANDALA_LEVEL,
                                1
                            ) ?: 1


                        val mandalaPercent =
                            data?.getIntExtra(
                                GameWebViewActivity.RESULT_MANDALA_PERCENT,
                                0
                            ) ?: 0


                        onMandalaSessionReturned(
                            status,
                            mandalaLevel,
                            mandalaPercent,
                            points
                        )

                    } else {

                        onMiniGameCompleted(
                            gameId,
                            score,
                            points
                        )
                    }
                }
            }
        }

    private fun handleSupportActivityCompleted(
        activityType: String,
        points: Int
    ) {
        cardSupportActivity.visibility = View.GONE

        val activityName = when (activityType) {
            "BREATHING" -> "4-7-8 breathing session"
            "GROUNDING" -> "5-4-3-2-1 grounding exercise"
            else -> "support activity"
        }

        addBotMessage(
            "Welcome back 🌿 You completed the $activityName and earned +$points MindPoints. Take a moment and notice how you're feeling now."
        )

        cardFeelingCheck.visibility = View.VISIBLE
    }

    private val supportActivityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data

                val activityType =
                    data?.getStringExtra(
                        BreathingExerciseActivity.RESULT_ACTIVITY_TYPE
                    ) ?: ""

                val points =
                    data?.getIntExtra(
                        BreathingExerciseActivity.RESULT_POINTS,
                        0
                    ) ?: 0

                if (activityType.isNotEmpty()) {

                    handleSupportActivityCompleted(
                        activityType,
                        points
                    )
                }
            }
        }


    /**
     * තෝරාගත් රොබෝවරයා අනුව ඉහළ Header එකේ නම සහ Icon එක ගතිකව වෙනස් කිරීම
     */
    private fun setupTopHeader() {
        val sharedPreferences = requireContext().getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
        val selectedAvatarId = sharedPreferences.getString("SELECTED_AVATAR_ID", "bot_gizmo") ?: "bot_gizmo"

        when (selectedAvatarId) {
            "bot_astro" -> {
                tvTopBarAvatarName.text = "Astro"
                ivTopBarAvatar.setImageResource(R.drawable.ic_astro_icon)
            }
            "bot_neo" -> {
                tvTopBarAvatarName.text = "Neo"
                ivTopBarAvatar.setImageResource(R.drawable.ic_neo_icon)
            }
            else -> {
                tvTopBarAvatarName.text = "Gizmo"
                ivTopBarAvatar.setImageResource(R.drawable.ic_gizmo_icon)
            }
        }
    }

    /**
     * රොබෝවාගේ Animation සහ මැද කොටුවේ Background වර්ණය යාවත්කාලීන කිරීම
     */
    fun updateAvatarAndMood(stressLevel: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
        val selectedAvatarId = sharedPreferences.getString("SELECTED_AVATAR_ID", "bot_gizmo") ?: "bot_gizmo"

        val (mood, backgroundColorHex) = when (stressLevel) {
            in 0..30 -> Pair("happy", "#E0F2F1")
            in 31..70 -> Pair("neutral", "#FFF9C4")
            in 71..100 -> Pair("sad", "#FFCDD2")
            else -> Pair("neutral", "#F5F5F5")
        }

        val avatarPrefix = when (selectedAvatarId) {
            "bot_astro" -> "astro"
            "bot_neo" -> "neo"
            else -> "gizmo"
        }

        val animationFileName = "${avatarPrefix}_${mood}.json"

        try {
            avatarBackgroundContainer.setBackgroundColor(Color.parseColor(backgroundColorHex))
            lottieRobotAvatar.setAnimation(animationFileName)
            lottieRobotAvatar.playAnimation()
        } catch (e: Exception) {
            Log.e("ChatFragment", "Animation Error: $animationFileName", e)
        }
    }

    /**
     * රොබෝවරයාගේ පිළිතුර ලැයිස්තුවට එකතු කර තිරය Update කිරීම සහ අගය සේව් කිරීම
     */
    fun receiveBotResponse(reply: String, stressScore: Int) {
        // 1. ලැබුණු Stress Score එක SharedPreferences එකට සේව් කිරීම (ActivitiesFragment එකට කියවීමට)
        val sharedPreferences = requireContext().getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("LAST_STRESS_SCORE", stressScore).apply()

        // 2. රොබෝගේ පෙනුම වෙනස් කිරීම
        updateAvatarAndMood(stressScore)

        // 3. Stress Score එක අනුව ක්‍රියාකාරකම් යෝජනා කිරීම
        handleActivitySuggestion(stressScore)

        // 4. රොබෝවරයාගේ පිළිතුර ලැයිස්තුවට එකතු කර තිරය Update කිරීම
        addBotMessage(reply)
    }


    /**
     * රොබෝවරයාගේ පිළිතුර ලැයිස්තුවට එකතු කර තිරය Update කිරීම
     */
    private fun addBotMessage(message: String) {

        messageList.add(
            ChatMessage(
                message,
                isUser = false
            )
        )

        chatAdapter.notifyItemInserted(
            messageList.size - 1
        )

        rvChatMessages.scrollToPosition(
            messageList.size - 1
        )
    }

    /**
     * Stress Score එක අනුව ක්‍රීඩා හෝ වෙනත් ක්‍රියාකාරකම් යෝජනා කිරීම
     */
    private fun handleActivitySuggestion(
        stressScore: Int
    ) {

        if (stressScore <= 70) {

            cardGameSuggestion.visibility =
                View.GONE

            return
        }


        val prefs =
            requireContext()
                .getSharedPreferences(
                    "MindMatePrefs",
                    Context.MODE_PRIVATE
                )


        val mandalaStatus =
            prefs.getString(
                "MANDALA_LAST_STATUS",
                ""
            ) ?: ""


        val mandalaLevel =
            prefs.getInt(
                "MANDALA_LAST_LEVEL",
                1
            )


        val mandalaPercent =
            prefs.getInt(
                "MANDALA_LAST_PERCENT",
                0
            )


        /*
         * Incomplete Mandala session thiyenawanam
         * eka priority denna.
         */
        if (
            mandalaStatus == "PAUSED"
            &&
            mandalaPercent > 0
            &&
            mandalaPercent < 100
        ) {

            suggestedGameType =
                "MANDALA"

            suggestedMandalaLevel =
                mandalaLevel


            tvGameSuggestion.text =
                "You already have Mandala Flow progress saved 🌸\n" +
                        "Level $mandalaLevel • $mandalaPercent% complete. " +
                        "Would you like to continue your 2-minute break?"


            btnPlaySuggestedGame.text =
                "🌸 Continue Level $mandalaLevel"


            cardGameSuggestion.visibility =
                View.VISIBLE

            return
        }


        /*
         * Previous Mandala level completed.
         * Next level available.
         */
        if (
            mandalaStatus == "COMPLETED"
            &&
            mandalaLevel < 6
        ) {

            val nextLevel =
                mandalaLevel + 1


            suggestedGameType =
                "MANDALA"

            suggestedMandalaLevel =
                nextLevel


            tvGameSuggestion.text =
                "Your next Mandala Flow level is ready ✨\n" +
                        "Would you like another short 2-minute activity?"


            btnPlaySuggestedGame.text =
                "🌸 Start Level $nextLevel"


            cardGameSuggestion.visibility =
                View.VISIBLE

            return
        }


        /*
         * Default suggestion
         */
        suggestedGameType =
            "CALM_BUBBLES"


        tvGameSuggestion.text =
            "Would you like a short 60-second break with Calm Bubbles?"


        btnPlaySuggestedGame.text =
            "🫧 Calm Bubbles"


        cardGameSuggestion.visibility =
            View.VISIBLE
    }

    private fun onMandalaSessionReturned(
        status: String,
        level: Int,
        percent: Int,
        points: Int
    ) {

        cardGameSuggestion.visibility =
            View.GONE


        if (
            status.equals(
                "COMPLETED",
                ignoreCase = true
            )
        ) {

            val message =

                if (level < 6) {

                    "Great work 🌿 You completed Mandala Flow Level $level " +
                            "and earned +$points MindPoints. " +
                            "Level ${level + 1} is now unlocked ✨ " +
                            "How are you feeling now?"

                } else {

                    "Great work 🌿 You completed Mandala Flow Level 6 " +
                            "and earned +$points MindPoints. " +
                            "You have completed all six Mandala levels ✨ " +
                            "How are you feeling now?"
                }


            addBotMessage(
                message
            )

        } else {

            addBotMessage(
                "Nice work taking a short break 🌿 " +
                        "Your Mandala Flow Level $level progress has been saved at $percent%. " +
                        "You can continue from here next time. " +
                        "How are you feeling now?"
            )
        }


        cardFeelingCheck.visibility =
            View.VISIBLE
    }

    private fun onMiniGameCompleted(
        gameId: String,
        score: Int,
        points: Int
    ) {

        cardGameSuggestion.visibility =
            View.GONE

        addBotMessage(
            "Welcome back 🌿 You completed your 60-second reset and earned +$points MindPoints. How are you feeling now?"
        )
        // Show the feeling check card
        cardFeelingCheck.visibility = View.VISIBLE
    }

    private fun saveActivityFeedback(
        feedback: String
    ) {

        requireContext()
            .getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                "LAST_ACTIVITY_FEEDBACK",
                feedback
            )
            .apply()
    }

    private fun saveStressScoreToJourney(stressScore: Int) {

        val preferences = requireContext().getSharedPreferences(
            "mindmate_journey",
            android.content.Context.MODE_PRIVATE
        )

        val dateKey = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        // අද දවසේ කලින් save කරපු AI scores
        val currentSum = preferences.getInt(
            "${dateKey}_ai_stress_sum",
            0
        )

        val currentCount = preferences.getInt(
            "${dateKey}_ai_stress_count",
            0
        )

        // අලුත් score එක එකතු කරනවා
        val newSum = currentSum + stressScore
        val newCount = currentCount + 1

        // අද දවසේ average stress score
        val averageStress = newSum / newCount

        preferences.edit()
            .putInt(
                "${dateKey}_ai_stress_sum",
                newSum
            )
            .putInt(
                "${dateKey}_ai_stress_count",
                newCount
            )
            .putInt(
                "${dateKey}_stress",
                averageStress
            )
            .apply()
    }


}