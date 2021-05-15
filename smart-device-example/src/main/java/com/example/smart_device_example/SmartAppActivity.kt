package com.example.smart_device_example

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextUInt

class SmartAppActivity : AppCompatActivity() {
    lateinit var container: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_app)
        container = findViewById(R.id.smart_app_container)
        container.setOnClickListener {
            it.setBackgroundColor(randColor())
        }
    }

    @ColorInt
    private fun randColor(): Int {
        val red = abs(Random.nextInt() % 255)
        val green = abs(Random.nextInt() % 255)
        val blue = abs(Random.nextInt() % 255)
        return Color.rgb(red, green, blue)
    }
}
