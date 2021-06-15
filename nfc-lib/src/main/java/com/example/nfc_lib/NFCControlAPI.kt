package com.example.nfc_lib

object NFCControlAPI {
    const val STATUS_BEGIN = "9999"
    const val STATUS_END = "1337"

    // "OK" status word sent in response to SELECT AID command (0x9000)
    const val STATUS_SUCCESS = "9000"

    const val STATUS_FAILED = "6F00"
    const val CLA_NOT_SUPPORTED = "6E00"
    const val INS_NOT_SUPPORTED = "6D00"
    const val AID = "F0301111111100"

    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    const val SELECT_INS = "A4"
    const val DEFAULT_CLA = "00"
    const val SELECT_ADPU_PARAMS = "0400"
    const val SELECT_ADPU_HEADER = DEFAULT_CLA + SELECT_INS + SELECT_ADPU_PARAMS

    const val MIN_APDU_LENGTH = 12

    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    fun buildSelectApdu(): ByteArray {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return hexStringToByteArray(
            SELECT_ADPU_HEADER + String.format("%02X", AID.length / 2) + AID
        )
    }
}
