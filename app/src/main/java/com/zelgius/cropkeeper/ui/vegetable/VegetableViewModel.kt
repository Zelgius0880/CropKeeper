package com.zelgius.cropkeeper.ui.vegetable

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Vegetable
import com.zelgius.database.repository.PhaseRepository
import com.zelgius.database.repository.VegetableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.move
import javax.inject.Inject

@HiltViewModel
class VegetableViewModel @Inject constructor(
    private val vegetableRepository: VegetableRepository,
    private val phaseRepository: PhaseRepository,
    private val application: Application?
) : ViewModel() {


    val phases = phaseRepository.getAll(application).onEach {
        phase = it.first()
    }

    var vegetable = mutableStateOf(Vegetable(name = ""))
    val vegetableNameState =
        mutableStateOf(TextFieldValue(vegetable.value.string(application)))

    var periodRange: ClosedFloatingPointRange<Float> = (1f..13f)

    private var _periods = mutableStateListOf<PeriodWithPhase>()

    var periods: List<PeriodWithPhase>
        get() = _periods
        set(value) {
            _periods.apply {
                clear()
                addAll(value)
            }
        }

    private var _errors = mutableStateListOf<SaveVegetableError>()

    var errors: List<SaveVegetableError>
        get() = _errors
        set(value) {
            _errors.apply {
                clear()
                addAll(value)
            }
        }

    fun savePeriod() {
        _periods.add(
            PeriodWithPhase(
                phase = phase,
                period = Period(
                    startingMonth = periodRange.start - 1,
                    endingMonth = periodRange.endInclusive - 1,
                    phaseUid = phase.phaseUid,
                    vegetableUid = vegetable.value.vegetableUid,
                    order = _periods.size
                )
            )
        )

        periodRange = (1f..12f)
        viewModelScope.launch {
            phase = phases.first().first()
        }
    }


    fun removePeriod(period: PeriodWithPhase) {
        _periods.remove(period)
    }

    fun movePeriod(from: Int, to: Int) {
        _periods.move(from, to)
    }

    lateinit var phase: Phase
    fun cancel() {
        viewModelScope.launch {
            phase = phases.first().first()
        }
    }

    fun save(): Flow<Vegetable?> = flow {
        _errors.clear()
        val result = vegetableNameState.value.text.let {
            if (it.isBlank()) _errors.add(SaveVegetableError.NameEmpty)
            if (periods.isEmpty()) _errors.add(SaveVegetableError.PeriodsEmpty)

            val vegetable = vegetable.value.copy(name = it)

            if (_errors.isEmpty()) {
                vegetableRepository.insertOrUpdate(
                    vegetable,
                    periods
                )

                vegetable
            } else null
        }

        phase = phases.first().first()
        emit(result)
    }

    fun reset(
        vegetable: Vegetable = Vegetable(name = ""),
        periods: List<PeriodWithPhase> = emptyList()
    ) {
        _errors.clear()
        this.vegetable.value = vegetable
        this.periods = periods
        val name = vegetable.string(application)
        this.vegetableNameState.value =
            TextFieldValue(name, selection = TextRange(name.length, name.length))
    }
}

enum class SaveVegetableError {
    NameEmpty, PeriodsEmpty
}