package com.dinusha.mindmate_sl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        fun bind(chatMessage: ChatMessage) {
            tvBotMessage.text = chatMessage.text
        }
    }
}