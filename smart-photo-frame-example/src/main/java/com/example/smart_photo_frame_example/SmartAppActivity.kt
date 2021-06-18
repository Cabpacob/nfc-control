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
import androidx.core.view.drawToBitmap
import com.example.nfc_lib.NfcControlReader
import com.example.smart_photo_frame_example.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.OutputStream
import java.io.Writer
import java.nio.charset.Charset
import kotlin.math.abs
import kotlin.random.Random

class SmartAppActivity : AppCompatActivity() {
    lateinit var container: View
    lateinit var image: ImageView
    lateinit var reader: NfcControlReader

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
//            val color = JSONObject(message).getString("color")
//            container.setBackgroundColor(Color.parseColor(color))
            val filename = "image"
            val file = File(application.obbDir, filename)
            file.parentFile.mkdirs()
            val flag1 = file.exists()
            val flag2 = file.canRead()
            val flag3 = file.canWrite()

            if (!file.exists()) {
                val flag4 = file.createNewFile()
            }

            val flag5 = file.exists()
            val flag6 = file.canRead()
            val flag7 = file.canWrite()
            val uri = Uri.fromFile(file)

            contentResolver.openOutputStream(uri).use {
                it?.write(message)
            }

//            file.printWriter().use { out ->
//                out.println(message.toByteArray())
//            }
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
