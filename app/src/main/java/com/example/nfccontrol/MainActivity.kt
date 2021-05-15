package com.example.nfccontrol

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_lib.HostCardEmulatorService


class MainActivity : AppCompatActivity() {
    private lateinit var messageTextView: TextView
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.textMessage)
        sendButton = findViewById(R.id.button)


        val oclSendButton = View.OnClickListener {
//             work
        }

        sendButton.setOnClickListener(oclSendButton)

        messageTextView.text = IntentHandler.returnMessage(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        messageTextView.text = IntentHandler.returnMessage(intent)
    }
}

