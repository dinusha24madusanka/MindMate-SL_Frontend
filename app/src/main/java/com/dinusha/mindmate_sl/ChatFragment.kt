package com.dinusha.mindmate_sl

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

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = ArrayList<ChatMessage>()
    private lateinit var rvChatMessages: RecyclerView
    private lateinit var ivCenterGizmo: ImageView

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
}