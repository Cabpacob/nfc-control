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
    private var message: String = ""
    private var expectedLength = -1

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
                var result = isoDep.transceive(selCommand)
                var statusWord = byteArrayOf(result[result.size - 2], result[result.size - 1])
                var payload: ByteArray = Arrays.copyOf(result, result.size - 2)
                while (!statusWord.contentEquals(hexStringToByteArray(NFCControlAPI.STATUS_END))) {
                    when {
                        hexStringToByteArray(NFCControlAPI.STATUS_BEGIN).contentEquals(statusWord) -> {
                            val resultData = String(payload, Charset.defaultCharset()).toInt()
                            expectedLength = resultData
                            message = ""
                            Log.i(TAG, "New request on $resultData bytes")
                        }
                        hexStringToByteArray(NFCControlAPI.STATUS_SUCCESS).contentEquals(statusWord) -> {
                            val resultData = String(payload, Charset.defaultCharset())
                            message += resultData
                            if (message.length == expectedLength) {
                                callback.onNewData(resultData)
                            }
                            Log.i(TAG, "Received: $resultData")
                        }
                        else -> {
                            Log.w(TAG, "Failed to select AID: ${byteArrayToHexString(result)}")
                        }
                    }
                    result = isoDep.transceive(selCommand)
                    statusWord = byteArrayOf(result[result.size - 2], result[result.size - 1])
                    payload = Arrays.copyOf(result, result.size - 2)
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
