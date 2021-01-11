package com.cheungbh.yogasdk.view


import android.content.Context
import android.graphics.*
import android.hardware.camera2.CameraCharacteristics
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView
import cheungbh.net.BodyJoints
import cheungbh.net.BodyPart
import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.MODEL_HEIGHT
import com.cheungbh.yogasdk.utilities.MODEL_WIDTH
import com.cheungbh.yogasdk.utilities.mirrorX

/**
 * This class will used to  the pose analysis result and provide some draw methods to
 * help the rendering of result.
 * */
@ExperimentalUnsignedTypes
class OverlayView: SurfaceView{

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var minConfidence:Float = 0f
    private var mCameraFacing: Int = CameraCharacteristics.LENS_FACING_BACK
    /** self defined paint class */
    private var paint: OverLayViewPaint
    private var secondaryPaint: OverLayViewPaint


    var linesColorCode: UInt = 0b0u
    var ptsColorCode: UInt = 0b0u
    class OverLayViewPaint: Paint(){
        var circleRadius: Float = 0f
    }

    /*** initlization *****/
    constructor(context: Context):super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    init {

        /** default Paint */
        paint = OverLayViewPaint().apply {
            circleRadius = 8f
            color = Color.GREEN
            strokeWidth = 5f
        }
        secondaryPaint = OverLayViewPaint().apply {
            circleRadius = 8f
            color = Color.GREEN
            strokeWidth = 5f
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if(viewWidth == 0 && viewHeight == 0){
            setMeasuredDimension(width, height)
        } else {
            setMeasuredDimension(viewWidth, viewHeight)
        }
    }


    /** public methods */
    fun requestLayout(width: Int, height: Int){
        viewWidth = width
        viewHeight = height
        requestLayout()
    }

    /**
     * @param targetFacing
     * any one in {CameraCharacteristics.LENS_FACING_BACK, CameraCharacteristics.LENS_FACING_FRONT}
     * default= CameraCharacteristics.LENS_FACING_BACK
     * */
    fun setCameraFacing(targetFacing: Int){
        if(targetFacing == CameraCharacteristics.LENS_FACING_BACK || targetFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            mCameraFacing = targetFacing
        }
    }


    /**
     * @param getLinePaint
     * Detriment color and stroke width to be use. Return null to use default.
     * Pass in null to use default
     *
     * @param getPointPaint
     * detriment color, radius and stroke width to be use. Return null to use default.
     * Pass in null to use default
     * */

    fun drawResult(
        person: Person,
        getPointPaint: ((BodyPart)->OverLayViewPaint?)? = null,
        getLinePaint: ((BodyPart, BodyPart)->OverLayViewPaint?)? = null
    ) {
        //calculate the mirrored coordinate if we received image from front camera
        val newPerson = if(mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT){
            person.mirrorX(MODEL_WIDTH, MODEL_HEIGHT)
        }else{
            person
        }
        holder!!.setFormat(PixelFormat.TRANSLUCENT)
        val canvas: Canvas = holder!!.lockCanvas()
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        // Draw `bitmap` and `person` in square canvas.
        val displayWidth: Int
        val displayHeight: Int
        val left: Int
        val right: Int
        val top: Int
        val bottom: Int
        //detriment the display are on canvas
        if (canvas.height > canvas.width) {
            displayWidth = canvas.width
            displayHeight = canvas.width
            left = 0
            top = (canvas.height - canvas.width) / 2
        } else {
            displayWidth = canvas.height
            displayHeight = canvas.height
            left = (canvas.width - canvas.height) / 2
            top = 0
        }
        right = left + displayWidth
        bottom = top + displayHeight
        Log.d("draw", "screenWidth/screenHeight = ${displayWidth}/${displayHeight} = ${displayHeight/displayHeight}")
        Log.d("draw", "canvas.width = ${canvas.width}, canvas.height = ${canvas.height}")
        Log.d("draw", "left = $left, right = $right, top = $top, bottom = $bottom")
        val boundaryPaint = Paint().apply{ setARGB(155, 0, 100, 0) }
        val widthRatio = displayWidth.toFloat() / MODEL_WIDTH
        val heightRatio = displayHeight.toFloat() / MODEL_HEIGHT
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), top.toFloat(), boundaryPaint)
        canvas.drawRect(0f, bottom.toFloat(), canvas.width.toFloat(), canvas.height.toFloat(), boundaryPaint)
        // Draw key points over the image.
        for (keyPoint in newPerson.keyPoints) {

            if (keyPoint.score > minConfidence) {
                val position = keyPoint.position
                val adjustedX: Float = position.x.toFloat() * widthRatio + left
                val adjustedY: Float = position.y.toFloat() * heightRatio + top
                if(adjustedY > bottom){
                    Log.d("draw", "adjustedX = $adjustedX, adjustedY = $adjustedY")
                    Log.d("draw", " position.x = ${position.x},  position.x = ${position.y}")
                }
                val customPaint = if(getPointPaint!=null) {
                    getPointPaint.invoke(keyPoint.bodyPart)
                }else{
                    val result = ptsColorCode and  (0b1u shl (12-keyPoint.bodyPart.ordinal))
                    if(result > 0b0u){
                        secondaryPaint
                    }else{
                        null
                    }
                }
                canvas.drawCircle(
                    adjustedX,
                    adjustedY,
                    customPaint?.circleRadius ?: paint.circleRadius,
                    customPaint ?: paint
                )
//                    paint.circleRadius, paint)
            }
        }

        for (line in BodyJoints.list) {
            if (
                (newPerson.keyPoints[line.first.ordinal].score > minConfidence) and
                (newPerson.keyPoints[line.second.ordinal].score > minConfidence)
            ) {
                val customPaint = if(getLinePaint != null) {
                    getLinePaint.invoke(line.first, line.second)
                }else{
                    val index1 = BodyJoints.list.indexOf(Pair(line.first, line.second))
                    val index2 = BodyJoints.list.indexOf(Pair(line.second, line.first))
                    val resultColorCode = if(index1 > -1){
                        BodyJoints.colorDecode[index1] and linesColorCode
                    }else if(index2 > -1){
                        BodyJoints.colorDecode[index2] and linesColorCode

                    }else{
                        0u
                    }
                    if(resultColorCode > 0u){
                        secondaryPaint
                    }else{
                        null
                    }
                }

                canvas.drawLine(
                    newPerson.keyPoints[line.first.ordinal].position.x.toFloat() * widthRatio + left,
                    newPerson.keyPoints[line.first.ordinal].position.y.toFloat() * heightRatio + top,
                    newPerson.keyPoints[line.second.ordinal].position.x.toFloat() * widthRatio + left,
                    newPerson.keyPoints[line.second.ordinal].position.y.toFloat() * heightRatio + top,
                    customPaint ?: paint
                )
            }
        }

        holder!!.unlockCanvasAndPost(canvas)
    }


    fun getDefaultPaint(): OverLayViewPaint = paint
    fun setDefaultPaint(color: Int?=null, circleRadius: Float?=null, strokeWidth: Float?=null){
        color?.let { paint.color = color }
        circleRadius?.let { paint.circleRadius = circleRadius }
        strokeWidth?.let { paint.strokeWidth = strokeWidth }
    }
    fun getSecondaryPaint(): OverLayViewPaint = paint
    fun setSecondaryPaint(color: Int?=null, circleRadius: Float?=null, strokeWidth: Float?=null){
        color?.let { secondaryPaint.color = color }
        circleRadius?.let { secondaryPaint.circleRadius = circleRadius }
        strokeWidth?.let { secondaryPaint.strokeWidth = strokeWidth }
    }

    fun setMinConfidence(minConfidence: Float){
        this.minConfidence = minConfidence
    }
}