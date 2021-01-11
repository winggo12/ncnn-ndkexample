package com.cheungbh.yogasdk.usecases
import android.content.pm.InstrumentationInfo
import android.widget.ArrayAdapter
import cheungbh.net.Person
import com.cheungbh.yogasdk.criteria.CriteriaBase
import com.cheungbh.yogasdk.utilities.classToArray


@ExperimentalUnsignedTypes
class Balasana(
    private val AngleBothLegs: CriteriaBase,
    private val AngleBothWaists: CriteriaBase
): YogaBase() {
    init {
        AngleBothLegs.set(45.0, 10.0, "")
        AngleBothWaists.set(45.0, 10.0, "")

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
    private val leg_ratio: Double = 0.5
    private val waist_ratio: Double = 0.5


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

        AngleBothWaists.update(kps)
        val waist_score: Double = AngleBothWaists.getScore()
        val waist_colorbit: UInt = AngleBothWaists.getColorbit()
        val waist_comment: MutableList<String>? = AngleBothWaists.getComment()


        AngleBothLegs.update(kps)
        val leg_score: Double = AngleBothLegs.getScore()
        val leg_colorbit: UInt = AngleBothLegs.getColorbit()
        val leg_comment: MutableList<String>? = AngleBothLegs.getComment()


        score = leg_ratio * leg_score + waist_ratio * waist_score
        colorbit = waist_colorbit or leg_colorbit
        comment = mutableListOf<String>().apply{
            addAll(leg_comment!!)
            addAll(waist_comment!!)
        }
    }

}
