package com.zelgius.cropkeeper.ui.seed.list

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.phase.PhaseTagList
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.cropkeeper.ui.vegetable.VegetableImage
import com.zelgius.cropkeeper.ui.vegetable.string
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.Phase
import com.zelgius.mock.dao.FakeProvider
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun SeedList(
    map: Map<SeedListSeparator, List<FullSeed>>,
    modifier: Modifier = Modifier,
    onSeedClicked: (FullSeed) -> Unit,
    onPhaseClicked: (FullSeed, Phase) -> Unit,
    onItemDelete: (FullSeed) -> Unit,
    onClose: (FullSeed) -> Unit,
) {
    if (map.isEmpty()) {
        Box(modifier) {
            Text(text = stringResource(id = R.string.nothing_to_display),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 128.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        BoxWithConstraints(modifier) {
            val cell: @Composable LazyItemScope.(item: FullSeed) -> Unit = {
                SeedCell(
                    item = it,
                    onSeedClicked = onSeedClicked,
                    onSeedClosed = onClose,
                    onPhaseSelected = { phase ->
                        onPhaseClicked(it, phase)
                    },
                    onItemDelete = {
                        onItemDelete(it)
                    }
                )
            }

            if (maxWidth >= 600.dp) {
                Column {
                    map.forEach { (separator, list) ->
                        SeedSeparator(separator)

                        LazyVerticalGrid(cells = GridCells.Adaptive(300.dp)) {
                            items(list) {
                                Box {
                                    cell(it)
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn {
                    items(map.flatMap { entry ->
                        buildList {
                            add(entry.key)
                            addAll(entry.value)
                        }
                    }, key = {
                        when (it) {
                            is SeedListSeparator -> it.name
                            is FullSeed -> it.seed.seedUid
                            else -> ""
                        }
                    }) {
                        Box(modifier = Modifier.animateItemPlacement()) {
                            when (it) {
                                is SeedListSeparator -> SeedSeparator(it)
                                is FullSeed -> cell(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeedSeparator(separator: SeedListSeparator) {
    Text(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(top = 16.dp),
        text = stringResource(
            id = when (separator) {
                SeedListSeparator.Actual -> R.string.actual
                SeedListSeparator.Planned -> R.string.planned
                SeedListSeparator.Ended -> R.string.ended
            }
        ),
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
fun LazyItemScope.SeedCell(
    item: FullSeed,
    onSeedClicked: (FullSeed) -> Unit,
    onPhaseSelected: (Phase) -> Unit,
    onSeedClosed: (FullSeed) -> Unit,
    onItemDelete: () -> Unit
) {
    val dismissState = rememberDismissState()
    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onItemDelete()
    }

    SwipeToDismiss(
        modifier = Modifier.animateItemPlacement(),
        directions = setOf(DismissDirection.EndToStart),
        state = dismissState,
        background = {
            Card(
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    Icon(
                        Icons.TwoTone.Delete,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 16.dp, vertical = 8.dp),

                        )
                }
            }
        }) {
        Card(
            Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp, bottom = 4.dp)
                .clickable { onSeedClicked(item) }
        ) {
            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val (image, name, date, phase) = createRefs()

                VegetableImage(
                    vegetable = item.vegetable,
                    size = 64.dp,
                    modifier = Modifier.constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(phase.bottom)
                    })


                Text(
                    item.vegetable.string(LocalContext.current),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .constrainAs(name) {
                            start.linkTo(image.end)
                            top.linkTo(parent.top)
                        }
                )
                Text(
                    item.seed.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = ContentAlpha.medium),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(bottom = 4.dp, start = 8.dp)
                        .constrainAs(date) {
                            start.linkTo(name.end)
                            linkTo(name.end, parent.end, bias = 1f)
                            linkTo(name.top, name.bottom)
                        }
                )


                val additionalRef = if (item.nextPhaseStarted || item.isLast) {
                    createRef()
                } else null

                var actualPhase by remember {
                    mutableStateOf(item.actualPeriod.phase)
                }

                Box(
                    Modifier
                        .padding(top = 8.dp, start = 8.dp, bottom = 8.dp)
                        .constrainAs(phase) {
                            linkTo(image.end, parent.end)
                            if (additionalRef != null)
                                top.linkTo(additionalRef.bottom)
                            else
                                bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                        }
                ) {
                    PhaseTagList(
                        phases = item.periods.map { it.phase },
                        selectedPhase = actualPhase, onPhaseClicked = {
                            actualPhase = it
                            onPhaseSelected(it)
                        }
                    )
                }

                if (additionalRef != null)
                    createVerticalChain(
                        name,
                        phase,
                        additionalRef,
                        chainStyle = ChainStyle.SpreadInside
                    )
                else
                    createVerticalChain(name, phase, chainStyle = ChainStyle.SpreadInside)

                val constraints: ConstrainScope.() -> Unit = {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                if (item.isLast && additionalRef != null) {
                    Button(
                        onClick = { onSeedClosed(item) },
                        modifier = Modifier
                            .height(32.dp)
                            .constrainAs(additionalRef) {
                                this.constraints()
                            },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.TwoTone.Check,
                            contentDescription = "",
                            modifier = Modifier.padding(4.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.end_it),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                } else if (item.nextPhaseStarted && additionalRef != null) {
                    Row(Modifier.constrainAs(additionalRef) {
                        this.constraints()
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.TwoTone.Warning,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = stringResource(id = R.string.next_phase_started),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class
)
@Preview
@Composable
fun SeedCellPreview() {
    val seed = runBlocking {
        FakeProvider.seedRepository.getAllFull().first()
    }
    AppTheme {
        Surface {
            LazyColumn {
                items(listOf(seed)) {
                    SeedCell(
                        item = seed,
                        onSeedClicked = {},
                        onPhaseSelected = {},
                        onSeedClosed = {},
                        onItemDelete = {}
                    )
                }

            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class
)
@Preview
@Composable
fun SeedCellClosePreview() {
    val seed = runBlocking {
        val item = FakeProvider.seedRepository.getAllFull().first()
        item.copy(actualPeriod = item.periods.last())
    }
    AppTheme {
        Surface {
            LazyColumn {
                items(listOf(seed)) {
                    SeedCell(
                        item = seed,
                        onSeedClicked = {},
                        onPhaseSelected = {},
                        onSeedClosed = {},
                        onItemDelete = {}
                    )
                }
            }
        }
    }
}


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class
)
@Preview
@Composable
fun SeedCellWarningPreview() {
    val seed = runBlocking {
        val item = FakeProvider.seedRepository.getAllFull().first()
        val month = LocalDate.now().month.value
        val actualDateStart = LocalDate.now().minusMonths(2).month.value - 1f
        val actualDateEnd = LocalDate.now().minusMonths(1).month.value - 1f
        item.copy(
            actualPeriod = item.actualPeriod.copy(
                period = item.actualPeriod.period.copy(
                    startingMonth = actualDateStart,
                    endingMonth = actualDateEnd
                )
            ),
            periods = item.periods.mapIndexed { index, periodWithPhase ->
                if (index != 1) periodWithPhase else periodWithPhase.copy(
                    period = periodWithPhase.period.copy(
                        startingMonth = month - 1f,
                        endingMonth = month.toFloat()
                    )
                )
            }
        )
    }
    AppTheme(true) {
        Surface {
            LazyColumn {
                items(listOf(seed)) {
                    SeedCell(
                        item = seed,
                        onSeedClicked = {},
                        onPhaseSelected = {},
                        onSeedClosed = {},
                        onItemDelete = {}
                    )
                }
            }
        }
    }
}


@Preview(widthDp = 600)
@Composable
fun SeedListPreview() {
    val map = runBlocking {

        val now = LocalDate.now()
        val currentMonth = now.month.value
        FakeProvider.seedRepository.getAllFull().groupBy {
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
    }

    AppTheme(true) {
        Surface {
            val context = LocalContext.current
            SeedList(
                map = map,
                onSeedClicked = {
                    Toast.makeText(context, "Seed Clicked", Toast.LENGTH_SHORT).show()
                },
                onPhaseClicked = { _, _ ->
                    Toast.makeText(context, "Phase Clicked", Toast.LENGTH_SHORT).show()
                },
                onClose = {}, onItemDelete = {})
        }
    }
}