package com.cheungbh.yogasdk.usecases.previous

import cheungbh.net.Person
import com.cheungbh.yogasdk.utilities.FeedbackUtilities
import com.cheungbh.yogasdk.utilities.FeedbackUtilities.getAngle
import com.cheungbh.yogasdk.utilities.classToArray
import kotlin.math.abs
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.YogaBase

class Vrksasana: YogaBase() {

    override val poseName: String = Pose.Vrksasana

    /** output */
    private var comment: MutableList<String>? = null
    private var score: Double? = null
    //private var detailedscore: List<Double>? = null

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private var left_right_thigh_ratio: Double = 0.5
    private var time_ratio: Double = 0.5

    /** score of body parts */
    private var left_right_thigh_score: Double = 0.0
    private var time_score: Double = 0.0

    /** unit = ms */
    private var start_time: Long = 0
    private var timer_ms: Long = 0
    private var isStartTiming: Boolean = false

    /** average angle */
    private var l_hip_average: Double = 0.0
    private var r_hip_average: Double = 0.0
    private var l_knee_average: Double = 0.0
    private var r_knee_average: Double = 0.0

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
        var detailedscore = listOf(left_right_thigh_score, time_score)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    /** private method */
    private fun calculateScore():Double{
        start_timing()
        time_score = cal_time_score(start_time)
        left_right_thigh_score = FeedbackUtilities.right_left_leg(resultArray!!, 90.0, 20.0, false)
        score = left_right_thigh_ratio * left_right_thigh_score + time_ratio * time_score

        return score!!
    }

    private fun makeComment():List<String>{
        comment = mutableListOf()
        comment!!.add("The angle between left and right thigh: " + FeedbackUtilities.comment(left_right_thigh_score))
        comment!!.add("The time of standing: $timer_ms")


        return comment!!
    }

    private fun start_timing(){
        val left_shoulder = resultArray!![1]
        val left_hip = resultArray!![7]
        val left_knee = resultArray!![9]
        val left_ankle = resultArray!![11]
        val angle1 = getAngle(left_shoulder, left_hip, left_knee)
        val angle2 = getAngle(left_knee, left_hip, left_ankle)

        val right_shoulder = resultArray!![2]
        val right_hip = resultArray!![8]
        val right_knee = resultArray!![10]
        val right_ankle = resultArray!![12]
        val angle3 = getAngle(right_shoulder, right_hip, right_knee)
        val angle4 = getAngle(right_knee, right_hip, right_ankle)

        l_hip_average = cal_average_angle(l_hip_average,  angle1)
        l_knee_average = cal_average_angle(l_knee_average,  angle2)
        r_hip_average = cal_average_angle(r_hip_average,  angle3)
        r_knee_average = cal_average_angle(r_knee_average,  angle4)

        if((abs(l_hip_average - 180.0) <= 20.0 && abs(l_knee_average - 180.0) <= 20.0)
            || (abs(r_hip_average - 180.0) <= 20.0 && abs(r_knee_average - 180.0) <= 20.0)
        ){
            if(!isStartTiming){
                start_time = 0
                start_time = System.currentTimeMillis()
                isStartTiming = true
            }

        }else{
            start_time = 0
            isStartTiming = false
        }
    }

    private fun cal_time_score(start_time: Long): Double{
        timer_ms = System.currentTimeMillis() - start_time
        val timer_s = timer_ms.toDouble() / 1000

        return when{
            (timer_ms <= 20)->70.0
            (timer_ms <= 40)->80.0
            (timer_ms <= 60)->90.0
            else->100.0
        }
    }

    private fun cal_average_angle(oldAverage: Double, newData: Double): Double{
        return (oldAverage * 3 + newData) / 4
    }
}