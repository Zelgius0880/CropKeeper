package com.zelgius.cropkeeper.ui.seed.overview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Vegetable
import com.zelgius.database.repository.PeriodHistoryRepository
import com.zelgius.database.repository.PeriodRepository
import com.zelgius.database.repository.SeedRepository
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeedOverviewViewModel @Inject constructor(
    private val seedRepository: SeedRepository,
    private val periodRepository: PeriodRepository,
    private val periodHistoryRepository: PeriodHistoryRepository
) : ViewModel() {
    fun getYears(vegetable: Vegetable) = flow {
        emit(periodHistoryRepository.getYears(vegetable))
    }

    fun getPeriodWithHistoryForYear(
        periods: List<PeriodWithPhase>,
        year: Int,
    ) = viewModelScope.launch {
        _periods.value = periodRepository.getPeriodWithHistoryForYear(periods, year)
    }

    fun getPeriodWithHistoryAverage(
        periods: List<PeriodWithPhase>,
    ) = viewModelScope.launch {
        _periods.value = periodRepository.getPeriodWithHistoryAverage(periods)
    }

    private val _periods = MutableStateFlow<List<PeriodWithPhaseAndHistory>>(emptyList())
    val periods: Flow<List<PeriodWithPhaseAndHistory>>
        get() = _periods

    private val _item = MutableStateFlow<FullSeed?>(null)
    val item: Flow<FullSeed>
        get() = _item.filterNotNull()

    private val _actualPhase = MutableStateFlow<Phase?>(null)
    val actualPhase: Flow<Phase>
        get() = _actualPhase.filterNotNull()

    fun setPhase(phase: Phase) {
        _actualPhase.value = phase
    }

    suspend fun loadSeed(uid: String) {
        _item.value = seedRepository.getFull(uid)?.apply {
            _actualPhase.value = actualPeriod.phase
        }
        getPeriodWithoutHistory()
    }


    fun getPeriodWithoutHistory() {
        _item.value?.let {
            _periods.value = it.periods.map { p ->
                PeriodWithPhaseAndHistory(
                    phase = p.phase,
                    period = p.period,
                    periodHistories = emptyList()
                )
            }
        }
    }
}

