package com.dinusha.mindmate_sl.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.data.model.GameItem

class GamesAdapter(
    private val gamesList: List<GameItem>,
    private val onGameClick: (GameItem) -> Unit
) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGameImage: ImageView = itemView.findViewById(R.id.ivGameImage)
        val tvGameTitle: TextView = itemView.findViewById(R.id.tvGameTitle)
        val tvGameDesc: TextView = itemView.findViewById(R.id.tvGameDesc)
        val tvGameDuration: TextView = itemView.findViewById(R.id.tvGameDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gamesList[position]
        holder.tvGameTitle.text = game.title
        holder.tvGameDesc.text = game.description
        holder.tvGameDuration.text = game.durationText
        holder.ivGameImage.setImageResource(game.imageResId)

        holder.itemView.setOnClickListener {
            onGameClick(game)
        }
    }

    override fun getItemCount(): Int = gamesList.size
}
