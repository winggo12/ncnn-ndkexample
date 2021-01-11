package com.cheungbh.yogasdk.utilities

import cheungbh.net.KeyPoint
import cheungbh.net.Person
import cheungbh.net.Position
import java.lang.Math.abs

fun KeyPoint.classToPosArray(): Array<Double>{
    return arrayOf(this.position.x.toDouble(), this.position.y.toDouble())
}

fun Person.classToArray(): Array<Array<Double>>{
    val keyPointListSize = this.keyPoints.size
    val returnArray = Array<Array<Double>>(keyPointListSize){ Array<Double>(2){ 0.0 } }
    for ((index, keyPoint) in this.keyPoints.withIndex()){
        returnArray[index][0] = keyPoint.position.x.toDouble()
        returnArray[index][1] = keyPoint.position.y.toDouble()
    }

    return returnArray
}
fun Person.clone(): Person{
    val returnPerson = Person().apply{
        this@clone.score
    }
    val newlist: List<KeyPoint> = this.keyPoints.map{
            keypoint->
        val newposition = Position().apply {
            x = keypoint.position.x
            y = keypoint.position.y
        }
        KeyPoint().apply {
            bodyPart = keypoint.bodyPart
            position = newposition
            score = keypoint.score
        }
    }

    returnPerson.keyPoints = newlist

    return returnPerson
}
fun Person.mirrorX(model_width: Int, model_height: Int): Person{
    val returnPerson = Person().apply{
        this@mirrorX.score
    }
    val newlist: List<KeyPoint> = this.keyPoints.map{
            keypoint->
        val newposition = Position().apply {
            x = abs(model_width - keypoint.position.x)
            y = keypoint.position.y
        }
        KeyPoint().apply {
            bodyPart = keypoint.bodyPart
            position = newposition
            score = keypoint.score
        }
    }

    returnPerson.keyPoints = newlist

    return returnPerson

}