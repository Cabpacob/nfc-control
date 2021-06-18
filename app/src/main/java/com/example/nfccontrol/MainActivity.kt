package com.example.nfccontrol

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.function.BiConsumer


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var stateApplication: StateApplication
    private lateinit var messageTextView: TextView

    private fun startCurrentAnimation() {
        findViewById<View>(R.id.root_layout).setBackgroundResource(stateApplication.state.animation)
        messageTextView.text = stateApplication.state.messageToUser

        val animDrawable = findViewById<View>(R.id.root_layout).background as AnimationDrawable

        animDrawable.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "OnCreate")
        stateApplication = application as StateApplication
        setContentView(R.layout.activity_main)
        messageTextView = findViewById(R.id.textView)

        stateApplication.stateListener = Runnable {
            startCurrentAnimation()
        }

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i(TAG, "OnNewIntent")

        stateApplication = application as StateApplication
        stateApplication.handleIntent(intent)

        stateApplication.sendMessage()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "OnResume")

        stateApplication = application as StateApplication
    }
}

