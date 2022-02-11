package com.zelgius.cropkeeper.ui.settings

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.zelgius.cropkeeper.Navigator
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.legacy.OutlinedTextField3
import com.zelgius.cropkeeper.ui.phase.string
import com.zelgius.cropkeeper.ui.theme.toColor
import com.zelgius.cropkeeper.ui.vegetable.AddOrEditVegetable
import com.zelgius.cropkeeper.ui.vegetable.VegetableImage
import com.zelgius.cropkeeper.ui.vegetable.VegetableViewModel
import com.zelgius.cropkeeper.ui.vegetable.string
import com.zelgius.database.model.Phase
import kotlinx.coroutines.launch

private val emptyPhase
    get() = Phase(name = "", color = "#fff") // create a new UID at each call

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun Settings(viewModel: SettingsViewModel = hiltViewModel(), navigator: Navigator) {
    val phases by viewModel.phases.collectAsState(emptyList())
    val vegetables by viewModel.vegetables.collectAsState(emptyList())
    val softKeyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val vegetableViewModel: VegetableViewModel = hiltViewModel()

    val bottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, confirmStateChange = {
            if (it == ModalBottomSheetValue.Hidden) {
                softKeyboardController?.hide()
                vegetableViewModel.reset()
            }
            it != ModalBottomSheetValue.HalfExpanded
        })

    var selectedPhase by remember {
        mutableStateOf(emptyPhase)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var isAddDialog by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Surface(Modifier.fillMaxSize()) {
        Column {
            SmallTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
            )

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
                        AddOrEditVegetable(vegetableViewModel) {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                snackbarHostState.showSnackbar(
                                    snackbarText,
                                    null,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                },
                sheetContentColor = MaterialTheme.colorScheme.surface
            ) {
                LazyColumn {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.phases),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Button(onClick = {
                                selectedPhase = emptyPhase
                                isAddDialog = true
                                showDialog = true
                            }) {
                                Icon(
                                    Icons.TwoTone.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    items(items = phases, key = { it.phaseUid }) {

                        Cell(
                            title = it.string(context),
                            modifier = Modifier.animateItemPlacement(),
                            onDeleteClicked = {
                                viewModel.delete(it)
                            },
                            onItemClicked = {
                                selectedPhase = it
                                isAddDialog = false
                                showDialog = true
                            },
                            startContent = {
                                Box(
                                    Modifier
                                        .padding(end = 8.dp)
                                        .clip(CircleShape)
                                        .background(it.color.toColor())
                                        .size(24.dp)
                                )
                            }
                        )
                    }


                    item {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.vegetables),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Button(onClick = {
                                coroutineScope.launch {
                                    vegetableViewModel.reset()
                                    bottomSheetState.show()
                                }
                            }) {
                                Icon(
                                    Icons.TwoTone.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    items(items = vegetables, key = { it.vegetableUid }) {
                        Cell(
                            title = it.string(context),
                            modifier = Modifier.animateItemPlacement(),
                            onDeleteClicked = {
                                viewModel.delete(it)
                            },
                            onItemClicked = {
                                coroutineScope.launch {
                                    vegetableViewModel.reset(it)
                                    bottomSheetState.show()
                                }
                            },
                            startContent = {
                                VegetableImage(
                                    vegetable = it,
                                    size = 36.dp,
                                    padding = 4.dp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        )
                    }

                    item {
                        TextButton(onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    OssLicensesMenuActivity::class.java
                                )
                            )
                        }) {
                            Text(
                                text = stringResource(id = R.string.licenses),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }

                    item {
                        TextButton(onClick = { navigator.navigateToIconLicenses() }) {
                            Text(
                                text = stringResource(id = R.string.icon_licenses),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }



    if (showDialog) {
        PhaseDialog(isAddDialog, selectedPhase, onDismiss = { showDialog = false }) {
            viewModel.savePhase(selectedPhase.copy(name = it))
            showDialog = false
        }
    }

}

@ExperimentalMaterialApi
@Composable
@ExperimentalMaterial3Api
fun Cell(
    title: String,
    modifier: Modifier = Modifier,
    startContent: @Composable (() -> Unit)? = null,
    onItemClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val dismissState = rememberDismissState()
    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onDeleteClicked()
    }

    SwipeToDismiss(
        modifier = modifier,
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
        Card(modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClicked() }) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                startContent?.invoke()
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun PhaseDialog(
    isAddDialog: Boolean,
    phase: Phase = Phase(name = "", color = "#fff"),
    onDismiss: () -> Unit = {},
    onSaveClicked: (name: String) -> Unit
) {
    var textState by remember {
        mutableStateOf(TextFieldValue())
    }

    var isError by remember {
        mutableStateOf(false)
    }

    isError = false
    textState = TextFieldValue(phase.name)

    val softKeyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = if (isAddDialog) R.string.add_phase else R.string.edit_phase)) },
        confirmButton = {
            TextButton(onClick = { onSaveClicked(textState.text) }) {
                Text(stringResource(id = R.string.save))
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.End) {

                OutlinedTextField3(
                    value = textState,
                    onValueChange = { textState = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { softKeyboardController?.hide() }
                    ),
                    label = { Text(text = stringResource(id = R.string.name)) },
                    isError = isError,
                    modifier = Modifier.padding(8.dp)
                )

            }
        })
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Preview
@Composable
fun CellPreview() {
    Cell("Preview", onItemClicked = {}) {}
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun PhaseDialogPreview() {
    PhaseDialog(true) {}
}