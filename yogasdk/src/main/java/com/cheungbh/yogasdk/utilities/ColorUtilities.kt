package com.cheungbh.yogasdk.utilities

@kotlin.ExperimentalUnsignedTypes
object ColorUtilities {
    var score_thresh: Double = 80.0

    fun left_arm(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1110000000000u
        }
        return pose_bit
    }

    fun right_arm(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1000110000000u
        }
        return pose_bit
    }

    fun left_leg(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1000000001100u
        }
        return pose_bit
    }

    fun right_leg(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1000000000011u
        }
        return pose_bit
    }

    fun left_shoulder(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1011000000000u
        }
        return pose_bit
    }

    fun right_shoulder(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1001100000000u
        }
        return pose_bit
    }

    fun left_waist(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1010001000000u
        }
        return pose_bit
    }

    fun right_waist(score: Double) : UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1000100010000u
        }
        return pose_bit
    }

    fun between_leg(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = 0b1000000111100u
        }
        return pose_bit
    }

    fun left_handkneeankle(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun right_handkneeankle(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun left_shoulderhandankle(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun right_shoulderhandankle(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun distLeftHandAnkle(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun distRightHandAnkle(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }


    fun distLeftShoulderKnee(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun distRightShoulderKnee(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun distLeftElbowKnee(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun distRightElbowKnee(score: Double): UInt{
        var pose_bit: UInt = 0b1000000000000u
        if (score < score_thresh) {
            pose_bit = unChangedColor()
        }
        return pose_bit
    }

    fun unChangedColor(): UInt{
        return 0b1000000000000u
    }

}