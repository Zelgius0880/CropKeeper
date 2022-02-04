package com.zelgius.cropkeeper.ui.seed.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zelgius.database.repository.SeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
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
}

enum class SeedListSeparator(val order: Int) {
    Actual(1), Planned(2), Ended(0)
}
