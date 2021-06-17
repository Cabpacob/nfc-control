package com.example.nfccontrol

enum class State(val stage: String, val animation: Int, val messageToUser: String) {
    PROGRESS("PROGRESS", R.drawable.gradient_progress_animation, "Hold your phone to smart device"),
    NO_DATA("NO_DATA", R.drawable.gradient_no_data_animation, "Please find a QR code"),
    FINISHED("FINISHED", R.drawable.gradient_finished_animation, "Data sent")
}