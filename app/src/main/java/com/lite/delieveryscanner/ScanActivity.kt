package com.lite.delieveryscanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class ScanActivity : Activity(){

    private lateinit var surfaceView: SurfaceView
    private lateinit var cameraSource : CameraSource
    private val MY_CAMERA_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_layout)
        surfaceView = findViewById(R.id.surface)
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        if(!isLandScape(displayMetrics.widthPixels, displayMetrics.heightPixels)){
            width = getPreviewWidth(displayMetrics.heightPixels)
        }else{
            height = getPreviewHeight(displayMetrics.widthPixels)
        }
        var barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(width, height).setAutoFocusEnabled(true).build()
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback{

            override fun surfaceChanged(surface: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions( Array(1){Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE)
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
                    setResult(RESULT_OK, resultIntent)
                    surfaceView.holder.surface.release()
                    Log.d("Saket",sparseArray.valueAt(0).displayValue)
                    finish()
                }
            }
        })
    }

    private fun isLandScape(width : Int, height : Int): Boolean{
        if(width < height)
            return false
        return true
    }

    private fun getPreviewWidth(height : Int): Int{
        val width = height*(16.0f/9.0f)
        return width.toInt()
    }

    private fun getPreviewHeight(width : Int): Int{
        val height = width*(16.0f/9.0f)
        return height.toInt()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            cameraSource.start()
        }
    }
}