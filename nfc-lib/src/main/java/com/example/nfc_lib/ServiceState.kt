package com.example.nfc_lib

import java.io.Serializable
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

class ServiceState : Serializable {
    val lock = ReentrantLock()
    val finishedCondition: Condition = lock.newCondition()
    var isFinished = false
}