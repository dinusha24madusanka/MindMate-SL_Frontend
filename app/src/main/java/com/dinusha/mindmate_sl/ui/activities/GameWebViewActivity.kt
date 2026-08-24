package com.dinusha.mindmate_sl.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dinusha.mindmate_sl.R
import com.google.android.material.appbar.MaterialToolbar

class GameWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    private var resultHandled = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_webview)
        val toolbar = findViewById<MaterialToolbar>(R.id.gameToolbar)
        webView = findViewById(R.id.gameWebView)
        progressBar = findViewById(R.id.gameProgressBar)
        val gameTitle = intent.getStringExtra(EXTRA_TITLE) ?: "MindMate Game"
        val gameUrl =intent.getStringExtra(EXTRA_URL) ?: "file:///android_asset/games/calm_bubbles.html"
        toolbar.title = gameTitle
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        webView.addJavascriptInterface(
            GameBridge(),
            "Android"
        )
        webView.webViewClient =
            WebViewClient()
        webView.webChromeClient =
            object : WebChromeClient() {
                override fun onProgressChanged(
                    view: WebView?,
                    newProgress: Int
                ) {
                    progressBar.progress = newProgress
                    progressBar.visibility =
                        if (newProgress < 100) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }
            }
        webView.loadUrl(gameUrl)

        onBackPressedDispatcher
            .addCallback(
                this,
                object :
                    OnBackPressedCallback(true) {

                    override fun handleOnBackPressed() {

                        showExitConfirmation()
                    }
                }
            )
    }


    inner class GameBridge {

        /*
         * Existing games:
         * Calm Bubbles etc.
         */
        @JavascriptInterface
        fun gameFinished(
            gameId: String,
            score: String,
            durationSeconds: String
        ) {

            val finalScore =
                score.toIntOrNull() ?: 0

            val finalDuration =
                durationSeconds.toIntOrNull() ?: 0

            runOnUiThread {

                handleGameFinished(
                    gameId,
                    finalScore,
                    finalDuration
                )
            }
        }        /*
         * Mandala exact progress save
         */
        @JavascriptInterface
        fun saveMandalaProgress(
            progressJson: String
        ) {

            getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )
                .edit()
                .putString(
                    MANDALA_PROGRESS_KEY,
                    progressJson
                )
                .apply()
        }


        /*
         * Mandala saved progress restore
         */
        @JavascriptInterface
        fun getMandalaProgress(): String {

            return getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )
                .getString(
                    MANDALA_PROGRESS_KEY,
                    ""
                ) ?: ""
        }


        /*
         * 120-sec Mandala session end
         *
         * status:
         * PAUSED    = level not completed
         * COMPLETED = level completed
         */
        /*
         * Reward a Mandala level immediately when it reaches 100%.
         *
         * Returns:
         * 10 = reward granted now
         * 0  = this level was already rewarded
         *
         * This does NOT close GameWebViewActivity.
         */
        @JavascriptInterface
        fun mandalaLevelCompleted(
            level: String,
            durationSeconds: String
        ): Int {

            val finalLevel =
                level.toIntOrNull()
                    ?.coerceIn(1, 6)
                    ?: 1

            val finalDuration =
                durationSeconds.toIntOrNull()
                    ?.coerceAtLeast(0)
                    ?: 0

            return awardMandalaLevelOnce(
                finalLevel,
                finalDuration
            )
        }


        @JavascriptInterface
        fun mandalaSessionEnded(
            status: String,
            level: String,
            percent: String,
            durationSeconds: String,
            points: String
        ) {

            val finalLevel =
                level.toIntOrNull()
                    ?.coerceIn(1, 6)
                    ?: 1

            val finalPercent =
                percent.toIntOrNull()
                    ?.coerceIn(0, 100)
                    ?: 0

            val finalDuration =
                durationSeconds.toIntOrNull()
                    ?.coerceAtLeast(0)
                    ?: 0

            /*
             * This value is only returned to Chat for the completion message.
             * Real MindPoints are awarded natively by mandalaLevelCompleted().
             */
            val finalPoints =
                points.toIntOrNull()
                    ?.coerceIn(0, 10)
                    ?: 0

            runOnUiThread {

                handleMandalaSessionEnded(
                    status,
                    finalLevel,
                    finalPercent,
                    finalDuration,
                    finalPoints
                )
            }
        }
    }


    private fun handleGameFinished(
        gameId: String,
        score: Int,
        durationSeconds: Int
    ) {

        if (resultHandled) return

        resultHandled = true

        saveGameCompletion(
            gameId,
            score,
            durationSeconds
        )

        addMindPoints(10)

        val resultIntent = Intent().apply {

            putExtra(
                RESULT_GAME_ID,
                gameId
            )

            putExtra(
                RESULT_GAME_SCORE,
                score
            )

            putExtra(
                RESULT_GAME_DURATION,
                durationSeconds
            )

            putExtra(
                RESULT_POINTS,
                10
            )
        }

        setResult(
            RESULT_OK,
            resultIntent
        )

        finish()
    }

    /*
     * Called when Mandala returns to Chat.
     *
     * The +10 reward is normally granted at the exact moment the level
     * reaches 100%, so this method must not blindly add points again.
     */
    private fun handleMandalaSessionEnded(
        status: String,
        level: Int,
        percent: Int,
        durationSeconds: Int,
        resultPoints: Int
    ) {

        if (resultHandled) return
        resultHandled = true

        val completed =
            status.equals(
                "COMPLETED",
                ignoreCase = true
            )

        /*
         * Defensive fallback for an older HTML file:
         * if COMPLETED returns without the immediate reward callback,
         * grant the level reward here once.
         */
        val pointsForResult =
            if (completed) {

                val alreadyRewarded =
                    getSharedPreferences(
                        "MindMatePrefs",
                        Context.MODE_PRIVATE
                    )
                        .getBoolean(
                            "MANDALA_LEVEL_${level}_REWARDED",
                            false
                        )

                if (!alreadyRewarded) {
                    awardMandalaLevelOnce(
                        level,
                        durationSeconds
                    )
                } else {
                    resultPoints
                }

            } else {
                0
            }

        /*
         * Store the last Mandala state used by Chat's dynamic suggestion.
         */
        getSharedPreferences(
            "MindMatePrefs",
            Context.MODE_PRIVATE
        )
            .edit()
            .putInt(
                "MANDALA_LAST_LEVEL",
                level
            )
            .putInt(
                "MANDALA_LAST_PERCENT",
                percent
            )
            .putString(
                "MANDALA_LAST_STATUS",
                if (completed) {
                    "COMPLETED"
                } else {
                    "PAUSED"
                }
            )
            .putLong(
                "MANDALA_LAST_SESSION_AT",
                System.currentTimeMillis()
            )
            .apply()

        val resultIntent =
            Intent().apply {

                putExtra(
                    RESULT_GAME_ID,
                    "mandala_paint_flow"
                )

                putExtra(
                    RESULT_GAME_SCORE,
                    percent
                )

                putExtra(
                    RESULT_GAME_DURATION,
                    durationSeconds
                )

                putExtra(
                    RESULT_POINTS,
                    pointsForResult
                )

                putExtra(
                    RESULT_GAME_STATUS,
                    if (completed) {
                        "COMPLETED"
                    } else {
                        "PAUSED"
                    }
                )

                putExtra(
                    RESULT_MANDALA_LEVEL,
                    level
                )

                putExtra(
                    RESULT_MANDALA_PERCENT,
                    percent
                )
            }

        setResult(
            RESULT_OK,
            resultIntent
        )

        finish()
    }


    /*
     * Grant +10 only once per Mandala level.
     *
     * The rewarded flag is saved before points are added so duplicate
     * JavaScript callbacks cannot grant the same level twice.
     */
    private fun awardMandalaLevelOnce(
        level: Int,
        durationSeconds: Int
    ): Int {

        val prefs =
            getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )

        val rewardKey =
            "MANDALA_LEVEL_${level}_REWARDED"

        if (
            prefs.getBoolean(
                rewardKey,
                false
            )
        ) {
            return 0
        }

        prefs.edit()
            .putBoolean(
                rewardKey,
                true
            )
            .putInt(
                "MANDALA_LAST_LEVEL",
                level
            )
            .putInt(
                "MANDALA_LAST_PERCENT",
                100
            )
            .putString(
                "MANDALA_LAST_STATUS",
                "COMPLETED"
            )
            .putLong(
                "MANDALA_LAST_SESSION_AT",
                System.currentTimeMillis()
            )
            .apply()

        saveGameCompletion(
            "mandala_paint_level_$level",
            100,
            durationSeconds
        )

        addMindPoints(
            MANDALA_LEVEL_REWARD
        )

        return MANDALA_LEVEL_REWARD
    }


    private fun saveGameCompletion(
        gameId: String,
        score: Int,
        durationSeconds: Int
    ) {

        val prefs =
            getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )


        prefs.edit()

            .putString(
                "LAST_GAME_ID",
                gameId
            )

            .putInt(
                "LAST_GAME_SCORE",
                score
            )

            .putInt(
                "LAST_GAME_DURATION",
                durationSeconds
            )

            .putLong(
                "LAST_GAME_COMPLETED_AT",
                System.currentTimeMillis()
            )

            .apply()
    }


    private fun addMindPoints(
        points: Int
    ) {

        val prefs =
            getSharedPreferences(
                "MindMatePrefs",
                Context.MODE_PRIVATE
            )


        val currentPoints =
            prefs.getInt(
                "MIND_POINTS",
                0
            )


        prefs.edit()
            .putInt(
                "MIND_POINTS",
                currentPoints + points
            )
            .apply()
    }


    private fun showCompletionDialog(
        score: Int
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                "Quick Reset Complete 🌿"
            )

            .setMessage(
                """
                Nice work!

                You completed a 60-second break and popped $score bubbles.

                +10 MindPoints

                How are you feeling now?
                """.trimIndent()
            )

            .setPositiveButton(
                "😊 Better"
            ) { _, _ ->

                saveFeedback(
                    "BETTER"
                )

                showFinalMessage(
                    "Glad you took a short break. You can continue when you're ready."
                )
            }

            .setNeutralButton(
                "😐 Same"
            ) { _, _ ->

                saveFeedback(
                    "SAME"
                )

                showFinalMessage(
                    "That's okay. You can try another short activity or continue chatting with MindMate."
                )
            }

            .setNegativeButton(
                "😣 Still stressed"
            ) { _, _ ->

                saveFeedback(
                    "STILL_STRESSED"
                )

                showFinalMessage(
                    "You may prefer a breathing or grounding activity next. You can also continue chatting with MindMate."
                )
            }

            .setCancelable(false)

            .show()
    }


    private fun saveFeedback(
        feedback: String
    ) {

        getSharedPreferences(
            "MindMatePrefs",
            Context.MODE_PRIVATE
        )
            .edit()

            .putString(
                "LAST_ACTIVITY_FEEDBACK",
                feedback
            )

            .apply()
    }


    private fun showFinalMessage(
        message: String
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                "MindMate"
            )

            .setMessage(
                message
            )

            .setPositiveButton(
                "Done"
            ) { _, _ ->

                finish()
            }

            .setCancelable(false)

            .show()
    }

    private fun leaveGameSafely() {

        if (resultHandled) {
            finish()
            return
        }

        val currentUrl =
            webView.url.orEmpty()

        if (
            currentUrl.contains(
                "mandala_paint_flow"
            )
        ) {

            /*
             * Ask the Mandala HTML to save its exact state
             * and return PAUSED result through the Android bridge.
             *
             * Do NOT call finish() here.
             * handleMandalaSessionEnded() will finish the Activity
             * after the result has been prepared for ChatFragment.
             */
            webView.evaluateJavascript(
                """
            (function () {

                if (
                    typeof returnPausedToMindMate === "function"
                ) {

                    returnPausedToMindMate();
                    return "HANDLED";
                }

                return "NOT_FOUND";

            })();
            """.trimIndent()
            ) { result ->

                /*
                 * Defensive fallback only.
                 * If the HTML function was unavailable,
                 * avoid trapping the user inside the activity.
                 */
                if (
                    result.contains(
                        "NOT_FOUND"
                    ) &&
                    !resultHandled
                ) {

                    runOnUiThread {
                        finish()
                    }
                }
            }

            return
        }

        // Other mini-games keep the normal exit behaviour.
        finish()
    }

    private fun showExitConfirmation() {

        /*
         * Once the result has already been handled,
         * simply close the activity.
         */
        if (resultHandled) {
            finish()
            return
        }

        AlertDialog.Builder(this)

            .setTitle(
                "Leave this activity?"
            )

            .setMessage(
                "Your progress will be saved before you leave."
            )

            .setPositiveButton(
                "Leave"
            ) { _, _ ->

                leaveGameSafely()
            }

            .setNegativeButton(
                "Keep Playing",
                null
            )

            .show()
    }
    override fun onDestroy() {
        webView.apply {
            stopLoading()
            removeJavascriptInterface(
                "Android"
            )
            clearHistory()
            removeAllViews()
            destroy()
        }
        super.onDestroy()
    }
    companion object {
        const val EXTRA_TITLE =
            "game_title"
        const val EXTRA_URL =
            "game_url"
        const val EXTRA_MANDALA_LEVEL =
            "MANDALA_LEVEL"
        const val RESULT_GAME_ID =
            "result_game_id"
        const val RESULT_GAME_SCORE =
            "result_game_score"
        const val RESULT_GAME_DURATION =
            "result_game_duration"
        const val RESULT_POINTS =
            "result_points"
        //Mandala
        const val MANDALA_PROGRESS_KEY =
            "MANDALA_PAINT_PROGRESS_JSON"
        const val RESULT_GAME_STATUS =
            "result_game_status"
        const val RESULT_MANDALA_LEVEL =
            "result_mandala_level"
        const val RESULT_MANDALA_PERCENT =
            "result_mandala_percent"
        const val MANDALA_LEVEL_REWARD =
            10
    }
}