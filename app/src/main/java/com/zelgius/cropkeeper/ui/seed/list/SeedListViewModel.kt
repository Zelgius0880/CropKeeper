package com.zelgius.cropkeeper.ui.seed.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.Phase
import com.zelgius.database.repository.SeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SeedListViewModel @Inject constructor(
    private val seedRepository: SeedRepository
) : ViewModel() {
    val seedsMap = seedRepository.getAllFullFlow().map { list ->
        val now = LocalDate.now()
        val currentMonth = now.month.value
        list.groupBy {
            val start = it.actualPeriod.period.startingMonth
            val end = it.actualPeriod.period.endingMonth
            val phases = it.periods.map { p -> p.phase }
            if (currentMonth.toFloat() in start..end)
                SeedListSeparator.Actual
            else if (phases.indexOf(it.actualPeriod.phase) != phases.size - 1) {
                SeedListSeparator.Planned
            } else {
                SeedListSeparator.Ended
            }
        }.toSortedMap { o1, o2 -> o1.order - o2.order }
    }.asLiveData()

    fun setPhase(item: FullSeed, phase: Phase) {
        viewModelScope.launch {
            seedRepository.updatePhase(item, phase)
        }
    }

    fun closeSeed(item: FullSeed) {
        viewModelScope.launch {
            seedRepository.closeSeed(item)
        }
    }

    fun delete(item: FullSeed){
        viewModelScope.launch {
            seedRepository.delete(item.seed)
        }
    }
}

enum class SeedListSeparator(val order: Int) {
    Actual(1), Planned(2), Ended(0)
}
