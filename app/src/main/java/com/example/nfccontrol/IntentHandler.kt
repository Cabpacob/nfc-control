package com.example.nfccontrol

import android.content.Intent

class IntentHandler {
    companion object {
        fun extractMessage(intent: Intent?): String? {
            return intent?.data?.getQueryParameter("msg")
        }
    }
}
