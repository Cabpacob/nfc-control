package com.example.nfc_lib

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle


abstract class BaseNfcControlAdpuService : HostApduService() {
    private var message: String? = null
    private var position = 0
    private var sentHeader = false
    private var isReady = false
//    private var state: ServiceState? = null
    private var activityClass: Class<*>? = null

    companion object {
        private const val messageLength = 200
    }

    fun setNewState(message: String?, clazz: Class<*>) {
        position = 0
        sentHeader = false
        isReady = false
        this.message = message
        this.activityClass = clazz
    }

    override fun processCommandApdu(
        commandApdu: ByteArray?,
        extras: Bundle?
    ): ByteArray {
        if (isReady) {
            return hexStringToByteArray(NFCControlAPI.STATUS_END)
        }

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
        val textBytes = if (sentHeader) {
            position += messageLength
            message?.substring(
                position - messageLength,
                kotlin.math.min(position, message!!.length)
            )?.toByteArray(charset)
                ?: ByteArray(0)
        } else {
            (message ?: "").length.toString().toByteArray(charset)
        }

        return if (hexCommandApdu.substring(10, 24) == NFCControlAPI.AID) {
            textBytes + (if (sentHeader) {

                if (message != null && position >= message!!.length) {
                    isReady = true
                    val intentToActivity = Intent(this, activityClass)
                    intentToActivity.putExtra("status", "finished")
                    intentToActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    application.startActivity(intentToActivity)
                }
                hexStringToByteArray(NFCControlAPI.STATUS_SUCCESS)
            } else {
                sentHeader = true
                hexStringToByteArray(NFCControlAPI.STATUS_BEGIN)
            })
        } else {
            hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }
    }
}
