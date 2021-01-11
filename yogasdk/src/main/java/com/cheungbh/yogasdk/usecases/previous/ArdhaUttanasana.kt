package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import kotlin.math.abs
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class ArdhaUttanasana: YogaBase() {

    override val poseName: String = Pose.ArdhaUttanasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private var leg_ratio: Double = 0.5
    private var leg_floor_ratio: Double = 0.5
//    private var waist_ratio: Double = 0.333

    /** score of body parts */
    private var waist_score: Double = 0.0
    private var leg_score: Double = 0.0
    private var leg_floor_score: Double = 0.0

    /** setter */
    override fun setResult(result: Person) {
        this.result = result
        resultArray = result.classToArray()
        calculateScore()
        makeComment()
    }

    override fun getDetailedScore(): List<Double> {
        var detailedscore = listOf(leg_score, leg_floor_score)
        return detailedscore
    }

    /** getter */
    override fun getScore(): Double = this.score!!
    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun makeComment(): MutableList<String>{
        comment =  mutableListOf()
        comment!!.add("The Hand-to-Ground Distance " + FeedbackUtilities.comment(leg_floor_score))
        comment!!.add("The angle between legs and floor " + FeedbackUtilities.comment(leg_floor_score))
//        comment!!.add("$waist_score, The Waist-to-Thigh Distance " + FeedbackUtilities.comment(waist_score))
        comment!!.add("The Straightness of the Legs " + FeedbackUtilities.comment(leg_score))

        return comment!!
    }

    private fun calculateScore():Double{

        val left_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
        val right_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
        leg_score =  if(left_leg_score > right_leg_score){left_leg_score}else{right_leg_score}

//        waist_score = FeedbackUtilities.right_waist(resultArray!!, 90.0, 20.0, false)

        leg_floor_score = leg_floor_arm()

        score = leg_ratio * leg_score + leg_floor_ratio *  leg_floor_score
        return score!!
    }

    private fun hand_foot_horizontal():  Double{
        val left_wrist = resultArray!![5][1]
        val left_ankle = resultArray!![11][1]
        val left_knee = resultArray!![9][1]

        val hand_foot_distance = abs(left_wrist - left_ankle)
        val foot_knee_distance = abs(left_ankle - left_knee)

        val ratio = hand_foot_distance/foot_knee_distance

        return if( ratio < 0.2){
            100.0
        }else if(ratio >= 0.2 && ratio < 0.5) {
            90.0
        }else if (ratio >= 0.5 && ratio < 0.8){
            80.0
        }else if (ratio >= 0.8 && ratio < 1.1){
            70.0
        }else{
            60.0
        }

    }

    private fun leg_floor(): Double{
        val left_knee = resultArray!![9]
        val right_knee = resultArray!![10]
        val left_ankle = resultArray!![11]
        val right_ankle = resultArray!![12]

        //draw a line parallel to screen's x-axis
        val left_floot_pt = Array<Double>(2){ 0.0 }
        left_floot_pt[0] = resultArray!![11][0] + 1
        left_floot_pt[1] = resultArray!![11][1]
        val right_floot_pt = Array<Double>(2){ 0.0 }
        right_floot_pt[0] = resultArray!![12][0] + 1
        right_floot_pt[1] = resultArray!![12][1]

        //find angle between leg and floor
        val left_angle = FeedbackUtilities.getAngle(left_ankle, left_knee, left_floot_pt)
        val right_angle = FeedbackUtilities.getAngle(right_ankle, right_knee, right_floot_pt)
        val left_leg_floor_score = FeedbackUtilities.ScoreConverter(left_angle, 90.0, 10.0, false)
        val right_leg_floor_score = FeedbackUtilities.ScoreConverter(right_angle, 90.0, 10.0, false)
        return 0.5 * (left_leg_floor_score + right_leg_floor_score)
    }
    private fun leg_floor_arm(): Double{
        val left_knee = resultArray!![9]
        val right_knee = resultArray!![10]
        val left_ankle = resultArray!![11]
        val right_ankle = resultArray!![12]
        val l_wrist = resultArray!![5]
        val r_wrist = resultArray!![6]

        //find angle between leg and the line joining ankle and wrist
        val left_angle = FeedbackUtilities.getAngle(left_ankle, left_knee, l_wrist)
        val right_angle = FeedbackUtilities.getAngle(right_ankle, right_knee, r_wrist)
        val left_leg_floor_score = FeedbackUtilities.ScoreConverter(left_angle, 90.0, 10.0, true)
        val right_leg_floor_score = FeedbackUtilities.ScoreConverter(right_angle, 90.0, 10.0, true)
        return 0.5 * (left_leg_floor_score + right_leg_floor_score)
    }
}