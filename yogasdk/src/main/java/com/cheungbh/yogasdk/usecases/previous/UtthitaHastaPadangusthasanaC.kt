package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class UtthitaHastaPadangusthasanaC: YogaBase() {

    override val poseName: String = Pose.UtthitaHastaPadangusthasanaC

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val leg_ratio = 0.6
    private val waist_ratio = 0.4

    /** score of body parts */
    private var waist_score: Double = 0.0
    private var leg_score: Double= 0.0

    private var direction: Int = -1

    /** setter */
    override fun setResult(result: Person){
        this.result = result
        resultArray = result.classToArray()
        calculateScore()
        makeComment()
    }

    /** getter */
    override fun getScore(): Double = this.score!!!!
    override fun getDetailedScore(): List<Double> {
        var detailedscore = listOf(leg_score, waist_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!!!
    override fun getResult(): Person = this.result!!!!

    /** private method */
    private fun calculateScore():Double{
        direction = decideDirection()

        val l_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
        val r_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
        leg_score = 0.5 * (l_leg_score + r_leg_score)

        val l_waist_score: Double
        val r_waist_score: Double
        when(direction){
            11 ->{
                l_waist_score = FeedbackUtilities.left_waist(resultArray!!, 0.0, 20.0, false)
                r_waist_score = FeedbackUtilities.right_waist(resultArray!!, 180.0, 20.0, false)
            }
            else -> {
                l_waist_score = FeedbackUtilities.left_waist(resultArray!!, 180.0, 20.0, false)
                r_waist_score = FeedbackUtilities.right_waist(resultArray!!, 0.0, 20.0, false)
            }

        }

        waist_score = 0.5 * (l_waist_score + r_waist_score)
        score = leg_ratio * leg_score + waist_ratio * waist_score
        return score!!
    }

    private fun makeComment():List<String>{
        comment = mutableListOf()
        comment!!.add("The straightness of the Body " + FeedbackUtilities.comment(waist_score))
        comment!!.add("The straightness of the Legs " + FeedbackUtilities.comment(leg_score))
        return comment!!
    }

    private fun decideDirection(): Int{
        val l_ankle = resultArray!![11]
        val r_ankle = resultArray!![12]
        if(l_ankle[1] < r_ankle[1]){
            return 11
        }else{
            return 12
        }
    }
}