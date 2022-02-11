package com.zelgius.cropkeeper.ui.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Vegetable
import com.zelgius.database.repository.PhaseRepository
import com.zelgius.database.repository.VegetableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application?,
    private val vegetableRepository: VegetableRepository,
    private val phaseRepository: PhaseRepository,
) : ViewModel() {
    val vegetables = vegetableRepository.getAll(application)

    val phases = phaseRepository.getAll(application)

    fun delete(phase: Phase) {
        viewModelScope.launch {
            phaseRepository.delete(phase)
        }
    }

    fun delete(vegetable: Vegetable) {
        viewModelScope.launch {
            vegetableRepository.delete(vegetable)
        }
    }

    fun savePhase(phase: Phase) {
        viewModelScope.launch {
            phaseRepository.insert(
                phase.copy(color = colors[phase.phaseUid.sumOf { it.code } % colors.size])
            )
        }
    }

    private val colors = listOf(
        "#D50000",
        "#C51162",
        "#AA00FF",
        "#6200EA",
        "#304FFE",
        "#2962FF",
        "#0091EA",
        "#00B8D4",
        "#00BFA5",
        "#00C853",
        "#64DD17",
        "#AEEA00",
        "#FFD600",
        "#FFAB00",
        "#FF6D00",
        "#DD2600",
    )
}