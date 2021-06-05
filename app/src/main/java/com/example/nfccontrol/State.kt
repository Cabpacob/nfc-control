package com.example.nfccontrol

enum class State(val stage: String, val animation: Int) {
    PROGRESS("PROGRESS", R.drawable.gradient_progress_animation),
    NO_DATA("NO_DATA", R.drawable.gradient_no_data_animation),
    FINISHED("FINISHED", R.drawable.gradient_finished_animation)
}