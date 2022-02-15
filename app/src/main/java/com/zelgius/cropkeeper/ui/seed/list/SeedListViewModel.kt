package com.zelgius.cropkeeper.ui.seed.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zelgius.cropkeeper.WidgetHelper
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
    private val seedRepository: SeedRepository,
    private val application: Application? = null
) : ViewModel() {
    val seedsMap get() = seedRepository.getGroupedFullSeeds().asLiveData()

    fun setPhase(item: FullSeed, phase: Phase) {
        viewModelScope.launch {
            seedRepository.updatePhase(item, phase)
            application?.let {
                WidgetHelper.update(it)
            }
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

