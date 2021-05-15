package com.example.nfc_lib

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class HostCardEmulatorService : HostApduService() {
    companion object {
        var text: String? = null

        const val TAG = "Host Card Emulator"
        const val STATUS_SUCCESS = "9000"
        const val STATUS_FAILED = "6F00"
        const val CLA_NOT_SUPPORTED = "6E00"
        const val INS_NOT_SUPPORTED = "6D00"
        const val AID = "F2323232323232"
        const val SELECT_INS = "A4"
        const val DEFAULT_CLA = "00"
        const val MIN_APDU_LENGTH = 12
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
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
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex(commandApdu)
        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        val charset = Charsets.UTF_8
        val textBytes = text!!.toByteArray(charset)

        return if (hexCommandApdu.substring(10, 24) == AID) {
            textBytes + Utils.hexStringToByteArray(STATUS_SUCCESS)
        } else {
            Utils.hexStringToByteArray(STATUS_FAILED)
        }
    }
}