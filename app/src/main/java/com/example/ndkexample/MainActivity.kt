package com.example.ndkexample

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cheungbh.net.BodyPart
import cheungbh.net.KeyPoint
import cheungbh.net.Person
import com.cheungbh.yogasdk.di.Injector
import com.cheungbh.yogasdk.view.CameraView
import com.cheungbh.yogasdk.view.OverlayView

class MainActivity : AppCompatActivity() {
    private lateinit var cameraView: CameraView
    private lateinit var overlayView: OverlayView
    private val ncnn = MobilenetSSDNcnn()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        Injector.setNetLibrary(applicationContext)
        //val ncnn = MobilenetSSDNcnn()
        //ncnn.Init(assets)
        val ret_init: Boolean = ncnn.Init(assets)
        if (!ret_init) {
            Log.e("MainActivity", "mobilenetssdncnn Init failed")
        }


        setContentView(R.layout.activity_main)
        overlayView = findViewById(R.id.resultView)
        cameraView = findViewById<CameraView>(R.id.cameraView).apply {
            setEventListener(object : CameraView.EventListener {
                override fun onImageAvailable(image: Image) {
                    val resultBitmap: Bitmap = Injector.getProcessImg().process(
                        image,
                        cameraView.getImgReaderSize(),
                        cameraView.getSensorOrientation()!!,
//                            activity!!.windowManager.defaultDisplay.rotation

                        this@MainActivity.windowManager.defaultDisplay.rotation
                    )

                    var objects: Array<MobilenetSSDNcnn.KeyPoint>? = null
                    /** AI interference */
                    if (resultBitmap!= null)
                    {
                        objects = ncnn.Detect(resultBitmap, true)
                    }

                    var finalscore: Float = 0.0f
                    var result: Person = Person()
                    var kpts: MutableList<KeyPoint> = mutableListOf()
                    if(objects != null)
                    {
                        for(i in 0..12)
                        {
                            var kpt = KeyPoint()
                            kpt.position.x = objects[i].x.toInt()
                            kpt.position.y = objects[i].y.toInt()
                            kpt.bodyPart =  BodyPart.values()[i]
                            kpt.score = objects[i].prob
                            finalscore += objects[i].prob
                            kpts.add(kpt)
                        }
                    }

                    result.keyPoints = kpts
                    result.score = finalscore/13
                    finalscore = 0.0f



//                    Injector.getNetLibrary().estimateSinglePose(resultBitmap)
//                    val result: Person = Injector.getNetLibrary().estimateSinglePose(resultBitmap)
//                    /** device */
//                    val device: Device = Injector.getNetLibrary().device
//                    /** last Inference Time Nanos */
//                    val lastInferenceTimeNanos: Long = Injector.getNetLibrary().lastInferenceTimeNanos
//                    /** display result on OverlayView */
//                    /** generate necessary score and comments */
//                    /** specify the posture */
                    //Injector.
//                    val TAG = "PoseScoreComment"
//                    val yogaBase: YogaBase = Injector.selectPose(Pose.TPose)
//                    yogaBase.setResult(result)
//                    val score = yogaBase.getScore()!!
//                    val comments = yogaBase.getComment()!!
//                    val color = yogaBase.getColorBit()
//                    Log.d(TAG, "The Score is : $score")
//                    Log.d(TAG, "The Commment is :  $comments")
//                    Log.d(TAG, "The ColorBit is :  $color")

                    /** update UI */
//                    this.runOnUiThread{
//                        //updating code
//                    }
                    //overlayView.drawResult(result, color)
                    if (result != null) {
                        overlayView.drawResult(result)
                    }

                }

                /**
                 * @param width : calculated CameraView's width
                 * @parasdkm height : calculated CameraView's height
                 * */
                override fun onCalculateDimension(width: Int, height: Int) {
                    overlayView.requestLayout(width, height)
                }
            })
        }

        checkCameraPermission()
    }
    //    private lateinit var cameraView: CameraView
//    private lateinit var overlayView: OverlayView
    override fun onResume() {
        super.onResume()

        /** Setting Camera Direction */
        //cameraView.setCameraFacing(CameraCharacteristics.LENS_FACING_FRONT)
        //overlayView.setCameraFacing(CameraCharacteristics.LENS_FACING_FRONT)

        cameraView.onResume()
    }
    override fun onPause() {
        super.onPause()
        cameraView.onPause()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkCameraPermission() {
        var permissionCamera = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA
                )
            ) {
                //show something to tell users why our app need permission
            }else{
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CameraView.REQUEST_CAMERA_PERMISSION
                )
            }
        }else{
            cameraView.permissionGranted()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CameraView.REQUEST_CAMERA_PERMISSION &&
            allPermissionsGranted(grantResults)) {
            cameraView.permissionGranted()
        }else{
            cameraView.permissionDenied()

        }
    }
    private fun allPermissionsGranted(grantResults: IntArray) = grantResults.all {
        it == PackageManager.PERMISSION_GRANTED
    }



}