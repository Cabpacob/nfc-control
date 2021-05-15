package com.example.nfccontrol

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_lib.HostCardEmulatorService


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    private fun createIntent(message: String?) {
        if (message == null) {
            messageTextView.text = "Please find a QR code"
        } else {
            messageTextView.text = message

            val intentToActivity = Intent(this, HostCardEmulatorService::class.java)

            intentToActivity.putExtra(HostCardEmulatorService.KEY_NAME, message)
        }
    }

    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.textMessage)

        val message = IntentHandler.extractMessage(intent)

        createIntent(message)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val message = IntentHandler.extractMessage(intent)

        createIntent(message)
    }
}

