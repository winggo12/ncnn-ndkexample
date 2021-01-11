package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class ArdhaChandarasana: YogaBase() {

    override val poseName: String = Pose.ArdhaChandarasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private var leg_ratio: Double = 0.4
    private var arm_ratio: Double = 0.2
    private var waist_ratio: Double = 0.4

    /** score of body parts */
    private var arm_score: Double = 0.0
    private var waist_score: Double = 0.0
    private var leg_score: Double = 0.0

    private var direction: Int = -1

    /** setter */
    override fun setResult(result: Person) {
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

    /** private method */
    private fun makeComment(): MutableList<String>{
        comment =  mutableListOf()

        comment!!.add("The Straightness of the Arms " + FeedbackUtilities.comment(arm_score))
        comment!!.add("The curvature of Body " + FeedbackUtilities.comment(waist_score))
        comment!!.add("The Straightness of the Legs " + FeedbackUtilities.comment(leg_score))
        return comment!!
    }

    private fun calculateScore():Double{
        val left_arm_score = FeedbackUtilities.left_arm(resultArray!!, 180.0, 20.0, true)
        val right_arm_score = FeedbackUtilities.right_arm(resultArray!!, 180.0, 20.0, true)

        val left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
        val right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
        arm_score = 0.5 * (left_arm_score + right_arm_score)

        leg_score = if(left_leg_score > right_leg_score){ left_leg_score } else { right_leg_score }

        direction = decideDirection()
        waist_score = when(direction){
            5 -> FeedbackUtilities.right_waist(resultArray!!, 180.0, 20.0, true)
            else -> FeedbackUtilities.left_waist(resultArray!!, 180.0, 20.0, true)
        }

        score = arm_ratio * arm_score + leg_ratio * leg_score + waist_ratio * waist_score
        return score!!
    }

    private fun decideDirection(): Int{
        val left_wrist = resultArray!![5]
        val right_wrist = resultArray!![6]
        return when{
            left_wrist[1] > right_wrist[1] -> 5
            else -> 6
        }
    }
}