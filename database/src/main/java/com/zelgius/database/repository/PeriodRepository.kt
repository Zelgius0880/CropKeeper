package com.zelgius.database.repository

import com.zelgius.database.dao.PeriodDao
import com.zelgius.database.dao.PeriodHistoryDao
import com.zelgius.database.dao.PhaseDao
import com.zelgius.database.model.*
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import javax.inject.Inject

class PeriodRepository @Inject constructor(
    private val periodDao: PeriodDao,
    private val periodHistoryDao: PeriodHistoryDao,
    private val phaseDao: PhaseDao
) {
    suspend fun insert(vararg periods: Period) = periodDao.insert(*periods)
    suspend fun delete(vararg periods: Period) = periodDao.delete(*periods)
    suspend fun update(vararg periods: Period) = periodDao.update(*periods)

    suspend fun getAll() = periodDao.getAll()

    suspend fun getPeriodWithHistoryForYear(
        periods: List<PeriodWithPhase>,
        year: Int
    ): List<PeriodWithPhaseAndHistory> = periods.map {
        PeriodWithPhaseAndHistory(
            period = it.period,
            phase = it.phase,
            periodHistories = periodHistoryDao.getPeriodWithHistoryForYear(
                it.period.periodUid,
                year
            )
        )
    }


    suspend fun getPeriodWithHistoryAverage(periods: List<PeriodWithPhase>) =
        with(periodDao.getPeriodWithHistory(periods.map { it.period.periodUid })) {
            coroutineScope {
                map {
                    PeriodWithPhaseAndHistory(phase = it.phase,
                        period = it.period,
                        periodHistories = if (it.periodHistories.isNotEmpty()) {
                            listOf(
                                PeriodHistory(
                                    startDate = LocalDate.now()
                                        .withDayOfYear(it.periodHistories.sumOf { h -> h.startDate.dayOfYear } / it.periodHistories.size),
                                    endDate = LocalDate.now()
                                        .withDayOfYear(it.periodHistories.sumOf { h -> h.endDate.dayOfYear } / it.periodHistories.size),
                                    periodUid = it.period.periodUid
                                )
                            )
                        } else emptyList())
                }
            }
        }

    suspend fun getPeriodsForVegetable(vegetable: Vegetable) = coroutineScope {
        periodDao.getPeriodsForVegetable(vegetable.vegetableUid).map {
            PeriodWithPhase(
                period = it,
                phase = phaseDao.get(it.phaseUid)
            )
        }
    }
}