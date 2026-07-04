package com.dinusha.mindmate_sl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = ArrayList<ChatMessage>()
    private lateinit var rvChatMessages: RecyclerView
    private lateinit var ivCenterGizmo: ImageView
    private lateinit var lottieRobotAvatar: LottieAnimationView
    private lateinit var avatarBackgroundContainer: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // UI Views ටික අල්ලගන්නවා
        rvChatMessages = view.findViewById(R.id.rvChatMessages)
        ivCenterGizmo = view.findViewById(R.id.ivCenterGizmo)
        val etMessageInput = view.findViewById<EditText>(R.id.etMessageInput)
        val btnSendMessage = view.findViewById<ImageView>(R.id.btnSendMessage)

        // RecyclerView එක Setup කරනවා
        chatAdapter = ChatAdapter(messageList)
        rvChatMessages.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true // මැසේජ් පල්ලෙහා ඉඳන් උඩට ලෝඩ් වෙන්න
        }
        rvChatMessages.adapter = chatAdapter

        // Send බටන් එක ක්ලික් කරාම
        btnSendMessage.setOnClickListener {
            val userText = etMessageInput.text.toString().trim()
            if (userText.isNotEmpty()) {

                // 1. මැද තියෙන ලොකු රොබෝ ඉමේජ් එක හංගනවා මැසේජ් ආවට පස්සේ
                ivCenterGizmo.visibility = View.GONE

                // 2. යසර්ගේ මැසේජ් එක ලිස්ට් එකට දාලා Adapter එක Update කරනවා
                messageList.add(ChatMessage(userText, true))
                chatAdapter.notifyItemInserted(messageList.size - 1)
                rvChatMessages.scrollToPosition(messageList.size - 1)

                etMessageInput.text.clear()

                // 3. බොට්ගෙන් රිප්ලයි එකක් එනවා වගේ පෙන්වන්න (Simulated Delay)
                Handler(Looper.getMainLooper()).postDelayed({
                    // දැනට අපි යසර් කියපු දේම බොට් ලව්වා කියවමු (XLM-RoBERTa එක දානකන්)
                    messageList.add(ChatMessage("Gizmo: උඹ කිව්වේ \"$userText\" කියලනේ මචං?", false))
                    chatAdapter.notifyItemInserted(messageList.size - 1)
                    rvChatMessages.scrollToPosition(messageList.size - 1)
                }, 1000) // තත්පර 1ක පමාවීමක්
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lottieRobotAvatar = view.findViewById(R.id.lottieAvatar)
        avatarBackgroundContainer = view.findViewById(R.id.avatarBackgroundContainer)
        loadSelectedAvatar()
        updateStressLevelBackground(50)
    }
    private fun loadSelectedAvatar() {
        val sharedPreferences = requireContext().getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)

        // දත්ත නිවැරදිව ලැබේදැයි බැලීමට Default එකක් ලෙස "ERROR" යොදා ඇත
        val selectedAvatarId = sharedPreferences.getString("SELECTED_AVATAR_ID", "ERROR")

        // Logcat එකේ දත්ත පරීක්ෂා කිරීම සඳහා (මෙය අතිශය වැදගත් වේ)
        android.util.Log.d("MindMateDebug", "Retrieved Avatar ID: $selectedAvatarId")

        val animationFile: String
        val avatarName: String

        when (selectedAvatarId) {
            "bot_gizmo" -> {
                animationFile = "Gizmo Robot.json"
                avatarName = "Gizmo"
            }
            "bot_astro" -> {
                animationFile = "Astro Robot.json"
                avatarName = "Astro"
            }
            "bot_neo" -> {
                animationFile = "Neo Robot.json"
                avatarName = "Neo"
            }
            else -> {
                // දත්ත නිසි පරිදි Save වී නොමැති නම් Gizmo පෙන්වයි
                animationFile = "Gizmo Robot.json"
                avatarName = "Gizmo"
                android.util.Log.e("MindMateDebug", "Avatar ID mismatch or not saved!")
            }
        }

        // 1. ප්‍රධාන රොබෝ Animation එක වෙනස් කිරීම
        lottieRobotAvatar.setAnimation(animationFile)
        lottieRobotAvatar.playAnimation()

        // 2. Top Bar එකේ නම වෙනස් කිරීම
        // (ඔබගේ XML හි අදාළ TextView එකේ ID එක "tvTopBarAvatarName" යැයි උපකල්පනය කර ඇත. එය ඔබේ ID එකට වෙනස් කරන්න)
        val tvTopBarName = requireView().findViewById<android.widget.TextView>(R.id.tvTopBarAvatarName)
        tvTopBarName.text = avatarName
    }

    private fun updateStressLevelBackground(stressLevel: Int) {
        val backgroundColor = when (stressLevel) {
            in 0..30 -> Color.parseColor("#E0F2F1") // Light Green
            in 31..70 -> Color.parseColor("#FFF9C4") // Light Yellow
            in 71..100 -> Color.parseColor("#FFCDD2") // Light Red
            else -> Color.parseColor("#F5F5F5") // Default/Gray
        }
        avatarBackgroundContainer.setBackgroundColor(backgroundColor)
    }
}