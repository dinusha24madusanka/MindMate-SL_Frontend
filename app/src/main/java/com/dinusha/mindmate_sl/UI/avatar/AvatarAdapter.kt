package com.dinusha.mindmate_sl.UI.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.dinusha.mindmate_sl.R

class AvatarAdapter(private val avatarList: List<Avatar>) :
    RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    // ViewHolder class එක මගින් XML හි ඇති UI components මතකයේ රඳවා ගනී
    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lottieAvatar: LottieAnimationView = itemView.findViewById(R.id.lottieAvatar)
        val tvAvatarName: TextView = itemView.findViewById(R.id.tvAvatarName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        // item_avatar.xml ගොනුව View එකක් බවට පරිවර්තනය කිරීම (Inflation)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val currentAvatar = avatarList[position]

        // Avatar නම set කිරීම
        holder.tvAvatarName.text = currentAvatar.name

        // Lottie animation එක load කිරීම (මෙම ගොනු assets ෆෝල්ඩරයේ තිබිය යුතුය)
        holder.lottieAvatar.setAnimation(currentAvatar.animationFileName)
        holder.lottieAvatar.playAnimation()
    }

    override fun getItemCount(): Int {
        return avatarList.size
    }
}