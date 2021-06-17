package com.example.smart_photo_frame_example

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_lib.NfcControlReader
import com.example.smart_photo_frame_example.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.OutputStream
import java.io.Writer
import kotlin.math.abs
import kotlin.random.Random

class SmartAppActivity : AppCompatActivity() {
    lateinit var container: View
    lateinit var reader: NfcControlReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_app)
        container = findViewById(R.id.smart_app_container)
        container.setOnClickListener {
            it.setBackgroundColor(randColor())
        }
        reader = NfcControlReader(object : NfcControlReader.Callback {
            override fun onNewData(data: String) {
                acceptMessage(data)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        enableReaderMode()
    }

    override fun onPause() {
        super.onPause()
        disableReaderMode()
    }

    @ColorInt
    private fun randColor(): Int {
        val red = abs(Random.nextInt() % 255)
        val green = abs(Random.nextInt() % 255)
        val blue = abs(Random.nextInt() % 255)
        return Color.rgb(red, green, blue)
    }

    private fun acceptMessage(message: String) {
        try {
//            val color = JSONObject(message).getString("color")
//            container.setBackgroundColor(Color.parseColor(color))
            val filename = "image.jpg"
            val file = File(application.filesDir, filename)
            if (!file.exists()) {
                file.createNewFile()
            }

            file.printWriter().use { out ->
                out.println(message.toByteArray())
            }
            container.background = Drawable.createFromPath(file.absolutePath)
        } catch (e: JSONException) {
            // Do nothing
        }
    }

    private fun enableReaderMode() {
        val nfc = NfcAdapter.getDefaultAdapter(this)
        nfc?.enableReaderMode(this, reader, READER_FLAGS, null)
    }

    private fun disableReaderMode() {
        val nfc = NfcAdapter.getDefaultAdapter(this)
        nfc?.disableReaderMode(this)
    }

    companion object {
        var READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
    }
}
