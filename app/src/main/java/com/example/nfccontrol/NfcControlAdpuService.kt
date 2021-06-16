package com.example.nfccontrol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.nfc_lib.BaseNfcControlAdpuService
import com.example.nfc_lib.ServiceState

class NfcControlAdpuService : BaseNfcControlAdpuService() {
    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Started")

        if (intent != null) {
            setNewState(
                intent.getStringExtra(KEY_NAME),
                intent.getSerializableExtra(HANDLER_KEY) as ServiceState
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
        const val KEY_NAME = "message"
        const val HANDLER_KEY = "handler"
    }
}
