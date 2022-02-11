package com.zelgius.cropkeeper.ui.vegetable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.generic.BorderedDropdown
import com.zelgius.cropkeeper.ui.generic.RangeSlider
import com.zelgius.cropkeeper.ui.legacy.OutlinedTextField3
import com.zelgius.cropkeeper.ui.phase.string
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Vegetable
import com.zelgius.mock.dao.FakeProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.draggedItem
import org.burnoutcrew.reorderable.rememberReorderState
import org.burnoutcrew.reorderable.reorderable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun AddOrEditVegetable(
    viewModel: VegetableViewModel = hiltViewModel(),
    onSaved: (vegetable: Vegetable) -> Unit = {}
) {
    val name by remember {
        viewModel.vegetableNameState
    }

    var addVisible by remember {
        mutableStateOf(false)
    }

    val errors = viewModel.errors
    val nameError = errors.contains(SaveVegetableError.NameEmpty)
    val periodsError = errors.contains(SaveVegetableError.PeriodsEmpty)

    val coroutineScope = rememberCoroutineScope()
    Column(Modifier.padding(8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val softKeyboardController = LocalSoftwareKeyboardController.current

            Column {

                OutlinedTextField3(
                    value = name,
                    onValueChange = { viewModel.vegetableNameState.value = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { softKeyboardController?.hide() }
                    ),
                    label = { Text(text = stringResource(id = R.string.name)) },
                    isError = nameError
                )

                AnimatedVisibility(visible = nameError) {
                    Text(
                        text = stringResource(id = R.string.name_cannot_be_empty),
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error)
                    )
                }

            }


            Button(modifier = Modifier.padding(start = 8.dp), onClick = {
                coroutineScope.launch {
                    viewModel.save().collect {
                        if (it != null) onSaved(it)
                    }
                }
            }) {
                Icon(painterResource(id = R.drawable.ic_twotone_save_24), contentDescription = "")
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = stringResource(id = R.string.periods),
                    style = MaterialTheme.typography.titleLarge
                )
                AnimatedVisibility(visible = periodsError) {
                    Text(
                        text = stringResource(id = R.string.periods_cannot_be_empty),
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error)
                    )
                }
            }

            Row {
                AnimatedVisibility(visible = addVisible) {
                    OutlinedButton(onClick = {
                        addVisible = false
                        viewModel.cancel()
                    }, Modifier.padding(end = 8.dp)) {
                        Icon(Icons.TwoTone.Close, contentDescription = "")
                    }
                }
                AnimatedAddButton(isAdd = addVisible) {
                    if (addVisible) viewModel.savePeriod()
                    addVisible = !addVisible
                }
            }
        }

        val reorderableState = rememberReorderState()

        LazyColumn(
            modifier = Modifier
                .heightIn(0.dp, 150.dp)
                .reorderable(reorderableState, { from, to ->
                    viewModel.movePeriod(from.index, to.index)
                }), state = reorderableState.listState
        ) {
            itemsIndexed(
                items = viewModel.periods,
                key = { _, item -> item.period.periodUid }) { index, item ->
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                        .draggedItem(reorderableState.offsetByKey(item.period.periodUid))
                        .detectReorderAfterLongPress(reorderableState)
                ) {
                    Column(
                        Modifier
                            .padding(horizontal = 4.dp)
                    ) {
                        if (index > 0) Divider(Modifier.padding(horizontal = 4.dp))
                        PeriodItem(item = item, Modifier.padding(vertical = 4.dp)) {
                            viewModel.removePeriod(it)
                        }
                    }
                }
            }
        }

        val phases by viewModel.phases.collectAsState(initial = emptyList())
        AnimatedVisibility(visible = addVisible) {
            PeriodEdition(
                phases,
                selectedPhase = null,
                modifier = Modifier.padding(top = 8.dp),
                onPhaseSelected = {
                    viewModel.phase = it
                },
                onPeriodRangeChange = {
                    viewModel.periodRange = it
                })
        }
    }
}


@Composable
fun PeriodEdition(
    phases: List<Phase>,
    modifier: Modifier = Modifier,
    selectedPhase: Phase? = null,
    onPhaseSelected: (Phase) -> Unit,
    onPeriodRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    var selectedIndex by remember {
        mutableStateOf(phases.indexOf(selectedPhase).coerceAtLeast(0))
    }

    var inverted by remember {
        mutableStateOf(false)
    }

    Column(modifier.padding(bottom = 16.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            BorderedDropdown(items = phases, selectedIndex = selectedIndex, onItemSelected = {
                onPhaseSelected(it)
                selectedIndex = phases.indexOf(it)
            }, formatting = { it.string(LocalContext.current) })

            IconButton(onClick = {
                inverted = !inverted
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_twotone_compare_arrows_24),
                    contentDescription = ""
                )
            }
        }

        PeriodRange(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            inverted = inverted,

            ) {
            if (!inverted) onPeriodRangeChange(it)
            else onPeriodRangeChange(it.endInclusive..it.start)
        }
    }
}


@Composable
private fun PeriodRange(
    modifier: Modifier = Modifier,
    inverted: Boolean = false,
    onPeriodRangeChange: ((ClosedFloatingPointRange<Float>) -> Unit)
) {
    Box(Modifier.height(72.dp)) {

        val activeColor = MaterialTheme.colorScheme.primary
        val inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)


        Column(Modifier.align(Alignment.BottomCenter)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                repeat(13) {
                    Divider(
                        Modifier
                            .height(8.dp)
                            .width(1.dp), thickness = 1.dp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                repeat(12) {
                    Text(
                        text = MONTH_DATE_FORMATTER.format(LocalDate.now().withMonth(it + 1))
                            .substring(0..0).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 1.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        RangeSlider(
            valueFrom = 1f,
            valueTo = 13f,
            stepSize = 0.25f,
            modifier = modifier.align(Alignment.TopCenter),
            isTickVisible = false,
            formatter = { it.toMonthString() },
            trackColor = if (!inverted) activeColor else inactiveColor,
            inactiveTrackColor = if (!inverted) inactiveColor else activeColor,
            onValueChange = onPeriodRangeChange
        )

    }
}

private fun Float.toMonthString() = LocalDate.now().withMonth(toInt().coerceAtMost(12)).let {
    PERIOD_DATE_FORMATTER.format(
        it.withDayOfMonth(
            ((this - it.month.value) * it.month.maxLength()).roundToInt()
                .coerceAtLeast(1)
                .coerceAtMost(it.month.maxLength())
        )
    )
}

@ExperimentalAnimationApi
@Composable
fun AnimatedAddButton(isAdd: Boolean, onClicked: () -> Unit) {
    FilledTonalButton(onClick = onClicked) {
        AnimatedContent(
            targetState = isAdd,
            transitionSpec = {
                // Compare the incoming number with the previous number.
                if (targetState) {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() with
                            slideOutHorizontally { width -> width } + fadeOut()
                }.using(
                    // Disable clipping since the faded slide-in/out should
                    // be displayed out of bounds.
                    SizeTransform(clip = false)
                )
            }
        ) { targetBoolean ->

            if (!targetBoolean) Icon(Icons.TwoTone.Add, contentDescription = "")
            else Icon(Icons.TwoTone.Check, contentDescription = "")
        }
    }
}

private val PERIOD_DATE_FORMATTER by lazy {
    DateTimeFormatter.ofPattern(
        "d MMMM",
        Locale.getDefault()
    )
}

private val MONTH_DATE_FORMATTER by lazy {
    DateTimeFormatter.ofPattern(
        "MMM",
        Locale.getDefault()
    )
}

@Composable
fun PeriodItem(
    item: PeriodWithPhase,
    modifier: Modifier = Modifier,
    onDelete: (PeriodWithPhase) -> Unit
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = item.phase.string(LocalContext.current),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val startMonth = item.period.startingMonth + 1
            val endMonth = item.period.endingMonth + 1

            Text(
                text = "${startMonth.toMonthString()} - ${endMonth.toMonthString()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
            )
        }

        IconButton(onClick = { onDelete(item) }) {
            Icon(Icons.TwoTone.Delete, contentDescription = null)
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class
)
@Composable
@Preview
fun AddOrEditVegetablePreview() {
    AppTheme(isSystemInDarkTheme()) {
        Surface {
            AddOrEditVegetable(
                viewModel = VegetableViewModel(
                    FakeProvider.vegetableRepository,
                    FakeProvider.periodRepository,
                    FakeProvider.phaseRepository,
                    null
                )
            )
        }
    }
}