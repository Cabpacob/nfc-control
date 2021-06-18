package com.example.smart_device_example

import android.graphics.Color
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_lib.NfcControlReader
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import kotlin.math.abs
import kotlin.random.Random

class SmartAppActivity : AppCompatActivity() {
    private lateinit var container: View
    private lateinit var reader: NfcControlReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_app)
        container = findViewById(R.id.smart_app_container)
        container.setOnClickListener {
            it.setBackgroundColor(randColor())
        }
        reader = NfcControlReader(object : NfcControlReader.Callback {
            override fun onNewData(data: ByteArray) {
                acceptMessage(String(data, Charset.defaultCharset()))
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
            val color = JSONObject(message).getString("color")
            container.setBackgroundColor(Color.parseColor(color))
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
