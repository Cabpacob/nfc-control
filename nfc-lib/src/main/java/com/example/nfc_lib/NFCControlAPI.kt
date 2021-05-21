package com.example.nfc_lib

object NFCControlAPI {
    const val STATUS_SUCCESS = "9000"
    const val STATUS_FAILED = "6F00"
    const val CLA_NOT_SUPPORTED = "6E00"
    const val INS_NOT_SUPPORTED = "6D00"
    const val AID = "F0391111111100"
    const val SELECT_INS = "A4"
    const val DEFAULT_CLA = "00"
    const val MIN_APDU_LENGTH = 12
}
