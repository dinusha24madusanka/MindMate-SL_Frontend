package com.dinusha.mindmate_sl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var lottieRobotAvatar: LottieAnimationView
    private lateinit var avatarBackgroundContainer: View
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageView
    private lateinit var rvChatMessages: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Views හඳුන්වා දීම (XML හි ID වලට අනුව)
        lottieRobotAvatar = view.findViewById(R.id.lottieRobotAvatar)
        avatarBackgroundContainer = view.findViewById(R.id.avatarBackgroundContainer)
        etMessageInput = view.findViewById(R.id.etMessageInput)
        btnSendMessage = view.findViewById(R.id.btnSendMessage)
        rvChatMessages = view.findViewById(R.id.rvChatMessages)

        // 2. Chat List එක සකස් කිරීම
        rvChatMessages.layoutManager = LinearLayoutManager(requireContext())
        // මෙතනදී ඔබගේ ChatAdapter එක සෙට් කරන්න: rvChatMessages.adapter = yourAdapter

        // 3. Send බොත්තමේ ක්‍රියාවලිය
        btnSendMessage.setOnClickListener {
            val message = etMessageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                // මෙතනදී පණිවිඩය Backend එකට යවා, ලැබෙන stress score එක updateAvatarAndMood එකට යවන්න
                etMessageInput.text.clear()
            }
        }

        updateAvatarAndMood(50)
    }

    private fun updateAvatarAndMood(stressLevel: Int) {
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

        // නිවැරදි ගොනු නාමය: "astro_happy.json" වැනි ආකෘතියකට මාරු කරන ලදී (Space ඉවත් කරන ලදී)
        val animationFileName = "${avatarPrefix}_${mood}.json"

        try {
            avatarBackgroundContainer.setBackgroundColor(Color.parseColor(backgroundColorHex))

            // Play the Lottie animation for the selected avatar and mood
            lottieRobotAvatar.setAnimation(animationFileName)
            lottieRobotAvatar.playAnimation()
        } catch (e: Exception) {
            android.util.Log.e("ChatFragment", "Lottie Error: $animationFileName", e)
        }
    }
}