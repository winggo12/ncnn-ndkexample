package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase


class UtthitaParsvakonasana: YogaBase() {

    companion object{
        private const val TAG = "GivePoseFeedback"
    }
    override val poseName: String = Pose.UtthitaParsvakonasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val leg_ratio = 0.5
    private val arm_ratio = 0.1
    private val waist_ratio = 0.4

    /** score of body parts */
    private var waist_score: Double = 0.0
    private var leg_score: Double= 0.0
    private var arm_score: Double= 0.0
    private var right_angle_leg_score: Double= 0.0
    private var shoulder_score: Double= 0.0
    private var direction: Int = -1

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
        var detailedscore = listOf(right_angle_leg_score, leg_score, waist_score, shoulder_score, arm_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun calculateScore():Double{

        direction = FeedbackUtilities.decideDirection(resultArray!!)
        if(direction == 6){
            right_angle_leg_score = FeedbackUtilities.left_leg(resultArray!!, 90.0, 20.0, false)
            leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, false)
            arm_score = FeedbackUtilities.right_arm(resultArray!!, 180.0, 20.0, false)
            shoulder_score = FeedbackUtilities.right_shoulder(resultArray!!, 180.0, 20.0, false)
            waist_score =  FeedbackUtilities.right_waist(resultArray!!, 180.0, 20.0, false)
            score = arm_ratio / 2 * (leg_score + shoulder_score) + waist_ratio * waist_score + leg_ratio / 2 * (right_angle_leg_score + leg_score)
        }else{
            right_angle_leg_score = FeedbackUtilities.right_leg(resultArray!!, 90.0, 20.0, false)
            leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, false)
            arm_score = FeedbackUtilities.left_arm(resultArray!!, 180.0, 20.0, false)
            shoulder_score = FeedbackUtilities.left_shoulder(resultArray!!, 180.0, 20.0, false)
            waist_score =  FeedbackUtilities.left_waist(resultArray!!, 180.0, 20.0, false)

        }
        score = arm_ratio / 2 * (leg_score + shoulder_score) + waist_ratio * waist_score + leg_ratio / 2 * (right_angle_leg_score + leg_score)
        return score!!
    }

    private fun makeComment():List<String>{
        comment = mutableListOf()

        val str = when(direction){
            6 -> "right"
            else-> "left"
        }
        val str1 = when(direction){
            6 -> "left"
            else-> "right"
        }
        comment!!.add("The Straightness of the Arms " + FeedbackUtilities.comment(arm_score))
        comment!!.add("The Straightness from shoulder to knee " + FeedbackUtilities.comment(waist_score))
        comment!!.add("The Straightness of the $str leg " + FeedbackUtilities.comment(leg_score))
        comment!!.add("The Curvature of the $str1 leg " + FeedbackUtilities.comment(right_angle_leg_score))


        return comment!!
    }


}