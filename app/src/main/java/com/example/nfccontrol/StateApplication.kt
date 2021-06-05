package com.example.nfccontrol

import android.app.Application

class StateApplication : Application() {
    var state: State = State.PROGRESS
}