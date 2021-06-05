package com.example.nfc_lib

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

abstract class BaseNfcControlAdpuService : HostApduService() {
    private var message: String? = null
    private var position = 0

    companion object {
        private const val messageLength = 200
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    override fun processCommandApdu(
        commandApdu: ByteArray?,
        extras: Bundle?
    ): ByteArray {
        if (commandApdu == null) {
            return hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }

        val hexCommandApdu = byteArrayToHexString(commandApdu)
        if (hexCommandApdu.length < NFCControlAPI.MIN_APDU_LENGTH) {
            return hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != NFCControlAPI.DEFAULT_CLA) {
            return hexStringToByteArray(NFCControlAPI.CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != NFCControlAPI.SELECT_INS) {
            return hexStringToByteArray(NFCControlAPI.INS_NOT_SUPPORTED)
        }

        val charset = Charsets.UTF_8
        val textBytes = message?.substring(position, position + messageLength)?.toByteArray(charset)
            ?: ByteArray(0)

        position += messageLength

        return if (hexCommandApdu.substring(10, 24) == NFCControlAPI.AID) {
            textBytes + hexStringToByteArray(NFCControlAPI.STATUS_SUCCESS)
        } else {
            hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }
    }
}
