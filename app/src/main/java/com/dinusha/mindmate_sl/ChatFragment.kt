package com.dinusha.mindmate_sl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class ChatFragment : Fragment(R.layout.fragment_chat) {

    // Views (පැරණි ඒවා එලෙසමයි)
    private lateinit var lottieRobotAvatar: LottieAnimationView
    private lateinit var avatarBackgroundContainer: View
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageView
    private lateinit var rvChatMessages: RecyclerView

    // New Views: ඉහළ තීරුවේ (Green Circle) වෙනස් වන මූලාංග
    private lateinit var tvTopRobotName: TextView
    private lateinit var ivTopRobotIcon: ImageView

    // Chat Data
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. View Initialization
        lottieRobotAvatar = view.findViewById(R.id.lottieRobotAvatar)
        avatarBackgroundContainer = view.findViewById(R.id.avatarBackgroundContainer)
        etMessageInput = view.findViewById(R.id.etMessageInput)
        btnSendMessage = view.findViewById(R.id.btnSendMessage)
        rvChatMessages = view.findViewById(R.id.rvChatMessages)

        // 2. Top Header Views හඳුන්වා දීම (XML එකේ ID සමඟ ගළපා ගන්න)
        tvTopRobotName = view.findViewById(R.id.tvTopBarAvatarName) // XML හි රොබෝ නම පෙන්වන TextView ID එක
        ivTopRobotIcon = view.findViewById(R.id.ivTopBarAvatar) // XML හි කුඩා රොබෝ පින්තූරයේ ImageView ID එක

        // 3. ඉහළ Header තීරුව ගතිකව යාවත්කාලීන කිරීමේ ශ්‍රිතය ඇමතීම
        setupTopHeader()

        // 4. Setup RecyclerView
        chatAdapter = ChatAdapter(messageList)
        rvChatMessages.layoutManager = LinearLayoutManager(requireContext())
        rvChatMessages.adapter = chatAdapter

        // 5. Send Message Action
        btnSendMessage.setOnClickListener {
            val messageText = etMessageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                messageList.add(ChatMessage(messageText, isUser = true))
                chatAdapter.notifyDataSetChanged()
                rvChatMessages.scrollToPosition(messageList.size - 1)
                etMessageInput.text.clear()
            }
        }

        // ආරම්භක මනෝභාවය සැකසීම (පැරණි තර්කනය එලෙසමයි)
        updateAvatarAndMood(50)
    }

    /**
     * තෝරාගත් රොබෝවරයා අනුව ඉහළ තීරුවේ (Header) නම සහ පින්තූරය වෙනස් කරන නව ශ්‍රිතය
     */
    private fun setupTopHeader() {
        val sharedPreferences = requireContext().getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
        val selectedAvatarId = sharedPreferences.getString("SELECTED_AVATAR_ID", "bot_gizmo") ?: "bot_gizmo"

        when (selectedAvatarId) {
            "bot_astro" -> {
                tvTopRobotName.text = "Astro"
                // Astro සඳහා වන drawable Icon එක මෙතනට ආදේශ කරන්න
                ivTopRobotIcon.setImageResource(R.drawable.ic_astro_icon)
            }
            "bot_neo" -> {
                tvTopRobotName.text = "Neo"
                // Neo සඳහා වන drawable Icon එක මෙතනට ආදේශ කරන්න
                ivTopRobotIcon.setImageResource(R.drawable.ic_neo_icon)
            }
            else -> {
                tvTopRobotName.text = "Gizmo"
                // Gizmo සඳහා වන drawable Icon එක මෙතනට ආදේශ කරන්න
                ivTopRobotIcon.setImageResource(R.drawable.ic_gizmo_icon)
            }
        }
    }

    /**
     * රොබෝවාගේ Animation සහ මැද කොටුවේ Background වර්ණය යාවත්කාලීන කිරීම (කිසිදු වෙනසක් කර නැත)
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
            android.util.Log.e("ChatFragment", "Animation Error: $animationFileName", e)
        }
    }

    /**
     * Backend එකෙන් පිළිතුරක් ලැබුණු විට ක්‍රියාත්මක වන ශ්‍රිතය
     */
    fun receiveBotResponse(reply: String, stressScore: Int) {
        updateAvatarAndMood(stressScore)
        messageList.add(ChatMessage(reply, isUser = false))
        chatAdapter.notifyDataSetChanged()
        rvChatMessages.scrollToPosition(messageList.size - 1)
    }
}