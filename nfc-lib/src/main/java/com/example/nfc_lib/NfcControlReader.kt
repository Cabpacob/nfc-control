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
    companion object {
        private const val TAG = "BasicCardReader"
    }

    /**
     * Callback when a new tag is discovered by the system, all the interaction with the
     * nfc device should be done here
     *
     * @param tag Discovered tag
     */
    private var message: ByteArray = ByteArray(0)
    private var expectedLength = -1

    private fun handleResult(payload: ByteArray, statusWord: ByteArray) {
        when {
            hexStringToByteArray(NFCControlAPI.STATUS_BEGIN).contentEquals(statusWord) -> {
                val resultData = String(payload, Charset.defaultCharset()).toInt()
                expectedLength = resultData
                message = ByteArray(0)
                Log.i(TAG, "New request on $resultData bytes")
            }

            hexStringToByteArray(NFCControlAPI.STATUS_SUCCESS).contentEquals(statusWord) -> {
                message += payload
                if (message.size == expectedLength) {
                    callback.onNewData(message)
                }

                Log.i(TAG, "Need $expectedLength")
                Log.i(TAG, "Have ${message.size}")
                Log.i(TAG, "Received: ${payload.size}")
            }

            else -> {
                Log.w(TAG, "Unknown sent status: ${byteArrayToHexString(statusWord)}")
            }
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.i(TAG, "New tag discovered")
        val isoDep = IsoDep.get(tag)
        isoDep?.use {
            try {
                Log.i(TAG, "Connect")
                isoDep.connect()
                isoDep.timeout = 5000
                Log.i(TAG, "Timeout = " + isoDep.timeout)
                Log.i(TAG, "MaxTransceiveLength = " + isoDep.maxTransceiveLength)

                Log.i(TAG, "Requesting remote AID")
                val selCommandSuccess = NFCControlAPI.buildSelectApduSuccess()

                Log.i(TAG, "Sending: " + byteArrayToHexString(selCommandSuccess))
                var result = isoDep.transceive(selCommandSuccess)
                var statusWord = byteArrayOf(result[result.size - 2], result[result.size - 1])
                var payload: ByteArray = Arrays.copyOf(result, result.size - 2)

                while (!statusWord.contentEquals(hexStringToByteArray(NFCControlAPI.STATUS_END))) {
                    Log.i(TAG, "Received ${payload.size} bytes with status $statusWord")

                    handleResult(payload, statusWord)

                    result = isoDep.transceive(selCommandSuccess)
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
        fun onNewData(data: ByteArray)
    }
}
