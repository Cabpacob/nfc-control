package com.example.nfccontrol

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    private fun submitMessage(message: String?) {
        if (message == null) {
            messageTextView.text = "Please find a QR code"
        } else {
            messageTextView.text = "Hold your phone to smart device"

            val intentToService = Intent(this, NfcControlAdpuService::class.java)
            intentToService.putExtra(NfcControlAdpuService.KEY_NAME, message)
            startService(intentToService)
        }
    }

    private lateinit var messageTextView: TextView

    private fun startCurrentAnimation() {
        val animDrawable = findViewById<View>(R.id.root_layout).background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()
    }

    private fun updateAnimation(message: String?) {
        val application = application as StateApplication
        if (message == null) {
            application.state = State.NO_DATA
        } else {
            application.state = State.PROGRESS
        }

        findViewById<View>(R.id.root_layout).setBackgroundResource(application.state.animation)
        startCurrentAnimation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.textView)

        val message = IntentHandler.extractMessage(intent)
        updateAnimation(message)

        submitMessage(message)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val message = IntentHandler.extractMessage(intent)
        updateAnimation(message)

        submitMessage(message)
    }
}

