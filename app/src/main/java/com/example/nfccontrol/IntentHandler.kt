package com.example.nfccontrol

import android.content.Intent

class IntentHandler {
    companion object {
        fun returnMessage(intent: Intent?): String? {
            if (intent == null || intent.data == null) {
                return null
            }
            return intent.data.toString()
        }
    }
}
