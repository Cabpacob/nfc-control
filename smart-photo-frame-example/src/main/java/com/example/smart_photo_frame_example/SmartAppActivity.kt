package com.example.smart_photo_frame_example

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_lib.NfcControlReader
import org.json.JSONException
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

class SmartAppActivity : AppCompatActivity() {
    private lateinit var container: View
    private lateinit var image: ImageView
    private lateinit var reader: NfcControlReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_app)
        container = findViewById(R.id.smart_app_container)
        image = findViewById(R.id.image)
        container.setOnClickListener {
            it.setBackgroundColor(randColor())
            image.setImageDrawable(null)
        }
        reader = NfcControlReader(object : NfcControlReader.Callback {
            override fun onNewData(data: ByteArray) {
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

    private fun acceptMessage(message: ByteArray) {
        try {
            val filename = "image"
            val file = File(application.obbDir, filename)
            file.parentFile!!.mkdirs()

            if (!file.exists()) {
                file.createNewFile()
            }

            val uri = Uri.fromFile(file)

            contentResolver.openOutputStream(uri).use {
                it?.write(message)
            }

            runOnUiThread {
                image.setImageDrawable(Drawable.createFromPath(file.absolutePath))
            }
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
