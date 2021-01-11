package com.cheungbh.yogasdk.usecases
import android.content.pm.InstrumentationInfo
import android.widget.ArrayAdapter
import cheungbh.net.Person
import com.cheungbh.yogasdk.criteria.CriteriaBase
import com.cheungbh.yogasdk.utilities.classToArray


@ExperimentalUnsignedTypes
class TposeNew2(
    private val AngleBothLegs: CriteriaBase,
    private val AngleBothShoulders: CriteriaBase,
    private val AngleBothArms: CriteriaBase

): YogaBase() {
    init {
        AngleBothLegs.set(180.0, 20.0, "")
        AngleBothShoulders.set(90.0, 10.0, "")
        AngleBothArms.set(180.0, 20.0, "")
    }
    override val poseName: String = com.cheungbh.yogasdk.domain.Pose.Companion.TPose

    /** output */
    private var comment : MutableList<String>? = null
    private var score : Double? = null
    private var colorbit: UInt = 0b0000000000000u

    /** input */
    private var result: Person? = null
    private var resultArray: Array<Array<Double>>? = null

    /** constant */
    private val leg_ratio: Double = 0.3
    private val shoulder_ratio: Double = 0.4
    private val arm_ratio: Double = 0.3

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

        AngleBothLegs.update(kps)
        val leg_score: Double = AngleBothLegs.getScore()
        val leg_colorbit: UInt = AngleBothLegs.getColorbit()
        val leg_comment: MutableList<String>? = AngleBothLegs.getComment()

        AngleBothShoulders.update(kps)
        val shoulder_score: Double = AngleBothShoulders.getScore()
        val shoulder_colorbit: UInt = AngleBothShoulders.getColorbit()
        val shoulder_comment: MutableList<String>? = AngleBothShoulders.getComment()

        AngleBothArms.update(kps)
        val arm_score: Double = AngleBothArms.getScore()
        val arm_colorbit: UInt = AngleBothArms.getColorbit()
        val arm_comment: MutableList<String>? = AngleBothArms.getComment()


        score = leg_ratio * leg_score + arm_ratio * arm_score + shoulder_ratio * shoulder_score
        colorbit = shoulder_colorbit or leg_colorbit or arm_colorbit
        comment = mutableListOf<String>().apply{
            addAll(leg_comment!!)
            addAll(arm_comment!!)
            addAll(shoulder_comment!!)
        }
    }

}