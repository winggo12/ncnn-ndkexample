package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class TPose: YogaBase() {

    override val poseName: String = Pose.TPose

    /** output */
    private var comment : MutableList<String>? = null
    private var score : Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val leg_ratio = 0.3
    private val shoulder_ratio = 0.4
    private val arm_ratio = 0.3

    /** score of body parts */
    private var arm_score: Double = 0.0
    private var shoulder_score: Double = 0.0
    private var leg_score: Double = 0.0

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
        var detailedscore = listOf(leg_score, shoulder_score, arm_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun calculateScore():Double{
        val left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 160.0, 20.0, false)
        val right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 160.0, 20.0, false)
        val left_shoulder_score = FeedbackUtilities.left_shoulder(resultArray!!, 80.0, 20.0, false)
        val right_shoulder_score = FeedbackUtilities.right_shoulder(resultArray!!, 80.0, 20.0, false)
        val left_arm_score = FeedbackUtilities.left_arm(resultArray!!, 160.0, 20.0, false)
        val right_arm_score = FeedbackUtilities.right_arm(resultArray!!, 160.0, 20.0, false)
        arm_score = 0.5 * (left_arm_score + right_arm_score)
        shoulder_score = 0.5*(left_shoulder_score+right_shoulder_score)
        leg_score = 0.5 * (left_leg_score + right_leg_score)


        score =  arm_ratio * arm_score + leg_ratio * leg_score + shoulder_ratio * shoulder_score
        return score!!
    }

    private fun makeComment():List<String>{

        comment =  mutableListOf()
        comment!!.add("The Straightness of the Arms " + FeedbackUtilities.comment(arm_score))
        comment!!.add("The Position of the Arms " + FeedbackUtilities.comment(shoulder_score))
        comment!!.add("The Straightness of the Legs " + FeedbackUtilities.comment(leg_score))

        return comment!!
    }



}