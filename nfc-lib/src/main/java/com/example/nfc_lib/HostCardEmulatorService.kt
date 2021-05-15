package com.example.nfc_lib

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class HostCardEmulatorService : HostApduService() {
    companion object {
        private const val TAG = "HostCardEmulatorService"
        const val KEY_NAME = "message"
    }

    private var message: String? = null

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Started")

        if (intent != null) {
            message = intent.getStringExtra(KEY_NAME)
        }

        return START_STICKY
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)

        Log.d(TAG, "OnRebind")
    }

    private class Utils {
        companion object {
            private const val HEX_CHARS = "0123456789ABCDEF"
            fun hexStringToByteArray(data: String): ByteArray {

                val result = ByteArray(data.length / 2)

                for (i in data.indices step 2) {
                    val firstIndex = HEX_CHARS.indexOf(data[i])
                    val secondIndex = HEX_CHARS.indexOf(data[i + 1])

                    val octet = firstIndex.shl(4).or(secondIndex)
                    result[i.shr(1)] = octet.toByte()
                }

                return result
            }

            private val HEX_CHARS_ARRAY = "0123456789ABCDEF".toCharArray()
            fun toHex(byteArray: ByteArray): String {
                val result = StringBuffer()

                byteArray.forEach {
                    val octet = it.toInt()
                    val firstIndex = (octet and 0xF0).ushr(4)
                    val secondIndex = octet and 0x0F
                    result.append(HEX_CHARS_ARRAY[firstIndex])
                    result.append(HEX_CHARS_ARRAY[secondIndex])
                }

                return result.toString()
            }
        }
    }

    override fun processCommandApdu(
        commandApdu: ByteArray?,
        extras: Bundle?
    ): ByteArray {
        if (commandApdu == null) {
            return Utils.hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex(commandApdu)
        if (hexCommandApdu.length < NFCControlAPI.MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != NFCControlAPI.DEFAULT_CLA) {
            return Utils.hexStringToByteArray(NFCControlAPI.CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != NFCControlAPI.SELECT_INS) {
            return Utils.hexStringToByteArray(NFCControlAPI.INS_NOT_SUPPORTED)
        }

        val charset = Charsets.UTF_8
        val textBytes = message!!.toByteArray(charset)

        return if (hexCommandApdu.substring(10, 24) == NFCControlAPI.AID) {
            textBytes + Utils.hexStringToByteArray(NFCControlAPI.STATUS_SUCCESS)
        } else {
            Utils.hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }
    }
}
