package com.example.nfccontrol

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.nfc_lib.BaseNfcControlAdpuService
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

class NfcControlAdpuService : BaseNfcControlAdpuService() {
    companion object {
        private const val TAG = "NfcControlAdpuService"
        const val IS_FILE = "image"
        const val KEY_NAME = "message"
        const val ACTIVITY_CLASS = "activity"
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Started")

        if (intent != null) {
            val isFile = intent.getBooleanExtra(IS_FILE, false)
            val message: ByteArray = if (isFile) {
                val bytes = intent.getByteArrayExtra(KEY_NAME) ?: ByteArray(0)

                val filename = String(bytes, Charset.defaultCharset())
                val uri = Uri.parse(filename)

                val buffer = ByteArrayOutputStream()
                contentResolver.openInputStream(uri).use {
                    it?.copyTo(buffer)
                }

                buffer.toByteArray()
            } else {
                intent.getByteArrayExtra(KEY_NAME) ?: ByteArray(0)
            }

            val activityClass = intent.getSerializableExtra(ACTIVITY_CLASS) as Class<*>

            setNewState(message, activityClass)
        }

        return START_STICKY
    }


    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "OnRebind")
        super.onRebind(intent)
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        Log.d(TAG, "on processCommandAdpu")
        return super.processCommandApdu(commandApdu, extras)
    }
}
