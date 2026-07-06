package com.dinusha.mindmate_sl.ui.chat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import kotlin.random.Random

class ChatFragment : Fragment(R.layout.fragment_chat) {

    // Views
    private lateinit var lottieRobotAvatar: LottieAnimationView
    private lateinit var avatarBackgroundContainer: View
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageView
    private lateinit var rvChatMessages: RecyclerView

    // Top Header Views (Green Circle Area)
    private lateinit var tvTopBarAvatarName: TextView
    private lateinit var ivTopBarAvatar: ImageView

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
        tvTopBarAvatarName = view.findViewById(R.id.tvTopBarAvatarName)
        ivTopBarAvatar = view.findViewById(R.id.ivTopBarAvatar)

        // 2. තෝරාගත් රොබෝවා අනුව ඉහළ Header එක සැකසීම
        setupTopHeader()

        // 3. Setup RecyclerView
        chatAdapter = ChatAdapter(messageList)
        rvChatMessages.layoutManager = LinearLayoutManager(requireContext())
        rvChatMessages.adapter = chatAdapter

        // 4. Send Message Action
        btnSendMessage.setOnClickListener {
            val messageText = etMessageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // පරිශීලකයාගේ පණිවිඩය එකතු කිරීම
                messageList.add(ChatMessage(messageText, isUser = true))
                chatAdapter.notifyDataSetChanged()
                rvChatMessages.scrollToPosition(messageList.size - 1)
                etMessageInput.text.clear()

                // මකා දැමූ Mock Reply තර්කනය නැවත ස්ථාපනය කිරීම (Handler එකක් මගින් ආරක්ෂිතව)
                // සැබෑ ජාල සම්බන්ධතාවයක් හැඟවීම සඳහා මිලි තත්පර 1000 ක (තත්පර 1) ප්‍රමාදයක් ලබා දී ඇත
                Handler(Looper.getMainLooper()).postDelayed({

                    // හැම Mood එකක්ම (Happy, Neutral, Sad) මාරුවෙන් මාරුවට පරීක්ෂා කිරීමට 0-100 අතර සසම්භාවී Stress අගයක් ගැනීම
                    val simulatedStressScore = Random.nextInt(0, 101)
                    val botReplyText = "ඔයා කිව්වේ \"$messageText\" කියලා"

                    // රොබෝගේ පිළිතුර UI එකට යැවීම
                    receiveBotResponse(botReplyText, simulatedStressScore)

                }, 1000)
            }
        }

        // Default ආරම්භක තත්ත්වය
        updateAvatarAndMood(50)
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
            Log.e("ChatFragment", "Animation Error: $animationFileName", e)
        }
    }

    /**
     * රොබෝවරයාගේ පිළිතුර ලැයිස්තුවට එකතු කර තිරය Update කිරීම
     */
    fun receiveBotResponse(reply: String, stressScore: Int) {
        updateAvatarAndMood(stressScore)
        messageList.add(ChatMessage(reply, isUser = false))
        chatAdapter.notifyDataSetChanged()
        rvChatMessages.scrollToPosition(messageList.size - 1)
    }
}