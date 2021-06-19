package com.example.nfccontrol

import android.content.Intent

class IntentHandler {
    companion object {
        fun extractMessageFromDeepLink(intent: Intent?): String? {
            return intent?.data?.getQueryParameter("msg")
        }
    }
}
