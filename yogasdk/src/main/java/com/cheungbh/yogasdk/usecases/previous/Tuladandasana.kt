package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.BodyPart
import cheungbh.net.KeyPoint
import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.classToArray
import com.cheungbh.yogasdk.utilities.classToPosArray
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class Tuladandasana: YogaBase() {

    override val poseName: String = Pose.Tuladandasana

    /** output */
    private var comment : MutableList<String>? = null
    private var score : Double? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val leg_arm_waist_ratio = 0.7
    private val standing_leg_ratio = 0.3

    /** score of body parts */
    private var leg_score: Double = 0.0
    private var arm_score: Double = 0.0
    private var shoulder_score: Double = 0.0
    private var standing_leg_score: Double = 0.0
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
        var detailedscore = listOf(standing_leg_score, leg_score, waist_score, shoulder_score, arm_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun calculateScore():Double{
        val standing_leg: KeyPoint = standing_leg()
        when(standing_leg.bodyPart){
            BodyPart.LEFT_ANKLE ->{
                standing_leg_score = FeedbackUtilities.left_leg(resultArray!!, 180.0, 20.0, true)
                leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
            }
            else ->{
                standing_leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
                leg_score = FeedbackUtilities.right_leg(resultArray!!, 180.0, 20.0, true)
            }
        }

        val left_arm_score = FeedbackUtilities.left_arm(resultArray!!, 180.0, 20.0, true)
        val right_arm_score = FeedbackUtilities.right_arm(resultArray!!, 180.0, 20.0, true)
        arm_score = 0.5 * (left_arm_score + right_arm_score)

        waist_score = FeedbackUtilities.right_waist(resultArray!!, 180.0, 20.0, true)
        val left_shoulder_score = FeedbackUtilities.left_shoulder(resultArray!!, 180.0, 20.0, true)
        val right_shoulder_score = FeedbackUtilities.right_shoulder(resultArray!!, 180.0, 20.0, true)
        val shoulder_score = 0.5 * (left_shoulder_score + right_shoulder_score)
        score = leg_arm_waist_ratio / 4 * (leg_score + arm_score + waist_score + shoulder_score) + standing_leg_ratio * standing_leg_score

        return score!!
    }

    private fun makeComment():List<String>{

        comment =  mutableListOf()
        comment!!.add("The Straightness of the Arms" + FeedbackUtilities.comment( arm_score))
        comment!!.add("The Straightness of the body " + FeedbackUtilities.comment( waist_score))
        comment!!.add("The Straightness of the horizontal leg " + FeedbackUtilities.comment( leg_score))
        comment!!.add("The Straightness of the standing leg " + FeedbackUtilities.comment( standing_leg_score))

        return comment!!
    }

    private fun standing_leg(): KeyPoint{

        val left_ankle = result!!.keyPoints[11].classToPosArray()
        val r_ankle = result!!.keyPoints[12].classToPosArray()

        return if(left_ankle[1] > r_ankle[1]){
            result!!.keyPoints[11]
        }else{
            result!!.keyPoints[12]
        }
    }
}