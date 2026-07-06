package com.dinusha.mindmate_sl.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinusha.mindmate_sl.data.model.ChatMessage
import com.dinusha.mindmate_sl.R

class ChatAdapter(private val messageList: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View Types හඳුනාගන්න constants දෙකක්
    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_BOT = 2

    // මැසේජ් එක අනුව View Type එක මොකක්ද කියලා තීරණය කරන තැන
    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_bot, parent, false)
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is UserViewHolder) {
            holder.bind(message)
        } else if (holder is BotViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messageList.size

    // User ගේ මැසේජ් එක ViewHolder එකට බඳින එක (Binding)
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserMessage: TextView = itemView.findViewById(R.id.tvUserMessage)
        fun bind(chatMessage: ChatMessage) {
            tvUserMessage.text = chatMessage.text
        }
    }

    // Bot ගේ මැසේජ් එක ViewHolder එකට බඳින එක (Binding)
    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBotMessage: TextView = itemView.findViewById(R.id.tvBotMessage)
        private val ivBotAvatar: ImageView = itemView.findViewById(R.id.ivBotAvatar)

        fun bind(chatMessage: ChatMessage) {
            tvBotMessage.text = chatMessage.text

            // 1. Context එක හරහා SharedPreferences ලබා ගැනීම
            val context = itemView.context
            val sharedPreferences = context.getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
            val selectedAvatarId = sharedPreferences.getString("SELECTED_AVATAR_ID", "bot_gizmo") ?: "bot_gizmo"

            // 2. තෝරාගත් රොබෝවා අනුව නිවැරදි කුඩා Icon එක තෝරා ගැනීම
            val botIconRes = when (selectedAvatarId) {
                "bot_astro" -> R.drawable.ic_astro_icon
                "bot_neo" -> R.drawable.ic_neo_icon
                else -> R.drawable.ic_gizmo_icon
            }

            // 3. ViewHolder එක ඇතුලේ තියෙන රොබෝගේ ImageView එකට පින්තූරය සෙට් කිරීම
            ivBotAvatar.setImageResource(botIconRes)
        }
    }
}