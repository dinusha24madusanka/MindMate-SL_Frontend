package com.dinusha.mindmate_sl.data.model

data class GameItem(
    val title: String,
    val description: String,
    val durationText: String,
    val imageResId: Int, // drawable පින්තූරය සඳහා
    val webUrl: String   // WebView එකෙන් Open වෙන්න ඕන Game Link එක
)