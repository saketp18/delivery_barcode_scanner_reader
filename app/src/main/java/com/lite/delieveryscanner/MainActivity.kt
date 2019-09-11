package com.lite.delieveryscanner

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var name : EditText
    private lateinit var phn : EditText
    private lateinit var generate : Button
    private lateinit var scanbtn : Button
    private lateinit var qrimage : ImageView
    private val REQUESTCODE = 101
    private val NAME_TAG = "name"
    private val PHONE_TAG = "phone"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        name = findViewById(R.id.editText)
        phn = findViewById(R.id.editText2)
        generate = findViewById(R.id.generate)
        scanbtn = findViewById(R.id.button2)
        qrimage = findViewById(R.id.imageView)
        generate.setOnClickListener{
            val model = Model(name.text.toString(), phn.text.toString())
            generate(model)
        }
        scanbtn.setOnClickListener{
            scanQRCode()
        }
    }

    private fun generate(model: Model){
        var json = JSONObject()
        json.put(NAME_TAG, model.name)
        json.put(PHONE_TAG, model.phone)
        val text :String = json.toString()
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 1000, 1000)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qrimage.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun scanQRCode(){
        intent = Intent(this@MainActivity, ScanActivity::class.java)
        startActivityForResult(intent, REQUESTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==  REQUESTCODE && resultCode == Activity.RESULT_OK){

            var response = data?.let { it.getStringExtra("model") }

            var json : JSONObject? = JSONObject(response)
            json?.let {
                var nameresult = it.getString(NAME_TAG)
                var phoneresult = it.getString(PHONE_TAG)
                name.setText(nameresult)
                phn.setText(phoneresult)
                Toast.makeText(applicationContext, "Result Updated", Toast.LENGTH_LONG).show()
            }
            Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
        }
    }
}
