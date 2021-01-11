package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class Bhujangasana: YogaBase() {


    override val poseName: String = Pose.Bhujangasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val waist_ratio = 0.7
    private val arm_ratio = 0.3

    /** score of body parts */
    private var arm_score = -1.0
    private var waist_score = -1.0

    private var l_arm_angle = 0.0
    private var r_arm_angle = 0.0
    private var waist_angle = 0.0
    /** setter */
    override fun setResult(result: Person) {
        this.result = result
        resultArray = result.classToArray()
        calculateScore()
        makeComment()
    }

    override fun getDetailedScore(): List<Double> {
        var detailedscore = listOf(waist_score, arm_score)
        return detailedscore
    }

    /** getter */
    override fun getScore(): Double = this.score!!
    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun makeComment(): MutableList<String>{
        comment = mutableListOf(
            "l_arm_angle: $l_arm_angle, r_arm_angle: $r_arm_angle, score: $arm_score, Arms " + FeedbackUtilities.comment(arm_score),
            "waist_angle: $waist_angle, $waist_score, Waist" + FeedbackUtilities.comment(waist_score))
        return comment!!
    }

    private fun calculateScore(): Double{
        val l_arm_score = FeedbackUtilities.left_arm(resultArray!!, 90.0, 20.0, false)
        val r_arm_score = FeedbackUtilities.right_arm(resultArray!!, 90.0, 20.0, false)
        arm_score = 0.5 * (l_arm_score + r_arm_score)
        waist_score = FeedbackUtilities.right_waist(resultArray!!,100.0, 20.0, false)

        score = arm_ratio * arm_score + waist_ratio *  waist_score

        return score!!
    }


}