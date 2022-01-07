package com.zelgius.cropkeeper.ui.seed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.getStringByName
import com.zelgius.cropkeeper.ui.history.*
import com.zelgius.cropkeeper.ui.legacy.Card3
import com.zelgius.cropkeeper.ui.period.PeriodListWithHistory
import com.zelgius.cropkeeper.ui.phase.PhaseTagList
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.cropkeeper.ui.vegetable.drawable
import com.zelgius.database.dao.fake.FakeProvider
import com.zelgius.database.model.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun SeedOverview(item: FullSeed, viewModel: SeedViewModel = viewModel()) {
    val title = item.vegetable.stringResource?.let {
        LocalContext.current.getStringByName(it)
    } ?: item.vegetable.name

    var actualPhase by remember {
        mutableStateOf(item.actualPeriod.phase)
    }

    var periods by remember {
        mutableStateOf(item.periods.map {
            PeriodWithPhaseAndHistory(
                phase = it.phase,
                period = it.period,
                periodHistories = emptyList()
            )
        })
    }

    val phase = remember {
        item.periods.map { it.phase }
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = item.vegetable.drawable),
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
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
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
                    CardPhases(phase, item.seed.startDate, actualPhase) {
                        actualPhase = it
                    }
                }

                item {
                    CardHistory(viewModel, item) {
                        periods = it
                    }
                }

                item {
                    Card3(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
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
private fun CardPhases(
    phases: List<Phase>,
    startDate: LocalDate,
    actualPhase: Phase,
    onPhaseSelected: (Phase) -> Unit
) {
    Card3(Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 8.dp)) {
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

@Composable
private fun CardHistory(
    viewModel: SeedViewModel,
    item: FullSeed,
    onPeriodsChange: (List<PeriodWithPhaseAndHistory>) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current


    var years by remember {
        mutableStateOf<List<Int>>(emptyList())
    }

    viewModel.getYears(item.vegetable).observe(lifecycleOwner) {
        years = it
    }
    Card3(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
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
                onItemSelected = { selected ->
                    when (selected) {
                        AverageItem -> viewModel.getPeriodWithHistoryAverage(item.periods)
                            .observe(lifecycleOwner) {
                                onPeriodsChange(it)
                            }

                        NoneItem -> onPeriodsChange(
                            item.periods.map { p ->
                                PeriodWithPhaseAndHistory(
                                    phase = p.phase,
                                    period = p.period,
                                    periodHistories = emptyList()
                                )
                            }
                        )
                        is YearItem -> viewModel.getPeriodWithHistoryForYear(
                            item.periods,
                            selected.year
                        ).observe(lifecycleOwner) {
                            onPeriodsChange(it)
                        }
                    }
                })
        }
    }
}

@Preview
@Composable
fun SeedOverviewPreview() {
    val seed = runBlocking { FakeProvider.seedRepository.getAllFull().first() }

    AppTheme {
        Surface {
            SeedOverview(
                seed,
                viewModel = SeedViewModel(
                    FakeProvider.periodRepository,
                    FakeProvider.periodHistoryRepository
                )
            )
        }
    }
}
