package com.dinusha.mindmate_sl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val btnStartChatting = view.findViewById<MaterialButton>(R.id.btnStartChatting)


        btnStartChatting.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChatFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}