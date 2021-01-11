package com.cheungbh.yogasdk.usecases

import cheungbh.net.Person


abstract class YogaBase{

    abstract val poseName : String
    /** set private var result: Person */
    abstract fun setResult(result: Person)
    /** get private var result: Person */
    abstract fun getResult(): Person
    /** get private var score: Double */
    abstract fun getScore(): Double

    abstract fun getDetailedScore(): List<Double>

    /** get private var comment: List<String> */
    abstract fun getComment(): List<String>

//    @kotlin.ExperimentalUnsignedTypes
//    open fun getColorBit(): UInt

    @kotlin.ExperimentalUnsignedTypes
    open fun getColorBit(): UInt{
        return 0b1000000000000u
    }

}