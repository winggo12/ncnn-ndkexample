package com.cheungbh.yogasdk.usecases

import android.content.pm.InstrumentationInfo
import android.widget.ArrayAdapter
import cheungbh.net.Person
import com.cheungbh.yogasdk.criteria.CriteriaBase
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.ColorUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import java.util.zip.DeflaterOutputStream

@ExperimentalUnsignedTypes
class AngleBetweenLegs : CriteriaBase() {

    private var max_angle: Double = 0.0
    private var step: Double = 0.0
    private var score_threshold: Double = 80.0
    private var direction: String = ""
    private var keypoint: Array<Array<Double>> ?= null

    private var angle: Double = 0.0
    private var score: Double = 0.0
    private var colorbit: UInt = 0b0000000000000u
    private var comment: MutableList<String>? = null

    private var leftScore: Double = 0.0
    private var rightScore: Double = 0.0
    private var leftColorBit: UInt = 0b0000000000000u
    private var rightColorBit: UInt = 0b0000000000000u

    private var good_performance: String = ""
    private var bad_performance: String = ""


    override fun getScore(): Double {
        return this.score
    }

    override fun getComment(): MutableList<String> {
        //this.comment = Array<String>
        return this.comment!!
    }

    override fun getColorbit(): UInt {
        return this.colorbit
    }

    override fun update(kps: Array<Array<Double>>) {
        this.keypoint = kps
        this.updateScore()
        this.updateColorBit()
        this.updateComments()
    }


    override fun set(maxAngle: Double, step: Double, direction: String) {
        this.max_angle = maxAngle
        this.step = step
        this.direction = direction

        if (this.max_angle > 160){
            this.good_performance = " arm is straight enough"
            this.bad_performance = " arm is not straight enough"
        }else if (this.max_angle < 60){
            this.good_performance = " arm is curve enough"
            this.bad_performance = " arm is not curve enough"
        }else{
            this.good_performance = " arm is close to " + this.max_angle.toString() + " degree"
            this.bad_performance = " arm is not close to " + this.max_angle.toString() + " degree"
        }
    }

    private fun updateScore(){
        this.leftScore = FeedbackUtilities.between_legs(this.keypoint!!, this.max_angle, this.step, false)
        this.score = 0.5*this.rightScore + 0.5* this.leftScore
    }

    private fun updateColorBit(){
        this.leftColorBit = ColorUtilities.left_arm(this.leftScore)
        this.rightColorBit = ColorUtilities.right_arm(this.rightScore)
        this.colorbit = this.leftColorBit or this.rightColorBit
    }

    private fun updateComments(){
        this.comment = mutableListOf()
        if (this.leftScore > this.score_threshold){
            this.comment!!.add("Left" + this.good_performance)
        }else{
            this.comment!!.add("Left" + this.bad_performance)
        }

        if (this.rightScore > this.score_threshold){
            this.comment!!.add("Right" + this.good_performance)
        }else{
            this.comment!!.add("Right" + this.bad_performance)
        }
    }

}