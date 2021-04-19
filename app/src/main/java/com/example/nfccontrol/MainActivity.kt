package com.example.nfccontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var messageTextView: TextView
    private lateinit var sendButton: Button
    private val cardEmulator: HostCardEmulatorService = HostCardEmulatorService()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.textMessage)
        sendButton = findViewById(R.id.button)


        val oclSendButton = View.OnClickListener {
            cardEmulator.text = messageTextView.text.toString()
        }

        sendButton.setOnClickListener(oclSendButton)
    }
}

