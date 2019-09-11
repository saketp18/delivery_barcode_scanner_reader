package com.lite.delieveryscanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class ScanActivity : AppCompatActivity(){

    private lateinit var surfaceView: SurfaceView
    private lateinit var cameraSource : CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_layout)
        surfaceView = findViewById(R.id.surface)
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(displayMetrics.widthPixels, displayMetrics.heightPixels).setAutoFocusEnabled(true).build()
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceChanged(surface: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

                try {
                    cameraSource.start(surface)
                }catch (e: IOException){

                }
            }

            override fun surfaceDestroyed(surface: SurfaceHolder?) {
            }

            override fun surfaceCreated(surface: SurfaceHolder?) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{

            override fun release() {
            }

            override fun receiveDetections(detector: Detector.Detections<Barcode>?) {
                var sparseArray : SparseArray<Barcode> = detector!!.detectedItems

                if(sparseArray.size() > 0){
                    var resultIntent = Intent()
                    resultIntent.putExtra("model", sparseArray.valueAt(0).displayValue)
                    setResult(Activity.RESULT_OK, resultIntent)
                    surfaceView.holder.surface.release()
                    Log.d("Saket",sparseArray.valueAt(0).displayValue)
                    finish()
                }
            }
        })
    }
}