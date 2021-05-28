package com.example.nfc_lib

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.annotation.AnyThread
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class NfcControlReader(private val callback: Callback) : NfcAdapter.ReaderCallback {
    /**
     * Callback when a new tag is discovered by the system, all the interaction with the
     * nfc device should be done here
     *
     * @param tag Discovered tag
     */
    override fun onTagDiscovered(tag: Tag?) {
        Log.i(TAG, "New tag discovered")
        // Android's Host-based Card Emulation (HCE) feature implements the ISO-DEP (ISO 14443-4)
        // protocol.
        //
        // In order to communicate with a device using HCE, the discovered tag should be processed
        // using the IsoDep class.
        val isoDep = IsoDep.get(tag)
        if (isoDep != null) {
            try {
                // Connect to the remote NFC device
                isoDep.connect()
                isoDep.timeout = 3600
                Log.i(TAG, "Timeout = " + isoDep.timeout)
                Log.i(TAG, "MaxTransceiveLength = " + isoDep.maxTransceiveLength)

                // Build SELECT AID command for our loyalty card service.
                // This command tells the remote device which service we wish to communicate with.
                Log.i(TAG, "Requesting remote AID")
                val selCommand = NFCControlAPI.buildSelectApdu()
                // Send command to remote device
                Log.i(TAG, "Sending: " + byteArrayToHexString(selCommand))
                val result = isoDep.transceive(selCommand)
                val statusWord = byteArrayOf(result[result.size - 2], result[result.size - 1])
                val payload: ByteArray = Arrays.copyOf(result, result.size - 2)
                if (hexStringToByteArray(NFCControlAPI.STATUS_SUCCESS).contentEquals(statusWord)) {
                    val resultData = String(payload, Charset.defaultCharset())
                    callback.onNewData(resultData)
                    Log.i(TAG, "Received: $resultData")
                } else {
                    Log.w(TAG, "Failed to select AID: ${byteArrayToHexString(result)}")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error communicating with card: $e")
            }
        }
    }

    interface Callback {
        @AnyThread
        fun onNewData(data: String)
    }

    companion object {
        private const val TAG = "BasicCardReader"
    }
}
