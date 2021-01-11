package com.cheungbh.yogasdk.usecases

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.Image
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import com.cheungbh.yogasdk.utilities.ImageUtils
import com.cheungbh.yogasdk.utilities.MODEL_HEIGHT
import com.cheungbh.yogasdk.utilities.MODEL_WIDTH
import kotlin.math.abs


/**
 * This class is a helper class to convert image format and rotate the image when necessary.
 * val modelInputRatio = MODEL_HEIGHT.toFloat() / MODEL_WIDTH
 *
 * */
class ProcessImage{
    companion object {
        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        private val ORIENTATIONS = SparseIntArray()
        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 0)
            ORIENTATIONS.append(Surface.ROTATION_90, 90)
            ORIENTATIONS.append(Surface.ROTATION_180, 180)
            ORIENTATIONS.append(Surface.ROTATION_270, 270)
        }


        private const val TAG = "ProcessImage"
    }

    private lateinit var resultBitmap: Bitmap
    private var modelInputRatio: Float = MODEL_HEIGHT.toFloat() / MODEL_WIDTH

    /** A ByteArray to save image data in YUV format  */
    private var yuvBytes = arrayOfNulls<ByteArray>(3)

    /** An IntArray to save image data in ARGB8888 format  */
    private var rgbBytes: IntArray? = null

    /** Crop Bitmap to maintain aspect ratio of model input.   */
    private fun cropBitmap(bitmap: Bitmap, modelInputRatio: Float): Bitmap {
        val bitmapRatio = bitmap.height.toFloat() / bitmap.width
        var croppedBitmap = bitmap

        // Acceptable difference between the modelInputRatio and bitmapRatio to skip cropping.
        val maxDifference = 1e-5

        // Checks if the bitmap has similar aspect ratio as the required model input.
        when {
            abs(modelInputRatio - bitmapRatio) < maxDifference -> return croppedBitmap
            modelInputRatio < bitmapRatio -> {
                // New image is taller so we are height constrained.
                val cropHeight = bitmap.height - (bitmap.width.toFloat() / modelInputRatio)
                croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    (cropHeight / 2).toInt(),
                    bitmap.width,
                    (bitmap.height - cropHeight).toInt()
                )
            }
            else -> {
                val cropWidth = bitmap.width - (bitmap.height.toFloat() * modelInputRatio)
                croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    (cropWidth / 2).toInt(),
                    0,
                    (bitmap.width - cropWidth).toInt(),
                    bitmap.height
                )
            }
        }
        return croppedBitmap
    }


    /**
     * This method will convert the image format from YUV420 to ARGB8888,
     * rotate the image,
     * crop the image into aspect ratio of model input
     * and scale the image into a specified size.
     * @param img is the image to be processed
     * @param imgReaderOutSize is the output resolution of ImageReader
     */
    fun process(img: Image, imgReaderOutSize: Size, sensorOrientation: Int, deviceOrientation: Int): Bitmap{

        val methodtag = "ProcessImage.process()"

        if(rgbBytes == null
            || imgReaderOutSize.width * imgReaderOutSize.height != rgbBytes!!.size){
            rgbBytes = IntArray(imgReaderOutSize.width * imgReaderOutSize.height)
            yuvBytes = arrayOfNulls(3)
        }


        fillBytes(img.planes, yuvBytes)


        Log.d(TAG, "$methodtag: image.width = ${img.width}, image.height = ${img.height}")
        Log.d(TAG, "$methodtag: imgReaderOutSize.width = ${imgReaderOutSize.width}, imgReaderOutSize.height = ${imgReaderOutSize.height}")
        Log.d(TAG,"$methodtag: rgbBytes = $rgbBytes")
        Log.d(TAG,"$methodtag: img.planes = ${img.planes}")
        Log.d(TAG,"$methodtag: yuvBytes[0] = ${yuvBytes[0]}, yuvBytes[1] = ${yuvBytes[1]}, yuvBytes[2] = ${yuvBytes[2]}")

        /** convert format */
        var now = System.currentTimeMillis()
        ImageUtils.convertYUV420ToARGB8888(
            yuvBytes[0]!!,
            yuvBytes[1]!!,
            yuvBytes[2]!!,
            imgReaderOutSize.width,
            imgReaderOutSize.height,
            /*yRowStride=*/ img.planes[0].rowStride,
            /*uvRowStride=*/ img.planes[1].rowStride,
            /*uvPixelStride=*/ img.planes[1].pixelStride,
            rgbBytes!!
        )
        // Create bitmap from int array
        val imageBitmap = Bitmap.createBitmap(
            rgbBytes!!, imgReaderOutSize.width,  imgReaderOutSize.height,
            Bitmap.Config.ARGB_8888
        )
        Log.d(
            TAG,
            "$methodtag: imageBitmap.width = ${imageBitmap.width}, imageBitmap.height = ${imageBitmap.height}"
        )
        val formatTranExecTime = System.currentTimeMillis() - now

        /** rotate image */
        now = System.currentTimeMillis()
        val displayRotation = getDisplayRotation(sensorOrientation, deviceOrientation)
        val rotatedBitmap = if(displayRotation != 0) {
            val rotateMatrix = Matrix()
            rotateMatrix.postRotate(displayRotation.toFloat())

            Bitmap.createBitmap(
                imageBitmap, 0, 0, imgReaderOutSize.width, imgReaderOutSize.height,
                rotateMatrix, true
            )
        }else{
            imageBitmap
        }
        val rotateExecTime = System.currentTimeMillis() - now

        /** crop image to square */
        now = System.currentTimeMillis()
        val croppedBitmap = cropBitmap(rotatedBitmap, modelInputRatio)
        Log.d(
            TAG,
            "$methodtag: croppedBitmap.width = ${croppedBitmap.width}, croppedBitmap.height = ${croppedBitmap.height}"
        )
        val cropExecTime =  System.currentTimeMillis() - now

        /** scale image */
        now = System.currentTimeMillis()
        // Created scaled version of bitmap for model input.
        resultBitmap = Bitmap.createScaledBitmap(
            croppedBitmap,
            MODEL_WIDTH,
            MODEL_HEIGHT, true
        )
        Log.d(
            TAG,
            "$methodtag: scaledBitmap.width = ${resultBitmap.width}, scaledBitmap.height = ${resultBitmap.height}"
        )
        val scaleExecTime = System.currentTimeMillis() - now

        Log.d(TAG, "$methodtag: formatTranExecTime = $formatTranExecTime, rotateExecTime = $rotateExecTime, cropExecTime = $cropExecTime, scaleExecTime = $scaleExecTime")
        return resultBitmap
    }

    /** Fill the yuvBytes with data from image planes.   */
    private fun fillBytes(planes: Array<Image.Plane>, yuvBytes: Array<ByteArray?>) {
        // Row stride is the total number of bytes occupied in memory by a row of an image.
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer.get(yuvBytes[i]!!)
        }
    }

    private fun getDisplayRotation(sensorOrientation: Int, deviceOrientation: Int): Int =
        (360 - ORIENTATIONS.get(deviceOrientation) + sensorOrientation) % 360

}