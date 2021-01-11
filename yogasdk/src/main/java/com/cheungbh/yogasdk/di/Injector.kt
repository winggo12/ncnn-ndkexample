package com.cheungbh.yogasdk.di

import android.content.Context
import cheungbh.net.Net
import com.cheungbh.yogasdk.domain.Pose
import com.cheungbh.yogasdk.usecases.*
import com.cheungbh.yogasdk.usecases.previous.*

/** [Injector] will give the implementation of [YogaBase]
 * If you want a set the pose to Natarajasana, you can assign Injector(Pose.NATARAJASANA) to to this variable.
 * After setting the pose, you can use the score calculation and comment generation function w.r.t. this pose.
 * */
object Injector {

    private lateinit var netLibrary: Net
    private val processImage by lazy { ProcessImage() }

    private val adhoMukhaShivanasanaFeedback by lazy{ AdhoMukhaShivanasana() }
    private val ardhaChandarasanaFeedback by lazy{ ArdhaChandarasana() }
    private val ardhaUttanasanaFeecback by lazy{ ArdhaUttanasana() }
    private val badhaKonasanaFeedback by lazy{ BaddhaKonasana() }
    private val bujangasanaFeedback by lazy{ Bhujangasana() }
    private val caturangaDandasanaFeedback by lazy{ CaturangaDandasana() }
    private val dandasanaFeedback by lazy { Dandasana() }
    private val halasanaFeedback by lazy { Halasana() }
    private val natarajasanaFeedback by lazy{ Natarajasana() }
    private val navasanaFeedback by lazy{ Navasana() }
    private val parivrttaPashvaKonasanaFeedback by lazy { ParivrttaPashvaKonasana() }
    private val parivrttaTrikonasanaFeedback by lazy { ParivrttaTrikonasana() }
    private val purnaShalabhasanaFeedback by lazy { PurnaShalabhasana() }
    private val tuladandasanaFeecback by lazy{ Tuladandasana() }

    private val ustrasanaFeedback by lazy{ Ustrasana() }
    private val uttanaPadasanaFeedback by lazy { UttanaPadasana() }
    private val ubhayaPadangushtasanaFeedback by lazy { UbhayaPadangushtasana() }
    private val urdhvaDhanurasanaFeedback by lazy { UrdhvaDhanurasana() }
    private val utthitaParsvakonasanaFeedback by lazy { UtthitaParsvakonasana() }
    private val utthitaHastaPadangusthasanaAFeedback by lazy { UtthitaHastaPadangusthasanaA() }
    private val utthitaHastaPadangusthasanaBFeedback by lazy { UtthitaHastaPadangusthasanaA() }
    private val utthitaHastaPadangusthasanaCFeedback by lazy { UtthitaHastaPadangusthasanaC() }
    private val vrksasanaFeecback by lazy { Vrksasana() }


    // New actions in January
    private val Balasana by lazy { Balasana(AngleBothLegs, AngleBothWaists) }
    private val tpose by lazy{ TposeNew2(AngleBothLegs, AngleBothShoulders, AngleBothArms)}


    private val leftAngleSingleArm by lazy { AngleSingleArm() }
    private val rightAngleSingleArm by lazy { AngleSingleArm() }
    private val leftAngleSingleLeg by lazy { AngleSingleLeg() }
    private val rightAngleSingleLeg by lazy { AngleSingleLeg() }
    private val leftAngleSingleShoulder by lazy { AngleSingleShoulder() }
    private val rightAngleSingleShoulder by lazy { AngleSingleShoulder() }
    private val leftAngleSingleWaist by lazy { AngleSingleWaist() }
    private val rightAngleSingleWaist by lazy { AngleSingleWaist() }
    private val leftAngleSingleHandKneeAnkle by lazy { AngleBothHandKneeAnkle() }
    private val rightAngleSingleHandKneeAnkle by lazy { AngleBothHandKneeAnkle() }
    private val leftAngleSingleShoulderHandAnkle by lazy { AngleBothShoulderHandAnkle() }
    private val rightAngleSingleShoulderHandAnkle by lazy { AngleSingleShoulderHandAnkle() }

    private val AngleBothLegs by lazy { AngleBothLegs() }
    private val AngleBothShoulders by lazy { AngleBothShoulders() }
    private val AngleBothArms by lazy { AngleBothArms() }
    private val AngleBothWaists by lazy { AngleBothWaists() }
    private val AngleBetweenLegs by lazy { AngleBetweenLegs() }
    private val AngleBothShoulderHandAnkle by lazy { AngleBothShoulderHandAnkle() }
    private val AngleBothHandKneeAnkle by lazy { AngleBothHandKneeAnkle() }


    fun selectPose(poseName: String): YogaBase {
        return when(poseName){
            Pose.CaturangaDandasana -> caturangaDandasanaFeedback
            Pose.Natarajasana -> natarajasanaFeedback
            Pose.Navasana -> navasanaFeedback
            Pose.Ustrasana -> ustrasanaFeedback
            Pose.BaddhaKonasana -> badhaKonasanaFeedback
            Pose.Bhujangasana -> bujangasanaFeedback
            Pose.AdhoMukhaShivanasana -> adhoMukhaShivanasanaFeedback
            Pose.ArdhaChandarasana -> ardhaChandarasanaFeedback
            Pose.Tuladandasana -> tuladandasanaFeecback
            Pose.TPose -> tpose
//            Pose.TPoseNew2 -> tposenew
            Pose.UtthitaParsvakonasana -> utthitaParsvakonasanaFeedback
            Pose.UtthitaHastaPadangusthasanaA -> utthitaHastaPadangusthasanaAFeedback
            Pose.UtthitaHastaPadangusthasanaB -> utthitaHastaPadangusthasanaBFeedback
            Pose.UtthitaHastaPadangusthasanaC -> utthitaHastaPadangusthasanaCFeedback
            Pose.Vrksasana -> vrksasanaFeecback
            Pose.Dandasana -> dandasanaFeedback
            Pose.ParivrttaPashvaKonasana -> parivrttaPashvaKonasanaFeedback
            Pose.ParivrttaTrikonasana -> parivrttaTrikonasanaFeedback
            Pose.PurnaShalabhasana -> purnaShalabhasanaFeedback
            Pose.UbhayaPadangushtasana -> ubhayaPadangushtasanaFeedback
            Pose.UrdhvaDhanurasana -> urdhvaDhanurasanaFeedback
            Pose.UttanaPadasana -> uttanaPadasanaFeedback
            Pose.Halasana -> halasanaFeedback

            // New actions in January

            Pose.Balasana -> Balasana
            
            else -> ardhaUttanasanaFeecback
        }
    }

    fun setNetLibrary(context: Context){

        netLibrary = Net(context)
    }
    fun closeLibrary(){
        netLibrary.close()
    }
    fun getNetLibrary(): Net = netLibrary
    fun getProcessImg(): ProcessImage = processImage
}