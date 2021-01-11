package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class Dandasana: YogaBase() {

    override val poseName: String = Pose.Dandasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val leg_ratio = 0.5
    private val waist_ratio = 0.5

    /** score of body parts */
    private var waist_score: Double = 0.0
    private var leg_score: Double = 0.0

    /** setter */
    override fun setResult(result: Person) {
        this.result = result
        resultArray = result.classToArray()
        calculateScore()
        makeComment()
    }

    override fun getDetailedScore(): List<Double> {
        var detailedscore = listOf(leg_score, waist_score)
        return detailedscore
    }

    /** getter */
    override fun getScore(): Double = this.score!!
    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun makeComment(): MutableList<String>{
        comment =  mutableListOf()
        comment!!.add("The Waist-to-Thigh Distance " + FeedbackUtilities.comment(waist_score))
        comment!!.add("The Straightness of the Legs " + FeedbackUtilities.comment(leg_score))
        return comment!!
    }

    private fun calculateScore(): Double{

        val left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
        val right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
        leg_score = if(left_leg_score > right_leg_score){ left_leg_score }else{ right_leg_score }
        waist_score = FeedbackUtilities.right_waist(resultArray!!, 90.0, 10.0, true)

        score = leg_ratio *  leg_score + waist_ratio * waist_score
        return score!!
    }

}