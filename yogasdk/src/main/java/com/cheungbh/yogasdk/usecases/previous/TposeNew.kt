package com.cheungbh.yogasdk.usecases.previous
import cheungbh.net.Person
import com.cheungbh.yogasdk.criteria.CriteriaBase
import com.cheungbh.yogasdk.usecases.YogaBase
import com.cheungbh.yogasdk.utilities.classToArray


@ExperimentalUnsignedTypes
class TposeNew(
private val leftAngleSingleArm: CriteriaBase,
private val rightAngleSingleArm: CriteriaBase,
private val leftAngleSingleLeg: CriteriaBase,
private val rightAngleSingleLeg: CriteriaBase,
private val leftAngleSingleShoulder: CriteriaBase,
private val rightAngleSingleShoulder: CriteriaBase
): YogaBase() {
    init {
        leftAngleSingleArm.set(180.0, 20.0, "left")
        rightAngleSingleArm.set(180.0, 20.0, "right")
        leftAngleSingleLeg.set(180.0, 20.0, "left")
        rightAngleSingleLeg.set(180.0, 20.0, "right")
        leftAngleSingleShoulder.set(90.0, 10.0, "left")
        rightAngleSingleShoulder.set(90.0, 10.0, "right")
    }
    override val poseName: String = com.cheungbh.yogasdk.domain.Pose.Companion.TposeNew

    /** output */
    private var comment : MutableList<String>? = null
    private var score : Double? = null
    private var colorbit: UInt = 0b0000000000000u

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val left_leg_ratio: Double = 0.15
    private val right_leg_ratio: Double = 0.15
    private val left_shoulder_ratio: Double = 0.2
    private val right_shoulder_ratio: Double = 0.2
    private val left_arm_ratio: Double = 0.15
    private val right_arm_ratio: Double = 0.15

    override fun setResult(result: Person){
        this.result = result
        resultArray = result.classToArray()
        update(resultArray!!)
    }

    /** getter */
    override fun getScore(): Double = this.score!!
    override fun getDetailedScore(): List<Double> {
        var detailedscore = listOf(0.0)
        return detailedscore
    }

    override fun getComment(): List<String> = this.comment!!
    override fun getResult(): Person = this.result!!

    @ExperimentalUnsignedTypes
    override fun getColorBit(): UInt = this.colorbit

    private fun update(kps: Array<Array<Double>>){

        leftAngleSingleArm.update(kps)
        val left_arm_score: Double = leftAngleSingleArm.getScore()
        val left_arm_colorbit: UInt = leftAngleSingleArm.getColorbit()
        val left_arm_comment: MutableList<String>? = leftAngleSingleArm.getComment()

        rightAngleSingleArm.update(kps)
        val right_arm_score: Double = rightAngleSingleArm.getScore()
        val right_arm_colorbit: UInt = rightAngleSingleArm.getColorbit()
        val right_arm_comment: MutableList<String>? = rightAngleSingleArm.getComment()

        leftAngleSingleShoulder.update(kps)
        val left_shoulder_score: Double = leftAngleSingleShoulder.getScore()
        val left_shoulder_colorbit: UInt = leftAngleSingleShoulder.getColorbit()
        val left_shoulder_comment: MutableList<String>? = leftAngleSingleShoulder.getComment()

        rightAngleSingleShoulder.update(kps)
        val right_shoulder_score: Double = rightAngleSingleShoulder.getScore()
        val right_shoulder_colorbit: UInt = rightAngleSingleShoulder.getColorbit()
        val right_shoulder_comment: MutableList<String>? = rightAngleSingleShoulder.getComment()

        leftAngleSingleLeg.update(kps)
        val left_leg_score: Double = leftAngleSingleLeg.getScore()
        val left_leg_colorbit: UInt = leftAngleSingleLeg.getColorbit()
        val left_leg_comment: MutableList<String>? = leftAngleSingleLeg.getComment()

        rightAngleSingleLeg.update(kps)
        val right_leg_score: Double = rightAngleSingleLeg.getScore()
        val right_leg_colorbit: UInt = rightAngleSingleLeg.getColorbit()
        val right_leg_comment: MutableList<String>? = rightAngleSingleLeg.getComment()

        score = left_leg_ratio * left_leg_score + right_leg_ratio * right_leg_score +
                left_shoulder_ratio * left_shoulder_score + right_shoulder_ratio * right_shoulder_score +
                left_arm_ratio * left_arm_score + right_arm_ratio * right_arm_score
        colorbit = left_arm_colorbit or left_leg_colorbit or left_shoulder_colorbit or
                right_arm_colorbit or right_shoulder_colorbit or right_leg_colorbit
        comment = mutableListOf<String>().apply{
            addAll(right_leg_comment!!)
            addAll(left_leg_comment!!)
            addAll(right_arm_comment!!)
            addAll(left_arm_comment!!)
            addAll(right_shoulder_comment!!)
            addAll(left_shoulder_comment!!)
        }
    }

}