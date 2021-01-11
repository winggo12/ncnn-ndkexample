package com.cheungbh.yogasdk.criteria

@ExperimentalUnsignedTypes
abstract class CriteriaBase {
    companion object{
        val LEFT = "left"
        val RIGHT = "right"
    }

    abstract fun getScore(): Double

    abstract fun getComment(): MutableList<String>

    abstract fun getColorbit(): UInt

    abstract fun update(kps: Array<Array<Double>>)

    abstract fun set(maxAngle: Double, step: Double, direction: String)

}