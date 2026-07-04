package com.dinusha.mindmate_sl.UI.avatar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.MainActivity // ඔයාගේ Main Activity එක

class ChooseRobotActivity : AppCompatActivity() {

    private lateinit var viewPagerAvatars: ViewPager2
    private lateinit var btnConfirmSelection: Button
    private lateinit var avatarList: List<Avatar>
    private lateinit var dotsIndicator: com.tbuonomo.viewpagerdotsindicator.DotsIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_robot)

        viewPagerAvatars = findViewById(R.id.viewPagerAvatars)
        dotsIndicator = findViewById(R.id.dotsIndicator)
        btnConfirmSelection = findViewById(R.id.btnConfirmSelection)

        // 1. Gizmo, Astro, Neo අවතාර් 3 එකතු කිරීම
        // අනිවාර්යයෙන්ම "Gizmo Robot.json", "Astro Robot.json", "Neo Robot.json" ගොනු assets ෆෝල්ඩරයේ තිබිය යුතුය.
        avatarList = listOf(
            Avatar("bot_gizmo", "Gizmo", "Gizmo Robot.json"),
            Avatar("bot_astro", "Astro", "Astro Robot.json"),
            Avatar("bot_neo", "Neo", "Neo Robot.json")
        )

        val adapter = AvatarAdapter(avatarList)
        viewPagerAvatars.adapter = adapter

        // 2. ViewPager එක සහ DotsIndicator එක සම්බන්ධ කිරීම
        dotsIndicator.attachTo(viewPagerAvatars)

        // 3. Avatar එක තේරීම
        btnConfirmSelection.setOnClickListener {
            val selectedAvatar = avatarList[viewPagerAvatars.currentItem]

            // Avatar එක සහ First Time Login එක මතක තබා ගැනීම
            saveInitialSetupData(selectedAvatar.id)

            Toast.makeText(this, "${selectedAvatar.name} Selected!", Toast.LENGTH_SHORT).show()

            // Home/Main Screen එකට යැවීම
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // ආපහු එන්න බැරි වෙන්න මේ Activity එක close කරනවා
        }
    }

    private fun saveInitialSetupData(avatarId: String) {
        val sharedPreferences = getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("SELECTED_AVATAR_ID", avatarId)
        editor.putBoolean("IS_FIRST_TIME", false) // මින්පසු පළමු වතාව නොවේ!
        editor.apply()
    }
}