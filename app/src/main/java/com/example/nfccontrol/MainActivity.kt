package com.example.nfccontrol

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_lib.HostCardEmulatorService


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    private fun submitMessage(message: String?) {
        if (message == null) {
            messageTextView.text = "Please find a QR code"
        } else {
            messageTextView.text = "Hold your phone to smart device"

            val intentToService = Intent(this, HostCardEmulatorService::class.java)
            Log.w("Jopa", "kk")
            intentToService.putExtra(HostCardEmulatorService.KEY_NAME, message)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.textView)

        val message = IntentHandler.extractMessage(intent)

        if (message == null) {
            val layout = findViewById<View>(R.id.root_layout)
            layout.setBackgroundResource(R.drawable.gradient_no_data_animation)
        }

        startCurrentAnimation()
        submitMessage(message)


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val message = IntentHandler.extractMessage(intent)

        submitMessage(message)
    }
}

