package com.cheungbh.yogasdk.utilities

import cheungbh.net.Person
import kotlin.math.abs

object FeedbackUtilities {
    fun square(num: Double): Double{
        return num * num
    }

    fun cal_dis(coord1: Array<Double>, coord2: Array<Double>): Double{
        val val1: Double =
            square(coord1[0] - coord2[0])
        val val2: Double =
            square(coord1[1] - coord2[1])
        val res: Double = Math.sqrt(val1 + val2)
        return res
    }

    fun calAngle(main_dis: Double, dis1: Double, dis2: Double): Double{
        val res: Double = (square(dis1) + square(
            dis2
        ) - square(main_dis))/(2*dis1*dis2)
        return Math.acos(res) * (180/Math.PI)
    }

    fun getAngle(coor1: Array<Double>, coor2: Array<Double>, coor3: Array<Double>): Double{
        val len1: Double = cal_dis(coor2, coor3)
        val len2: Double = cal_dis(coor1, coor2)
        val len3: Double = cal_dis(coor1, coor3)
        return calAngle(len1, len2, len3)
    }

    fun compareTreeRes(angle:Double): Double{
        val minAngle = 80
        val maxAngle = 150
        val ratio = 1 - ((angle - minAngle) /(maxAngle - minAngle))
        if (ratio < 0){
            return 0.toDouble()
        }else if(ratio > 1){
            return 1.toDouble()
        }else{
            return ratio
        }

    }

    fun comment(score: Double) : String{
        return when {
            score < 79.0 -> " not ideal"
            score > 81.0 -> " is great"
            else -> " is okay"
        }
    }


    fun ScoreConverter(currParam: Double, fullMarkParam: Double, step: Double, exact: Boolean): Double{
        if(exact) {
            return when {
                abs(currParam - fullMarkParam) < 2 -> 100.0
                abs(currParam - fullMarkParam) < step -> 90.0
                abs(currParam - fullMarkParam) < 2 * step -> 80.0
                abs(currParam - fullMarkParam) < 3 * step ->  70.0
                else -> 60.0
            }
        }else{
            return when {
                abs(currParam - fullMarkParam) < step ->  100.0
                abs(currParam - fullMarkParam) < 2 * step -> 90.0
                abs(currParam - fullMarkParam) < 3 * step -> 80.0
                abs(currParam - fullMarkParam) < 4 * step ->  70.0
                else -> 60.0
            }
        }

    }

    fun scoreTree(person: Person):Double {
        val basicScore = 45
        val heightScore = 55
        val left_hip_x = person.keyPoints[7].position.x.toDouble()
        val right_hip_x = person.keyPoints[8].position.x.toDouble()
        val left_knee_x = person.keyPoints[9].position.x.toDouble()
        val right_knee_x = person.keyPoints[10].position.x.toDouble()
        val left_ankle_x = person.keyPoints[11].position.x.toDouble()
        val right_ankle_x = person.keyPoints[12].position.x.toDouble()
        val left_hip_y = person.keyPoints[7].position.y.toDouble()
        val right_hip_y = person.keyPoints[8].position.y.toDouble()
        val left_knee_y = person.keyPoints[9].position.y.toDouble()
        val right_knee_y = person.keyPoints[10].position.y.toDouble()
        val left_ankle_y = person.keyPoints[11].position.y.toDouble()
        val right_ankle_y = person.keyPoints[12].position.y.toDouble()

        val left_hip = arrayOf(left_hip_x, left_hip_y)
        val right_hip = arrayOf(right_hip_x, right_hip_y)
        val left_knee = arrayOf(left_knee_x, left_knee_y)
        val right_knee = arrayOf(right_knee_x, right_knee_y)
        val left_ankle = arrayOf(left_ankle_x, left_ankle_y)
        val right_ankle = arrayOf(right_ankle_x, right_ankle_y)

        val angle = Math.min(
            getAngle(
                left_knee,
                left_ankle,
                left_hip
            ),
            getAngle(
                right_knee,
                right_ankle,
                right_hip
            )
        )
        val ratio = compareTreeRes(angle)
        val score =  heightScore * ratio + basicScore
        return score
    }

    /** analyze different parts of body and give a score */
    fun left_leg(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_knee = result[9]
        val left_hip = result[7]
        val left_ankle = result[11]

        val angle = getAngle(left_knee, left_ankle, left_hip)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun right_leg(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val r_knee = result[10]
        val r_hip = result[8]
        val r_ankle = result[12]

        val angle = getAngle(r_knee, r_ankle, r_hip)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun left_arm(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_elbow = result[3]
        val left_shoulder = result[1]
        val left_wrist = result[5]

        val angle = getAngle(left_elbow, left_wrist, left_shoulder)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun right_arm(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val r_elbow = result[4]
        val r_shoulder = result[2]
        val r_wrist = result[6]

        val angle = getAngle(r_elbow, r_shoulder, r_wrist)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun left_shoulder(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_shoulder = result[1]
        val left_hip = result[7]
        val left_elbow = result[3]
        val angle = getAngle(left_shoulder, left_hip, left_elbow)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun right_shoulder(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val right_shoulder = result[2]
        val right_hip = result[8]
        val right_elbow = result[4]
        val angle = getAngle(right_shoulder, right_hip, right_elbow)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun left_waist(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_shoulder = result[1]
        val left_hip = result[7]
        val left_knee = result[9]
        val angle = getAngle(left_hip, left_shoulder, left_knee)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun right_waist(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val right_shoulder = result[2]
        val right_hip = result[8]
        val right_knee = result[10]
        val angle = getAngle(right_hip, right_shoulder, right_knee)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun left_hand_knee_ankle(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_hand = result[5]
        val left_knee = result[9]
        val left_ankle = result[11]

        val angle = getAngle(left_knee, left_hand, left_ankle)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun right_hand_knee_ankle(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val right_hand = result[6]
        val right_knee = result[10]
        val right_ankle = result[12]
        val angle = getAngle(right_knee, right_hand, right_ankle)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }

    fun left_shoulder_hand_ankle(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_shoulder = result[1]
        val left_hand = result[5]
        val left_ankle = result[11]

        val angle = getAngle(left_hand, left_shoulder, left_ankle)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun right_shoulder_hand_ankle(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val right_shoulder = result[2]
        val right_hand = result[6]
        val right_ankle = result[12]
        val angle = getAngle(right_hand, right_shoulder, right_ankle)
        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun between_legs(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, exact:Boolean): Double{
        val middle_hip: Array<Double> = arrayOf((result[7][0] + result[8][0]) / 2, (result[7][1] + result[8][1]) / 2)
        val left_knee = result[9]
        val right_knee = result[10]
        val angle = getAngle(middle_hip, left_knee, right_knee)
        return ScoreConverter(angle, fullMarkAngle, step, exact)
    }

    fun left_hand_ankle_dist(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_hand = result[5]
        val left_knee = result[9]
        val left_ankle = result[11]
        val ratio = cal_dis(left_hand, left_ankle) / cal_dis(left_hand, left_knee)
        return ScoreConverter(ratio, fullMarkAngle, step, excat)
    }


    fun right_left_leg(result:Array<Array<Double>>, fullMarkAngle: Double, step: Double, excat:Boolean): Double{
        val left_hip = result[7]
        val left_knee = result[9]
        val right_hip = result[8]
        val right_knee = result[10]

        /** move line between right_hip and right_knee to make the above two lines intersect */
        val diffX = left_hip[0] - right_hip[0]
        val diffY = left_hip[1] - right_hip[1]
        val movedLine = moveLine(right_hip, right_knee, diffX, diffY)
        val new_right_hip = movedLine[0]
        val new_right_knee = movedLine[1]

        val angle = getAngle(new_right_hip, new_right_knee, left_knee)

        return ScoreConverter(angle, fullMarkAngle, step, excat)
    }
    fun decideDirection(resultArray: Array<Array<Double>>): Int{
        val left_wrist = resultArray[5]
        val right_wrist = resultArray[6]
        return when{
            left_wrist[1] < right_wrist[1] -> 5
            else -> 6
        }
    }
    fun movePoint(point:Array<Double>, diffX: Double, diffY: Double): Array<Double>{
        return arrayOf(point[0]+diffX, point[1]+diffY)
    }
    fun moveLine(point1:Array<Double>, point2:Array<Double>, diffX: Double, diffY: Double): Array<Array<Double>>{
        val newPt1 = movePoint(point1, diffX, diffY)
        val newPt2 = movePoint(point2, diffX, diffY)
        return arrayOf(newPt1, newPt2)
    }
}

