package com.zelgius.cropkeeper.ui.history

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.generic.BorderedDropdown
import com.zelgius.cropkeeper.ui.theme.AppTheme

@Composable
fun HistoryDropdown(
    list: List<Int>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onItemSelected: (HistoryItem) -> Unit
) {
    val items = buildList {
        add(NoneItem)
        add(AverageItem)
        addAll(list.map { YearItem(it) })
    }

    var selectedIndex by remember {
        mutableStateOf(0)
    }

    BorderedDropdown(
        items = items,
        selectedIndex = selectedIndex,
        modifier = modifier,
        backgroundColor = backgroundColor,
        formatting = {
            when (it) {
                is NoneItem -> stringResource(id = R.string.none)
                is AverageItem -> stringResource(id = R.string.average)
                is YearItem -> "${it.year}"
            }
        }) {
        onItemSelected(it)
        selectedIndex = items.indexOf(it).coerceAtLeast(0)
    }
}

@Composable
@Preview
fun HistoryDropdownPreview() {
    AppTheme(useDarkTheme = true) {
        HistoryDropdown(list = (2019..2022).toList(), onItemSelected = {})
    }
}

sealed interface HistoryItem

object NoneItem : HistoryItem
object AverageItem : HistoryItem
data class YearItem(val year: Int) : HistoryItem