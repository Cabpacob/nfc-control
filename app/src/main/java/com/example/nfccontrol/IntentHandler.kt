package com.example.nfccontrol

import android.content.Intent

class IntentHandler {
    companion object {
        fun returnMessage(intent: Intent?): String {
            return intent?.data.toString()
        }
    }
}