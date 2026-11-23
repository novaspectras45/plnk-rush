package com.Zihmaho6.Tekloc.data.repository

import com.Zihmaho6.Tekloc.data.local.PrefsStore
import com.Zihmaho6.Tekloc.domain.model.AppSettings
import com.Zihmaho6.Tekloc.domain.model.ClockMode
import com.Zihmaho6.Tekloc.domain.model.DropSpeed
import com.Zihmaho6.Tekloc.domain.model.TrajectoryMode
import com.Zihmaho6.Tekloc.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val store: PrefsStore) : SettingsRepository {

    override fun load(): AppSettings = AppSettings(
        trajectory = TrajectoryMode.entries.getOrElse(
            store.readInt(PrefsStore.KEY_TRAJECTORY, 0)
        ) { TrajectoryMode.FULL },
        dropSpeed = DropSpeed.entries.getOrElse(
            store.readInt(PrefsStore.KEY_DROP_SPEED, 1)
        ) { DropSpeed.STANDARD },
        clockMode = ClockMode.entries.getOrElse(
            store.readInt(PrefsStore.KEY_CLOCK, 0)
        ) { ClockMode.ON }
    )

    override fun setTrajectory(mode: TrajectoryMode) {
        store.writeInt(PrefsStore.KEY_TRAJECTORY, mode.ordinal)
    }

    override fun setDropSpeed(speed: DropSpeed) {
        store.writeInt(PrefsStore.KEY_DROP_SPEED, speed.ordinal)
    }

    override fun setClockMode(mode: ClockMode) {
        store.writeInt(PrefsStore.KEY_CLOCK, mode.ordinal)
    }
}
