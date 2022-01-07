package com.zelgius.cropkeeper.ui.seed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.zelgius.cropkeeper.ui.history.periodWithHistorySample
import com.zelgius.cropkeeper.ui.history.yearsSample
import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import com.zelgius.database.model.Vegetable
import com.zelgius.database.repository.PeriodHistoryRepository
import com.zelgius.database.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeedViewModel @Inject constructor(
    private val periodRepository: PeriodRepository,
    private val periodHistoryRepository: PeriodHistoryRepository
) : ViewModel() {
    fun getYears(vegetable: Vegetable) = liveData {
        emit(periodHistoryRepository.getYears(vegetable))
    }

    fun getPeriodWithHistoryForYear(
        periods: List<PeriodWithPhase>,
        year: Int,
    ) = liveData {
        emit(periodRepository.getPeriodWithHistoryForYear(periods, year))
    }

    fun getPeriodWithHistoryAverage(
        periods: List<PeriodWithPhase>,
    ) = liveData {
        emit(periodRepository.getPeriodWithHistoryAverage(periods))
    }
}