package com.example.nfccontrol

import android.app.Application

class StateApplication : Application() {
    var message: String? = null
    var state: State = State.PROGRESS
}