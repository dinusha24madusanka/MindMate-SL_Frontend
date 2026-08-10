package com.dinusha.mindmate_sl.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.dinusha.mindmate_sl.R
import com.google.android.material.appbar.MaterialToolbar

class GameWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_webview)

        val toolbar = findViewById<MaterialToolbar>(R.id.gameToolbar)
        webView = findViewById(R.id.gameWebView)
        progressBar = findViewById(R.id.gameProgressBar)

        val gameTitle =
            intent.getStringExtra(EXTRA_TITLE) ?: "MindMate Game"

        val gameUrl =
            intent.getStringExtra(EXTRA_URL)
                ?: "file:///android_asset/games/mind_reset.html"

        toolbar.title = gameTitle

        toolbar.setNavigationOnClickListener {
            finish()
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {

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

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        webView.apply {
            stopLoading()
            clearHistory()
            removeAllViews()
            destroy()
        }

        super.onDestroy()
    }

    companion object {
        const val EXTRA_TITLE = "game_title"
        const val EXTRA_URL = "game_url"
    }
}