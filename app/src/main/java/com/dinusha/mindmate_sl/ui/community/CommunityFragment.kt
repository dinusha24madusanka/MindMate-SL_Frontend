package com.dinusha.mindmate_sl.ui.community

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dinusha.mindmate_sl.R

class CommunityFragment : Fragment(R.layout.fragment_community) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAcademic = view.findViewById<Button>(R.id.btnAcademicCircle)
        val btnWellbeing = view.findViewById<Button>(R.id.btnWellbeingCircle)
        val btnMotivation = view.findViewById<Button>(R.id.btnMotivationCircle)

        btnAcademic.setOnClickListener {
            showComingSoon("Academic Support Circle")
        }

        btnWellbeing.setOnClickListener {
            showComingSoon("Well-being Circle")
        }

        btnMotivation.setOnClickListener {
            showComingSoon("Motivation Circle")
        }
    }

    private fun showComingSoon(circleName: String) {
        Toast.makeText(
            requireContext(),
            "$circleName will be enabled after secure community integration.",
            Toast.LENGTH_SHORT
        ).show()
    }
}