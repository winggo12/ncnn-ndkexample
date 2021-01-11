package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

//TO BE MODIFIED
class ParivrttaPashvaKonasana: YogaBase() {

    override val poseName: String = Pose.ParivrttaPashvaKonasana

    /** output */
    private var comment : MutableList<String>? = null
    private var score : Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */

    private val leg_ratio = 0.4
    private val waist_ratio = 0.4
    private val arm_ratio = 0.2

    /** score of body parts */
    private var arm_score: Double = 0.0
    private var waist_score: Double = 0.0
    private var leg_score: Double = 0.0

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
        var detailedscore = listOf(leg_score, waist_score, arm_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun calculateScore():Double{
        direction = FeedbackUtilities.decideDirection(resultArray!!)
        val right_leg_score: Double
        val left_leg_score: Double
        when (direction) {
            5->{
                right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 90.0, 20.0, true)
                left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
                arm_score = FeedbackUtilities.right_arm(resultArray!!, 180.0, 20.0, true)
                waist_score = FeedbackUtilities.left_waist(resultArray!!, 180.0, 20.0, true)
            }
            else->{
                right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
                left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 98.0, 20.0, true)
                arm_score = FeedbackUtilities.left_arm(resultArray!!, 180.0, 20.0, true)
                waist_score = FeedbackUtilities.right_waist(resultArray!!, 180.0, 20.0, true)
            }

        }

        leg_score = 0.5 * (right_leg_score + left_leg_score)


        score = arm_ratio * arm_score +  leg_ratio * leg_score + waist_ratio * waist_score
        return score!!
    }

    private fun makeComment():List<String>{

        comment =  mutableListOf()
        comment!!.add("The Straightness of the Arms " + FeedbackUtilities.comment( arm_score))
        comment!!.add("The Waist-to-Thigh Distance " + FeedbackUtilities.comment( waist_score))
        comment!!.add("The Straightness of the Legs " + FeedbackUtilities.comment( leg_score))

        return comment!!
    }



}