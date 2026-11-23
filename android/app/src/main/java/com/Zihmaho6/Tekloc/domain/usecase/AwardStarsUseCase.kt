package com.Zihmaho6.Tekloc.domain.usecase

class AwardStarsUseCase {

    data class Stars(val target: Boolean, val allGates: Boolean, val underPar: Boolean) {
        val count: Int
            get() = (if (target) 1 else 0) + (if (allGates) 1 else 0) + (if (underPar) 1 else 0)
    }

    operator fun invoke(
        won: Boolean,
        gatesLit: Int,
        gateTotal: Int,
        bumpersUsed: Int,
        par: Int,
        replanUsed: Boolean
    ): Stars = Stars(
        target = won,
        allGates = gateTotal > 0 && gatesLit >= gateTotal,
        underPar = bumpersUsed <= par && !replanUsed
    )
}
