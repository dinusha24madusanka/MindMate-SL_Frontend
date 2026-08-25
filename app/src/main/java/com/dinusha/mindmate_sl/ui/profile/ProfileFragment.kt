package com.dinusha.mindmate_sl.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.ui.avatar.ChooseRobotActivity

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var ivProfileAvatar: ImageView
    private lateinit var tvAvatarName: TextView
    private lateinit var tvMindPoints: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar)
        tvAvatarName = view.findViewById(R.id.tvAvatarName)
        tvMindPoints = view.findViewById(R.id.tvMindPoints)

        val btnChangeCompanion =
            view.findViewById<Button>(R.id.btnChangeCompanion)

        val btnClearLocalData =
            view.findViewById<Button>(R.id.btnClearLocalData)

        loadProfileData()

        btnChangeCompanion.setOnClickListener {

            val intent =
                Intent(requireContext(), ChooseRobotActivity::class.java)

            startActivity(intent)
        }

        btnClearLocalData.setOnClickListener {
            showClearDataDialog()
        }
    }

    override fun onResume() {
        super.onResume()

        if (this::ivProfileAvatar.isInitialized) {
            loadProfileData()
        }
    }

    private fun loadProfileData() {

        val prefs = requireContext()
            .getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )

        val selectedAvatarId =
            prefs.getString(
                "SELECTED_AVATAR_ID",
                "bot_gizmo"
            ) ?: "bot_gizmo"

        val points =
            prefs.getInt(
                "MIND_POINTS",
                0
            )

        when (selectedAvatarId) {

            "bot_astro" -> {
                ivProfileAvatar.setImageResource(
                    R.drawable.ic_astro_icon
                )

                tvAvatarName.text =
                    "Astro"
            }

            "bot_neo" -> {
                ivProfileAvatar.setImageResource(
                    R.drawable.ic_neo_icon
                )

                tvAvatarName.text =
                    "Neo"
            }

            else -> {
                ivProfileAvatar.setImageResource(
                    R.drawable.ic_gizmo_icon
                )

                tvAvatarName.text =
                    "Gizmo"
            }
        }

        tvMindPoints.text =
            "$points pts"
    }

    private fun showClearDataDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Clear local data?")
            .setMessage(
                "This will remove locally stored MindMate preferences and progress from this device."
            )
            .setPositiveButton("Clear") { _, _ ->

                val context = requireContext()

                context
                    .getSharedPreferences(
                        "MindMatePrefs",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .clear()
                    .apply()

                context
                    .getSharedPreferences(
                        "mindmate_journey",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .clear()
                    .apply()

                Toast.makeText(
                    context,
                    "Local MindMate data cleared.",
                    Toast.LENGTH_SHORT
                ).show()

                loadProfileData()
            }
    }
}