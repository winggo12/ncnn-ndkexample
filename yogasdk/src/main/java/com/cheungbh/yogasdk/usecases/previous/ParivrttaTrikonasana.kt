package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class ParivrttaTrikonasana: YogaBase() {

    override val poseName: String = Pose.ParivrttaTrikonasana

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

        val left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
        val right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)

        val left_arm_score = FeedbackUtilities.left_arm(resultArray!!, 180.0, 20.0, true)
        val right_arm_score = FeedbackUtilities.right_arm(resultArray!!, 180.0, 20.0, true)

        leg_score = if(left_leg_score > right_leg_score){left_leg_score}else{ right_leg_score }
        arm_score = 0.5 * (left_arm_score + right_arm_score)
        val left_waist_score: Double
        val right_waist_score: Double
        when(direction){
            5->{
                left_waist_score = FeedbackUtilities.left_waist(resultArray!!, 135.0, 20.0, true)
                right_waist_score = FeedbackUtilities.right_waist(resultArray!!, 45.0, 20.0, true)
            }
            else-> {
                left_waist_score = FeedbackUtilities.left_waist(resultArray!!, 45.0, 20.0, true)
                right_waist_score = FeedbackUtilities.right_waist(resultArray!!, 135.0, 20.0, true)
            }
        }
        waist_score = 0.5 * (left_waist_score + right_waist_score)
        score = arm_ratio * arm_score +  leg_ratio * leg_score + waist_ratio * waist_score
        return score!!
    }

    private fun makeComment():List<String>{

        comment =  mutableListOf()
        comment!!.add("The Straightness of the Arms " + FeedbackUtilities.comment( arm_score))
        comment!!.add("The Waist-to-Thigh Distance " + FeedbackUtilities.comment( waist_score))
        comment!!.add("The Straightness of the Legs " + FeedbackUtilities.comment( leg_score))
        comment!!.add("Direction: $direction")

        return comment!!
    }



}