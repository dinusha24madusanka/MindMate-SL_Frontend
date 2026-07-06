package com.dinusha.mindmate_sl.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.data.model.ExerciseItem

class ExercisesAdapter(
    private val exerciseList: List<ExerciseItem>,
    private val onExerciseClick: (ExerciseItem) -> Unit
) : RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivExerciseIcon: ImageView = itemView.findViewById(R.id.ivExerciseIcon)
        val tvExerciseTitle: TextView = itemView.findViewById(R.id.tvExerciseTitle)
        val tvExerciseDesc: TextView = itemView.findViewById(R.id.tvExerciseDesc)
        val tvExerciseDuration: TextView = itemView.findViewById(R.id.tvExerciseDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.tvExerciseTitle.text = exercise.title
        holder.tvExerciseDesc.text = exercise.description
        holder.tvExerciseDuration.text = exercise.durationText
        holder.ivExerciseIcon.setImageResource(exercise.iconResId)

        holder.itemView.setOnClickListener {
            onExerciseClick(exercise)
        }
    }

    override fun getItemCount(): Int = exerciseList.size
}
