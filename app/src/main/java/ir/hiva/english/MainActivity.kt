package ir.hiva.english

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import android.webkit.WebView
import java.util.Locale

class MainActivity : android.app.Activity(), TextToSpeech.OnInitListener {
    private lateinit var web: WebView
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    inner class Bridge {
        @JavascriptInterface
        fun speak(text: String, queue: Boolean) {
            if (!ttsReady) return
            tts?.speak(text, if (queue) TextToSpeech.QUEUE_ADD else TextToSpeech.QUEUE_FLUSH, null, "u")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.addJavascriptInterface(Bridge(), "Android")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setSpeechRate(0.85f)
            ttsReady = true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        web.evaluateJavascript("window.hwBack && window.hwBack()") { r ->
            if (r != "true") finish()
        }
    }

    override fun onDestroy() {
        tts?.stop(); tts?.shutdown()
        super.onDestroy()
    }
}
