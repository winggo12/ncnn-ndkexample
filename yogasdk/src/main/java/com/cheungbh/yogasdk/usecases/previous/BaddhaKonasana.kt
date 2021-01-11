package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person

import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class BaddhaKonasana: YogaBase() {

    override val poseName: String = Pose.BaddhaKonasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */

    /** score of body parts */
    private var left_leg_score = -1.0
    private var right_leg_score = -1.0

    /** setter */
    override fun setResult(result: Person) {
        this.result = result
        resultArray = result.classToArray()
        calculateScore()
        makeComment()
    }

    /** getter */
    override fun getScore(): Double = this.score!!
    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!
    override fun getDetailedScore(): List<Double> {
        var detailedscore = listOf(left_leg_score, right_leg_score)
        return detailedscore
    }

    /** private method */
    private fun makeComment(): MutableList<String>{
        comment =  mutableListOf()
        comment!!.add("The Curvature of the Legs " + FeedbackUtilities.comment(score!!))

        return comment!!
    }

    private fun calculateScore():Double{
        left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 0.0, 20.0, true)
        right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 0.0, 20.0, true)
        score = 0.5 * (left_leg_score + right_leg_score)

        return score!!
    }


}