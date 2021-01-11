package com.cheungbh.yogasdk.domain

import com.cheungbh.yogasdk.usecases.Balasana

data class Pose(var poseName: String) {
    companion object {
        const val ArdhaUttanasana = "Ardha Uttanasana"
        const val AdhoMukhaShivanasana = "Adho Mukha Shivanasana"
        const val ArdhaChandarasana = "Ardha Chandarasana"
        const val BaddhaKonasana = "Baddha Konasana"
        const val Bhujangasana = "Bhujangasana"
        const val CaturangaDandasana = "Caturanga Dandasana"
        const val Dandasana = "Dandasana"
        const val Halasana = "Halasana"
        const val Natarajasana = "Natarajasana"
        const val Navasana = "Navasana"
        const val ParivrttaPashvaKonasana = "Parivrtta Pashvakonasana"
        const val ParivrttaTrikonasana = "Parivrtta Trikonasana"
        const val PurnaShalabhasana = "Purna Shalabhasana"
        const val Tuladandasana = "Tuladandasana"
        const val TPose = "TPose"
        const val TposeNew = "TposeNew"
        const val Ustrasana = "Ustrasana"
        const val UbhayaPadangushtasana = "Ubhaya Padangushtasana"
        const val UrdhvaDhanurasana = "Urdhva Dhanurasana"
        const val UttanaPadasana = "Uttana Padasana"
        const val UtthitaHastaPadangusthasanaB = "Utthita Hasta Padangusthasana B"
        const val UtthitaHastaPadangusthasanaC = "Utthita Hasta Padangusthasana C"
        const val UtthitaHastaPadangusthasanaA = "Utthita Hasta Padangusthasana A"
        const val UtthitaParsvakonasana = "Utthita Parsvakonasana"
        const val Vrksasana = "Vrksasana"

        // New Actions in January
        const val Balasana = "Balasana"

    }
}