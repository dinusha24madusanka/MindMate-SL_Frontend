package com.dinusha.mindmate_sl.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dinusha.mindmate_sl.ui.main.MainActivity
import com.dinusha.mindmate_sl.R
import com.dinusha.mindmate_sl.ui.avatar.ChooseRobotActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // තත්පර 5ක් Loading Screen එක පෙන්වා ඊළඟ Screen එකට මාරු වීම
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("MindMatePrefs", Context.MODE_PRIVATE)
            val isFirstTime = sharedPreferences.getBoolean("IS_FIRST_TIME", true)

            val intent = if (isFirstTime) {
                // පළමු වතාව නම් රොබෝ තෝරන Screen එකට යවනවා
                Intent(this, ChooseRobotActivity::class.java)
            } else {
                // පළමු වතාව නෙවෙයි නම් කෙලින්ම Home Screen එකට යවනවා
                Intent(this, MainActivity::class.java)
            }
            
            startActivity(intent)
            finish()
        }, 5000)
    }
}
