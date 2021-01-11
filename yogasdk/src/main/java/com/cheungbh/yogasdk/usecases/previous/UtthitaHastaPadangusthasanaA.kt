package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class UtthitaHastaPadangusthasanaA: YogaBase() {
    override val poseName: String = Pose.UtthitaHastaPadangusthasanaA

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null


    /** constant */
    private val leg_ratio = 0.7
    private val waist_ratio = 0.2
    private val arm_ratio = 0.1

    /** score of body parts */
    private var waist_score: Double = 0.0
    private var leg_score: Double = 0.0
    private var arm_score: Double = 0.0

    private var side: Int = -1

    private var leg_lenth: Double = 0.0
    private var wrist_to_ankle_distance: Double = 0.0

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
        side = detectSide()
        arm_score = arm()
        val right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
        val left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
        leg_score = 0.5 * (left_leg_score + right_leg_score)
        waist_score = when(side){
            11 -> FeedbackUtilities.right_waist(resultArray!!, 180.0, 20.0, true)
            else -> FeedbackUtilities.left_waist(resultArray!!, 180.0, 20.0, true)
        }


        score = leg_ratio * leg_score + waist_ratio * waist_score + arm_ratio * arm_score

        return score!!
    }

    private fun makeComment():List<String>{
        comment = mutableListOf()
        comment!!.add("The Wrist-To-Ankle distance " + comment_wrist_ankle_distance(arm_score))
        comment!!.add("The Curvature of the Body " + FeedbackUtilities.comment(waist_score))
        comment!!.add("The Curvature of the Legs " + FeedbackUtilities.comment(leg_score))

        return comment!!
    }

    private fun arm(): Double {

        val r_wrist = resultArray!![6]
        val r_ankle = resultArray!![12]
        val r_knee = resultArray!![10]
        val l_wrist = resultArray!![5]
        val l_ankle = resultArray!![11]
        val l_knee = resultArray!![9]
        when(side) {
            11 -> {
                leg_lenth = FeedbackUtilities.cal_dis(l_ankle, l_knee)
                wrist_to_ankle_distance = FeedbackUtilities.cal_dis(l_ankle, l_wrist)
            }
            else->{
                leg_lenth = FeedbackUtilities.cal_dis(r_ankle, r_knee)
                wrist_to_ankle_distance = FeedbackUtilities.cal_dis(r_ankle, r_wrist)
            }

        }


        return if(wrist_to_ankle_distance < leg_lenth * 0.2){
            100.0
        }else {
            90.0
        }

    }

    private fun detectSide(): Int{
        val l_ankle = resultArray!![11]
        val r_ankle = resultArray!![12]
        return if(l_ankle[1] < r_ankle[1]){
            11
        }else{
            12
        }
    }

    private fun comment_wrist_ankle_distance(score: Double): String{
        return if(score == 100.0){
            " is great"
        }else{
            " is not ideal"
        }
    }
}