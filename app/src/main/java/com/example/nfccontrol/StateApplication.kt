package com.example.nfccontrol

import android.app.Application
import android.content.Intent
import android.util.Log

class StateApplication : Application() {
    companion object {
        private const val TAG = "StateApplication"
    }

    private var message: String? = null
    private var isFile: Boolean? = null
    var state: State = State.PROGRESS
    lateinit var stateListener: Runnable

    fun sendMessage() {
        if (message != null) {
            Log.i(TAG, "Send message $message")
            val intentToService = Intent(this, NfcControlAdpuService::class.java)

            intentToService.putExtra(NfcControlAdpuService.KEY_NAME, message)
            intentToService.putExtra(NfcControlAdpuService.IS_FILE, isFile)
            intentToService.putExtra(NfcControlAdpuService.ACTIVITY_CLASS, MainActivity::class.java)

            startService(intentToService)
        }
    }

    private fun getMessage(intent: Intent?): String? {
        return IntentHandler.extractMessageFromDeepLink(intent)
    }

    fun handleIntent(intent: Intent?) {
        val status = intent?.extras?.get("status")

        if (status == "finished") {
            state = State.FINISHED
        } else {
            if (intent?.type?.startsWith("image/") == true) {
                isFile = true
                message = intent.data.toString()
            } else {
                isFile = false
                message = getMessage(intent)
            }

            stateListener.run()
        }
    }
}