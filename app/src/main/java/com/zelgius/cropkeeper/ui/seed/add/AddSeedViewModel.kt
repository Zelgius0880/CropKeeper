package com.zelgius.cropkeeper.ui.seed.add

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Seed
import com.zelgius.database.model.Vegetable
import com.zelgius.database.repository.PeriodRepository
import com.zelgius.database.repository.SeedRepository
import com.zelgius.database.repository.VegetableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddSeedViewModel @Inject constructor(
    private val vegetableRepository: VegetableRepository,
    private val periodRepository: PeriodRepository,
    private val seedRepository: SeedRepository,
    private val application: Application?
) : ViewModel() {
    private lateinit var vegetable: Vegetable
    private var periods: List<PeriodWithPhase> = listOf()

    private val _seed = MutableStateFlow<FullSeed?>(null)
    val seed: Flow<FullSeed>
        get() = _seed.filterNotNull()

    fun loadSeed(uid: String?) {
        if (_seed.value == null) {
            viewModelScope.launch {
                _seed.value = uid?.let {
                    seedRepository.getFull(it)?.also { s ->
                        update(s.vegetable)
                    }
                } ?: emptySeed().first().also { s ->
                    update(s.vegetable)
                }
            }
        }
    }

    fun save() = flow {
        val item = seed.first()
        val phase = actualPhaseFlow.first()
        val vegetable = vegetableFlow.first()
        val actualPeriod = (periods.find { it.phase == phase }
            ?: periods.first()).period

        seedRepository.insertOrUpdate(
            item.seed.copy(
                startDate = if (item.isNew) LocalDate.now() else item.seed.startDate,
                vegetableUid = vegetable.vegetableUid,
                actualPeriodUid = actualPeriod.periodUid
            ),
            actualPeriod = actualPeriod
        )
        emit(Unit)
    }

    val vegetables = vegetableRepository.getAll(application)

    //Fixme I think it is better to use State instead of flows
    private val _vegetableFlow = MutableStateFlow<Vegetable?>(null)
    val vegetableFlow: Flow<Vegetable>
        get() = _vegetableFlow.filterNotNull()

    private val _periodsFlow = MutableStateFlow<List<PeriodWithPhase>?>(null)
    val periodsFlow: Flow<List<PeriodWithPhase>>
        get() = _periodsFlow.filterNotNull()

    private val _phasesFlow = MutableStateFlow<List<Phase>?>(null)
    val phasesFlow: Flow<List<Phase>>
        get() = _phasesFlow.filterNotNull()

    private val _actualPhaseFlow = MutableStateFlow<Phase?>(null)
    val actualPhaseFlow: Flow<Phase>
        get() = _actualPhaseFlow.filterNotNull()

    fun update(vegetable: Vegetable) {
        _vegetableFlow.value = vegetable
        this.vegetable = vegetable

        viewModelScope.launch {
            periods = periodRepository.getPeriodsForVegetable(vegetable)
            _periodsFlow.value = periods
            _phasesFlow.value = periods.map { it.phase }.also {
                _actualPhaseFlow.value = it.first()
            }
        }
    }

    fun updateActualPhase(phase: Phase) {
        _actualPhaseFlow.value = phase
    }

    private fun emptySeed() = flow {
        val vegetable = vegetableRepository.getAll(application).first().first()
        val periods = periodRepository.getPeriodsForVegetable(vegetable)
        val actualPeriod = periods.first()
        emit(
            FullSeed(
                seed = Seed(
                    startDate = LocalDate.now(),
                    vegetableUid = vegetable.vegetableUid,
                    actualPeriodUid = actualPeriod.period.periodUid
                ),
                actualPeriod = actualPeriod,
                periods = periods,
                vegetable = vegetable,
                isNew = true
            )
        )
    }

    fun delete(vegetable: Vegetable) = flow {
        val restore = vegetableRepository.delete(vegetable)
        update(vegetables.first().first())
        emit(restore)
    }

    suspend fun undo(vegetable: Vegetable, periods: List<PeriodWithPhase>) {
        vegetableRepository.insertOrUpdate(vegetable, periods)
        update(vegetables.first().first())
    }
}