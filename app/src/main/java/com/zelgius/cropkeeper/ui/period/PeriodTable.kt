package com.zelgius.cropkeeper.ui.period

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import com.zelgius.cropkeeper.ui.Sample
import com.zelgius.cropkeeper.ui.phase.phaseSample
import com.zelgius.cropkeeper.ui.phase.string
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.cropkeeper.ui.theme.toColor
import com.zelgius.cropkeeper.ui.theme.toPx
import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import com.zelgius.mock.dao.FakeProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.Year
import java.util.*
import kotlin.random.Random
import java.time.format.TextStyle as DateTextStyle

private const val CELL_WIDTH = 60
private const val CELL_HEIGHT = 16
private const val HEADER_WIDTH = 100

// FIXME On my Pixel 5 Api 32, the text does not want to wrap to a new line when too long. It works well on Emulator. So a dirty fix is to fix the height of the Text
private val PHASE_NAME_HEIGHT
@Composable
get() =  LocalDensity.current.run {  60.sp.toDp() }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PeriodList(periods: List<PeriodWithPhase>, lazy: Boolean = true) = PeriodListWithHistory(
    periods = periods.map {
        PeriodWithPhaseAndHistory(
            phase = it.phase,
            period = it.period,
            periodHistories = emptyList(),
        )
    }, lazy = lazy
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PeriodListWithHistory(periods: List<PeriodWithPhaseAndHistory>, lazy: Boolean = true) {
    PeriodTableWithHistory(periods, lazy = lazy) {
        val text = it.phase.string(LocalContext.current)

        Row(Modifier.height(IntrinsicSize.Max)) {
            Card(
                shape = RoundedCornerShape(0f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.height(PHASE_NAME_HEIGHT),
            ) {
                Box(modifier = Modifier.fillMaxHeight()) {
                    Text(
                        text = text,
                        color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .width(HEADER_WIDTH.dp)
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            Box(
                Modifier
                    .horizontalScroll(horizontalScrollState)
                    .fillMaxHeight()
            ) {
                val color = it.phase.color.toColor()
                val colorWithAlpha = color.copy(alpha = 0.5f)
                PeriodRange(
                    Triple(it.period.startingMonth, it.period.endingMonth, color),
                    *it.periodHistories.map { h ->
                        Triple(
                            h.startDate.toMonthPercent(),
                            h.endDate.toMonthPercent(),
                            colorWithAlpha
                        )
                    }
                        .toTypedArray()
                )
            }
        }
    }
}

@Composable
private fun PeriodTableWithHistory(
    list: List<PeriodWithPhaseAndHistory>,
    lazy: Boolean = true,
    content: @Composable PeriodTableScope.(period: PeriodWithPhaseAndHistory) -> Unit
) {
    AdaptiveCellWidth {
        if (lazy) {
            LazyColumn {
                item {
                    PeriodTableHeader()
                }

                items(list) {
                    content(it)
                }
            }
        } else {
            Column {
                PeriodTableHeader()

                list.forEach {
                    content(it)
                }
            }
        }
    }
}

@Composable
fun PeriodTableScope.PeriodTableHeader() {
    Card(
        shape = RoundedCornerShape(0f),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row {
            Spacer(modifier = Modifier.width(HEADER_WIDTH.dp))
            Box(Modifier.horizontalScroll(horizontalScrollState)) { MonthHeader() }
        }
    }
}

@Composable
fun PeriodTableScope.MonthHeader() {
    ConstraintLayout {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            var date = LocalDate.now().withMonth(1)
            for (i in 1..12) {
                Box(modifier = Modifier.width(cellWidth.dp)) {
                    Text(
                        text = date.month.getDisplayName(DateTextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                date = date.plusMonths(1)
            }
        }

        val now = with(LocalDate.now()) {
            dayOfYear / Year.of(year).length().toFloat()
        }
        val margin = (now * 12 * cellWidth).dp
        val iconRef = createRef()

        Box(modifier = Modifier.constrainAs(iconRef) {
            start.linkTo(parent.start, margin = margin - 14.7.dp)
            bottom.linkTo(parent.bottom)
        }) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(30.dp)
                    .offset(y = 10.dp)
                    .align(Alignment.BottomCenter)
            )

            Divider(
                color = MaterialTheme.colorScheme.secondary, modifier = Modifier
                    .width(2.dp)
                    .height(2.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun PeriodTableScope.PeriodRange(
    vararg ranges: Triple<Float, Float, Color>
) {
    Surface(
        Modifier
            .width(12.toCell())
            .fillMaxHeight()
    ) {

        // Month separation
        Row(Modifier.fillMaxHeight()) {

            for (i in 1..11) {
                Spacer(modifier = Modifier.width(cellWidth.dp - 1.dp))
                Divider(
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(vertical = 1.dp)
                )
            }
        }

        // Range
        ConstraintLayout(Modifier.fillMaxWidth()) {

            if (ranges.size == 1) {
                val (starting, ending, color) = ranges.first()
                periodBar(starting, ending, color = color, toCell = { toCell() })
            } else {
                var previousRef: ConstrainedLayoutReference? = null
                ranges.forEachIndexed { index, range ->
                    val (starting, ending, color) = range
                    periodBar(
                        starting,
                        ending,
                        color = color,
                        toCell = { toCell() },
                        previous = previousRef,
                        last = index == ranges.size - 1
                    ).also { ref ->
                        previousRef = ref
                    }
                }
            }

            val now = with(LocalDate.now()) {
                dayOfYear / Year.of(year).length().toFloat()
            }

            // Today marker
            Divider(color = MaterialTheme.colorScheme.secondary, modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .constrainAs(createRef()) {
                    start.linkTo(parent.start, margin = (now * 12).toCell() - 0.5.dp)
                })
        }
    }
}

@Composable
private fun ConstraintLayoutScope.periodBar(
    starting: Float,
    ending: Float,
    color: Color,
    toCell: Float.() -> Dp,
    previous: ConstrainedLayoutReference? = null,
    last: Boolean = true
): ConstrainedLayoutReference {
    val cellHeight = if (previous == null) CELL_HEIGHT
    else CELL_HEIGHT / 2
    return if (starting > ending) {
        val ref = createRef()
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .constrainAs(ref) {

                    start.linkTo(parent.start, margin = starting.toCell())
                    end.linkTo(parent.end, margin = 0.dp)
                    if (last && previous == null) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    } else if (previous == null) {
                        top.linkTo(parent.top, margin = 4.dp)
                    } else {
                        top.linkTo(previous.bottom, margin = 2.dp)
                        if (last) bottom.linkTo(parent.bottom, margin = 4.dp)
                    }

                    width = Dimension.value((12 - starting).toCell())
                }
                .background(color)
                .height(cellHeight.dp)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(color)
                .constrainAs(createRef()) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end, margin = (12 - ending).toCell())
                    top.linkTo(ref.top)
                    bottom.linkTo(ref.bottom)
                    width = Dimension.value((ending).toCell())

                }
                .height(cellHeight.dp)

        )

        ref
    } else {
        val ref = createRef()
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .constrainAs(ref) {
                    start.linkTo(parent.start, margin = (starting).toCell())
                    end.linkTo(parent.end, margin = (12 - ending).toCell())

                    if (last && previous == null) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    } else if (previous == null) {
                        top.linkTo(parent.top, margin = 4.dp)
                    } else {
                        top.linkTo(previous.bottom, margin = 2.dp)
                        if (last) bottom.linkTo(parent.bottom, margin = 4.dp)
                    }
                    width = Dimension.value((ending - starting).toCell())
                }
                .height(cellHeight.dp)
                .background(color)
        )

        ref
    }
}

private fun LocalDate.toMonthPercent(): Float =
    dayOfYear / Year.of(year).length().toFloat() * 12

@Composable
fun AdaptiveCellWidth(
    modifier: Modifier = Modifier,
    content: @Composable PeriodTableScope.() -> Unit
) {
    BoxWithConstraints(modifier) {
        val cellWidth = if (maxWidth < 400.dp) CELL_WIDTH.toFloat()
        else (maxWidth.value - 100) / 12f
        val scope = PeriodTableScopeImpl(cellWidth)
        scope.content()
    }
}


interface PeriodTableScope {
    val cellWidth: Float
    val horizontalScrollState: ScrollState
        @Composable get

    fun Int.toCell() = (this * cellWidth).dp
    fun Float.toCell() = (this * cellWidth).dp
}

private data class PeriodTableScopeImpl(override val cellWidth: Float) : PeriodTableScope {
    private var _horizontalScrollState: ScrollState? = null

    override val horizontalScrollState: ScrollState
        @Composable
        get() = _horizontalScrollState ?: rememberScrollState(
            ((LocalDate.now().month.value - 2).coerceAtLeast(0) * cellWidth).dp.toPx()
                .toInt()
        ).also { _horizontalScrollState = it }
}


@Preview
@Composable
fun PreviewPeriod() {
    AppTheme(false) {
        Surface {
            PeriodList(periods = periodSample.value, lazy = false)
        }
    }
}

@Preview(widthDp = 600)
@Composable
fun PreviewPeriodWithHistory() {
    val periods = runBlocking {
        FakeProvider.periodRepository.getPeriodWithHistoryForYear(
            FakeProvider.periodRepository.getPeriodsForVegetable(
                FakeProvider.vegetableRepository.getAll().first().first()
            ),
            2015
        )
    }

    AppTheme {
        PeriodListWithHistory(periods = periods)
    }
}


val periodSample = Sample {
    (1..5).map {
        val start = Random.nextInt(0, 12)
        val end = Random.nextInt(0, 12)
        PeriodWithPhase(
            period = Period(
                startingMonth = start.toFloat(),
                endingMonth = end.toFloat(),
                order = it,
                vegetableUid = "",
                phaseUid = "",
            ),

            phase = phaseSample.value[it - 1]
        )

    }
}
