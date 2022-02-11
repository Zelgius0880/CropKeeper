package com.zelgius.cropkeeper.ui.seed.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zelgius.common.getStringByName
import com.zelgius.cropkeeper.Navigator
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.history.AverageItem
import com.zelgius.cropkeeper.ui.history.HistoryDropdown
import com.zelgius.cropkeeper.ui.history.HistoryItem
import com.zelgius.cropkeeper.ui.history.NoneItem
import com.zelgius.cropkeeper.ui.history.YearItem
import com.zelgius.cropkeeper.ui.period.PeriodListWithHistory
import com.zelgius.cropkeeper.ui.phase.PhaseTagList
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.cropkeeper.ui.vegetable.VegetableImage
import com.zelgius.cropkeeper.ui.vegetable.drawable
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Vegetable
import com.zelgius.mock.dao.FakeProvider
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
@ExperimentalMaterial3Api
fun SeedOverview(
    uid: String,
    navigator: Navigator = Navigator(),
    viewModel: SeedOverviewViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsState(initial = null)

    Surface(Modifier.fillMaxSize()) {
        item?.let {
            SeedOverview(item = it, viewModel = viewModel, navigator = navigator)
        }
    }

    LaunchedEffect(key1 = uid) {
        viewModel.loadSeed(uid)
    }
}

@Composable
@ExperimentalMaterial3Api
fun SeedOverview(
    item: FullSeed,
    navigator: Navigator = Navigator(),
    viewModel: SeedOverviewViewModel = hiltViewModel()
) {
    val title = item.vegetable.stringResource?.let {
        LocalContext.current.getStringByName(it)
    } ?: item.vegetable.name

    val actualPhase by viewModel.actualPhase.collectAsState(initial = item.actualPeriod.phase)

    val periods by viewModel.periods.collectAsState(initial = emptyList())
    val phases = periods.map { it.phase }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(
                id = item.vegetable.drawable ?: R.drawable.ic_launcher_foreground
            ),
            contentDescription = "Background",
            Modifier
                .alpha(.1f)
                .align(Alignment.BottomCenter)
                .aspectRatio(1f)
                .padding(32.dp)
                .fillMaxSize()
        )

        Column {

            SmallTopAppBar(
                title = { Text(text = title, color = MaterialTheme.colorScheme.onSurface) },
                actions = {
                    IconButton(onClick = {
                        navigator.navigateToEditSeed(item.seed)
                    }) {
                        Icon(
                            Icons.TwoTone.Edit,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )

            LazyColumn {
                item {
                    CardPhases(item.vegetable, phases, item.seed.startDate, actualPhase) {
                        viewModel.setPhase(it)
                    }
                }

                item {
                    CardHistory(viewModel, item) {
                        when (it) {
                            AverageItem -> viewModel.getPeriodWithHistoryAverage(item.periods)

                            NoneItem -> viewModel.getPeriodWithoutHistory()
                            is YearItem -> viewModel.getPeriodWithHistoryForYear(
                                item.periods,
                                it.year
                            )
                        }
                    }
                }

                item {
                    Card(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Column {
                            PeriodListWithHistory(periods = periods, lazy = false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
private fun CardPhases(
    vegetable: Vegetable,
    phases: List<Phase>,
    startDate: LocalDate,
    actualPhase: Phase,
    onPhaseSelected: (Phase) -> Unit
) {
    Card(Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VegetableImage(vegetable = vegetable, modifier = Modifier.padding(8.dp))

            Column(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(id = R.string.seed_at),
                    color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )

                PhaseTagList(
                    phases = phases,
                    selectedPhase = actualPhase,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    onPhaseSelected(it)
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
private fun CardHistory(
    viewModel: SeedOverviewViewModel,
    item: FullSeed,
    onHistorySelectionChange: (HistoryItem) -> Unit
) {

    val years by viewModel.getYears(item.vegetable).collectAsState(initial = emptyList())

    Card(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(id = R.string.show_history),
                style = MaterialTheme.typography.titleSmall
            )

            HistoryDropdown(
                list = years,
                modifier = Modifier.padding(8.dp),
                onItemSelected = {
                    onHistorySelectionChange(it)
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SeedOverviewPreview() {
    val seed = runBlocking { FakeProvider.seedRepository.getAllFull().first() }

    AppTheme {
        SeedOverview(
            item = seed,
            viewModel = SeedOverviewViewModel(
                FakeProvider.seedRepository,
                FakeProvider.periodRepository,
                FakeProvider.periodHistoryRepository
            )
        )

    }
}
