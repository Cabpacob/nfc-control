package com.example.nfc_lib

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log


abstract class BaseNfcControlAdpuService : HostApduService() {
    companion object {
        private const val messageLength = 20000
        private const val TAG = "BaseNfcAdpuService"
    }

    private lateinit var message: List<Byte>
    private var position = 0
    private var sentHeader = false
    private var isReady = false

    private var activityClass: Class<*>? = null

    fun setNewState(message: ByteArray, clazz: Class<*>) {
        position = 0
        sentHeader = false
        isReady = false
        this.message = message.toList()
        this.activityClass = clazz
    }

    private fun getSubList(message: List<Byte>): ByteArray {
        return if (sentHeader) {
            position += messageLength
            Log.i(
                TAG,
                "Sent from ${position - messageLength} to ${
                    kotlin.math.min(
                        position,
                        message.size
                    )
                }"
            )

            message.subList(
                position - messageLength,
                kotlin.math.min(position, message.size)
            ).toByteArray()
        } else {
            message.size.toString().toByteArray()
        }
    }

    private fun sentIntentToActivity() {
        val intentToActivity = Intent(this, activityClass)
        intentToActivity.putExtra("status", "finished")
        intentToActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intentToActivity)
    }

    private fun getStatusCode(): ByteArray {
        return if (!sentHeader) {
            sentHeader = true
            hexStringToByteArray(NFCControlAPI.STATUS_BEGIN)
        } else {
            if (position >= message.size) {
                Log.i(TAG, "Message sent successfully")

                isReady = true
                sentIntentToActivity()
            }
            hexStringToByteArray(NFCControlAPI.STATUS_SUCCESS)
        }
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

        val textBytes = getSubList(message)

        Log.i(TAG, "Length ${textBytes.size}")

        return if (hexCommandApdu.substring(10, 24) == NFCControlAPI.AID) {
            Log.i(TAG, "Sent ${textBytes.size} bytes")

            textBytes + getStatusCode()
        } else {
            hexStringToByteArray(NFCControlAPI.STATUS_FAILED)
        }
    }
}
