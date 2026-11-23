package com.Zihmaho6.Tekloc.presentation.menu

data class MenuUiState(
    val totalStars: Int = 0,
    val bestScore: Int = 0,
    val streak: Int = 0,
    val currentStage: Int = 0,
    val tutorialSeen: Boolean = false,
    val dailyDone: Boolean = false,
    val dailyStars: Int = 0,
    val coreTint: Long = 0xFF00F5FF
)
