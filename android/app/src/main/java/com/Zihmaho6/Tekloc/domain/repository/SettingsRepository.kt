package com.Zihmaho6.Tekloc.domain.repository

import com.Zihmaho6.Tekloc.domain.model.AppSettings
import com.Zihmaho6.Tekloc.domain.model.ClockMode
import com.Zihmaho6.Tekloc.domain.model.DropSpeed
import com.Zihmaho6.Tekloc.domain.model.TrajectoryMode

interface SettingsRepository {
    fun load(): AppSettings
    fun setTrajectory(mode: TrajectoryMode)
    fun setDropSpeed(speed: DropSpeed)
    fun setClockMode(mode: ClockMode)
}
