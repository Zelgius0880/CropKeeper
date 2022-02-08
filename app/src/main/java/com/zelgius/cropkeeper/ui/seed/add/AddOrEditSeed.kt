package com.zelgius.cropkeeper.ui.seed.add

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zelgius.cropkeeper.CircularRevealEffect
import com.zelgius.cropkeeper.Navigator
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.Action
import com.zelgius.cropkeeper.ui.legacy.Card3
import com.zelgius.cropkeeper.ui.period.PeriodList
import com.zelgius.cropkeeper.ui.phase.PhaseTagList
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.cropkeeper.ui.vegetable.AddOrEditVegetable
import com.zelgius.cropkeeper.ui.vegetable.VegetableDropdown
import com.zelgius.cropkeeper.ui.vegetable.VegetableImage
import com.zelgius.cropkeeper.ui.vegetable.VegetableViewModel
import com.zelgius.cropkeeper.ui.vegetable.drawable
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Vegetable
import com.zelgius.mock.dao.FakeProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalAnimationApi
@Composable
fun AddSeed(
    modifier: Modifier = Modifier,
    navigator: Navigator = Navigator(),
    viewModel: AddSeedViewModel = hiltViewModel()
) {
    var visible by rememberSaveable {
        mutableStateOf(false)
    }

    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxSize()
        ) {}
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val widthOffset = (screenWidth - 56 / 2 - 16f) / screenWidth

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val heightOffset = (screenHeight - 56 / 2 - 16f) / screenHeight

    CircularRevealEffect(visible = visible, offset = Offset(widthOffset, heightOffset)) {
        AddOrEditSeed(modifier, navigator, viewModel)
    }

    LaunchedEffect(key1 = false) {
        viewModel.loadSeed(null)
        visible = true
    }

    BackHandler {
        visible = false
    }

    LaunchedEffect(key1 = visible) {
        delay(100)
        if (!visible) navigator.popBackStack()
    }
}

@Composable
fun EditSeed(
    modifier: Modifier = Modifier,
    navigator: Navigator = Navigator(),
    uid: String,
    viewModel: AddSeedViewModel = hiltViewModel()
) {
    AddOrEditSeed(modifier, navigator, viewModel)

    LaunchedEffect(key1 = false) {
        viewModel.loadSeed(uid)
    }
}

@Composable
fun AddOrEditSeed(
    modifier: Modifier = Modifier,
    navigator: Navigator = Navigator(),
    viewModel: AddSeedViewModel
) {
    var item by remember {
        mutableStateOf<FullSeed?>(null)
    }

    LaunchedEffect(key1 = false) {
        viewModel.seed.collect {
            item = it
        }
    }

    val coroutineScope = rememberCoroutineScope()
    Surface(Modifier.fillMaxSize()) {
        Column {
            SmallTopAppBar(
                title = {
                    Text(text = item?.let { stringResource(id = if (it.isNew) R.string.new_seed else R.string.edit_seed) }
                        ?: "")
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.save().collect {
                                navigator.popBackStack()
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_twotone_save_24),
                            contentDescription = null
                        )
                    }
                })
            item?.let {
                AddOrEditSeed(it, modifier, navigator, viewModel)
            }
        }
    }
}


@OptIn(
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun AddOrEditSeed(
    item: FullSeed,
    modifier: Modifier = Modifier,
    navigator: Navigator = Navigator(),
    viewModel: AddSeedViewModel
) {
    val vegetable by viewModel.vegetableFlow.collectAsState(initial = item.vegetable)
    val periods by viewModel.periodsFlow.collectAsState(initial = item.periods)

    val vegetableViewModel: VegetableViewModel = hiltViewModel()
    val softKeyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, confirmStateChange = {
            if (it == ModalBottomSheetValue.Hidden) {
                softKeyboardController?.hide()
                vegetableViewModel.reset()
            }
            it != ModalBottomSheetValue.HalfExpanded
        })
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        ),
        sheetContent = {
            Surface {
                val snackbarText = stringResource(id = R.string.save_succeed)
                AddOrEditVegetable(
                    vegetableViewModel
                ) {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        snackbarHostState.showSnackbar(
                            snackbarText,
                            null,
                            duration = SnackbarDuration.Short
                        )
                    }

                    viewModel.update(it)
                }
            }
        },
        sheetContentColor = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn {
                item {
                    var vegetables by remember {
                        mutableStateOf<List<Vegetable>>(emptyList())
                    }

                    LaunchedEffect(true) {
                        viewModel.vegetables.collect {
                            vegetables = it
                        }
                    }

                    val snackbarText = stringResource(id = R.string.item_deleted)
                    val snackbarActionText = stringResource(id = R.string.undo)
                    CardVegetable(
                        isNew = item.isNew,
                        vegetable = vegetable,
                        startDate = item.seed.startDate,
                        vegetables = vegetables,
                        onActionSelected = {
                            coroutineScope.launch {
                                when (it) {
                                    Action.Add, Action.Edit -> {
                                        if (it == Action.Add)
                                            vegetableViewModel.reset()
                                        else
                                            vegetableViewModel.reset(vegetable, periods)
                                        bottomSheetState.animateTo(
                                            ModalBottomSheetValue.Expanded
                                        )
                                    }
                                    Action.Delete -> viewModel.delete(vegetable).collect {
                                        val response = snackbarHostState.showSnackbar(
                                            snackbarText,
                                            snackbarActionText,
                                            duration = SnackbarDuration.Short
                                        )
                                        if (response == SnackbarResult.ActionPerformed) {
                                            viewModel.undo(it.first, it.second)
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        viewModel.update(it)
                    }
                }

                item {
                    val phase by viewModel.actualPhaseFlow.collectAsState(initial = item.actualPeriod.phase)
                    val phases by viewModel.phasesFlow.collectAsState(initial = emptyList())
                    CardPhase(phases, phase) {
                        viewModel.updateActualPhase(it)
                    }
                }

                item {
                    CardPeriod(periods)
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

}


@Composable
fun CardVegetable(
    vegetable: Vegetable,
    isNew: Boolean,
    startDate: LocalDate = LocalDate.now(),
    vegetables: List<Vegetable>,
    onActionSelected: (Action) -> Unit,
    onVegetableSelected: (Vegetable) -> Unit
) {
    Card3(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {

            VegetableImage(vegetable = vegetable, size = 64.dp)

            Column(
                Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
            ) {
                if (!isNew) {
                    Text(
                        stringResource(id = R.string.seed_at),
                        color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )

                    Text(
                        startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.padding(bottom = 4.dp, start = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )

                }

                Text(
                    stringResource(id = R.string.add_vegetable_title),
                    color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )

                VegetableDropdown(
                    items = vegetables,
                    vegetable = vegetable,
                    onVegetableSelected = {
                        onVegetableSelected(it)
                    },
                    onActionSelected = onActionSelected,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CardPhase(phases: List<Phase>, phase: Phase, onPhaseSelected: (Phase) -> Unit) {
    Card3(
        Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = stringResource(id = R.string.add_vegetable_phase),
                color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
                style = MaterialTheme.typography.titleSmall,
            )

            PhaseTagList(
                phases = phases,
                modifier = Modifier.padding(bottom = 8.dp),
                selectedPhase = phase, onPhaseClicked = {
                    onPhaseSelected(it)
                }
            )
        }
    }
}

@Composable
fun CardPeriod(periods: List<PeriodWithPhase>, modifier: Modifier = Modifier) {
    Card3(
        modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        PeriodList(periods = periods, lazy = false)
    }
}

@Preview
@Composable
fun AddOrEditSeedPreview() {
    AppTheme(false) {
        val item = runBlocking { FakeProvider.seedRepository.getAllFull().first() }
        AddOrEditSeed(
            viewModel = AddSeedViewModel(
                FakeProvider.vegetableRepository,
                FakeProvider.periodRepository,
                FakeProvider.seedRepository,
                null
            )
        )
    }
}

@Preview
@Composable
fun CardVegetablePreview() {
    AppTheme(false) {
        val item = runBlocking { FakeProvider.seedRepository.getAllFull().first() }
        val context = LocalContext.current
        val vegetables = runBlocking { FakeProvider.vegetableRepository.getAll(context).first() }

        var vegetable by remember {
            mutableStateOf(item.vegetable)
        }

        CardVegetable(
            isNew = false,
            vegetable = vegetable,
            vegetables = vegetables,
            onActionSelected = {}) {
            vegetable = it
        }
    }
}

@Preview
@Composable
fun CardPhasePreview() {
    AppTheme {
        val item = runBlocking { FakeProvider.seedRepository.getAllFull().first() }
        CardPhase(phase = item.actualPeriod.phase, phases = item.periods.map { it.phase }) {}
    }
}

@Preview
@Composable
fun CardPeriodPreview() {
    AppTheme {
        val item = runBlocking { FakeProvider.seedRepository.getAllFull().first() }
        CardPeriod(periods = item.periods)
    }
}