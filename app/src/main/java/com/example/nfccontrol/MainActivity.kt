package com.example.nfccontrol

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_lib.ServiceState
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import kotlin.concurrent.withLock


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    private fun submitMessage(message: String?, isImage: Boolean) {
        if (message != null) {
            val intentToService = Intent(this, NfcControlAdpuService::class.java)

            if (!isImage) {
                intentToService.putExtra(NfcControlAdpuService.KEY_NAME, message)
            } else {
                val image = File(message)
                intentToService.putExtra(NfcControlAdpuService.KEY_NAME, image.readBytes())
            }
            intentToService.putExtra(NfcControlAdpuService.HANDLER_KEY, MainActivity::class.java)
            startService(intentToService)
        }
    }

    private lateinit var messageTextView: TextView

    private fun startCurrentAnimation() {
        val animDrawable = findViewById<View>(R.id.root_layout).background as AnimationDrawable

//        animDrawable.setEnterFadeDuration(10)
//        animDrawable.setExitFadeDuration(5000)

        animDrawable.start()
    }

    private fun updateAnimation(message: String?, isFinished: Boolean = false) {
        val application = application as StateApplication
        when {
            isFinished -> {
                application.state = State.FINISHED
            }
            message == null -> {
                application.state = State.NO_DATA
            }
            else -> {
                application.state = State.PROGRESS
            }
        }

        findViewById<View>(R.id.root_layout).setBackgroundResource(application.state.animation)
        messageTextView.text = application.state.messageToUser
        startCurrentAnimation()
    }

    private fun getMessage(intent: Intent? = null): String? {
        val message = IntentHandler.extractMessage(intent)
        val application = application as StateApplication
        if (message != null) {
            application.message = message
        }

        return application.message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.textView)

//        val message = getMessage()
//
//        updateAnimation(message)
//        submitMessage(message)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val status = intent?.extras?.get("status")

        if (status == "finished") {
            updateAnimation(null, true)
        } else {
            val application = application as StateApplication
            val message: String? = if (intent?.type?.startsWith("image/") == true) {
                application.isImage = true
                intent.data.toString()
            } else {
                application.isImage = false
                getMessage(intent)
            }
            updateAnimation(message)
            submitMessage(message, application.isImage == true)
        }
    }

    override fun onResume() {
        super.onResume()

        val message = getMessage()

        val application = application as StateApplication
        if (application.state != State.FINISHED) {
            updateAnimation(message)
            submitMessage(message, application.isImage == true)
        }
    }
}

