package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.FeedbackUtilities.comment
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class Ustrasana : YogaBase() {

    override val poseName: String = Pose.Ustrasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private var waist_ratio: Double = 0.4
    private var leg_ratio: Double= 0.5
    private var arm_ratio: Double= 0.1


    /** score of body parts */
    private var waist_score: Double = 0.0
    private var leg_score: Double= 0.0
    private var waist_angle: Double = 0.0
    private var arm_score: Double= 0.0
    private var right_leg_score: Double= 0.0
    private var left_leg_score: Double= 0.0
    private var right_arm_score: Double= 0.0
    private var left_arm_score: Double= 0.0
    @kotlin.ExperimentalUnsignedTypes
    private var colorCode: UInt = 0b0000000000000u

    /** setter */
    override fun setResult(result: Person){
        this.result = result
        resultArray = result.classToArray()
        calculateScore()
        makeComment()
    }

    /** getter */
    override fun getScore(): Double = this.score!!
    override fun getDetailedScore(): List<Double> {
        var detailedscore = listOf(leg_score, waist_score, arm_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

//    @kotlin.ExperimentalUnsignedTypes
//    override fun getColorBit(): UInt {
//        val right_arm = ColorUtilities.right_arm(right_arm_score)
//        val left_arm = ColorUtilities.left_arm(left_arm_score)
//        val left_leg = ColorUtilities.left_leg(left_leg_score)
//        val right_leg = ColorUtilities.right_leg(left_leg_score)
//        val waist = ColorUtilities.right_waist(waist_score)
//        return right_arm or left_arm or left_leg or right_leg or waist
//    }

    /** private method */
    private fun calculateScore():Double{
        right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 90.0, 10.0, false)
        left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 90.0, 10.0, false)
        leg_score = 0.5 * (right_leg_score + left_leg_score)

        right_arm_score = FeedbackUtilities.right_arm(resultArray!!, 180.0, 20.0, true)
        left_arm_score = FeedbackUtilities.left_arm(resultArray!!, 180.0, 20.0, true)
        arm_score = 0.5 * (right_arm_score + left_arm_score)

        waist_score = FeedbackUtilities.right_waist(resultArray!!, 90.0, 20.0, true)

        score = arm_ratio * arm_score + waist_ratio * waist_score + leg_ratio * leg_score
        return score!!
    }

    private fun makeComment():List<String>{
        comment = mutableListOf()
        comment!!.add("The Straightness of the Arms " + comment(arm_score))
        comment!!.add("The Curvature of the Body " + comment(waist_score))
        comment!!.add("The Curvature of the Legs " + comment(leg_score))

        return comment!!
    }


}