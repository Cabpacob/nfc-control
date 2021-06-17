package com.example.nfccontrol

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.nfc_lib.BaseNfcControlAdpuService
import com.example.nfc_lib.ServiceState
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

class NfcControlAdpuService : BaseNfcControlAdpuService() {
    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Started")

        if (intent != null) {
            val filename =
                String(intent.getByteArrayExtra(KEY_NAME) ?: ByteArray(0), Charset.defaultCharset())

            val isImage = intent.getBooleanExtra(IMAGE_KEY, false)
            val message: ByteArray

            if (isImage) {
                val uri = Uri.parse(filename)
//                val image = File(, uri.toString())

                val baos = ByteArrayOutputStream()
                contentResolver.openInputStream(uri).use {
                    it?.copyTo(baos)
                }

                message = baos.toByteArray()

            } else {
                message = intent.getByteArrayExtra(KEY_NAME) ?: ByteArray(0)
            }

            setNewState(
                message,
                intent.getSerializableExtra(HANDLER_KEY) as Class<*>
            )
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

    companion object {
        private const val TAG = "NfcControlAdpuService"
        const val IMAGE_KEY = "image"
        const val KEY_NAME = "message"
        const val HANDLER_KEY = "handler"
    }
}
