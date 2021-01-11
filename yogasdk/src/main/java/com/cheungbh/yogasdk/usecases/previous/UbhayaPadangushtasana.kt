package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase


class UbhayaPadangushtasana: YogaBase() {
    override val poseName: String = Pose.UbhayaPadangushtasana

    /** output */
    private var comment : MutableList<String>? = null
    private var score : Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private var ratio: Double = 0.5

    /** score of body parts */
    private var leg_score: Double = 0.0
    private var waist_score: Double = 0.0
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
        var detailedscore = listOf(leg_score, waist_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun calculateScore():Double{
        //TO BE MODIFIED
        val r_score = FeedbackUtilities.right_waist(resultArray!!, 30.0, 20.0, false) //40
        val l_score = FeedbackUtilities.left_waist(resultArray!!, 30.0, 20.0, false) //40

        waist_score = if(l_score > r_score){ l_score }else{ r_score }

        val left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
        val right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
        leg_score = 0.5 * (left_leg_score + right_leg_score)
        score = ratio * (waist_score + leg_score)
        return score!!
    }

    private fun makeComment():List<String>{

        comment =  mutableListOf()
        comment!!.add("The curvature of body " + FeedbackUtilities.comment( waist_score))
        comment!!.add("The straightness of legs " + FeedbackUtilities.comment( leg_score))


        return comment!!
    }


}