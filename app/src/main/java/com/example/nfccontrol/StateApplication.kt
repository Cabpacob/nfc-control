package com.example.nfccontrol

import android.app.Application

class StateApplication : Application() {
    var message: String? = null
    @Volatile var state: State = State.PROGRESS
    var isImage: Boolean? = null
}