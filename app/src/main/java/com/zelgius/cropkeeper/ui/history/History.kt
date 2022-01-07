package com.zelgius.cropkeeper.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.Sample
import com.zelgius.cropkeeper.ui.generic.TagText
import com.zelgius.cropkeeper.ui.period.periodSample
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.database.model.PeriodHistory
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

@Composable
fun HistoryDropdown(
    list: List<Int>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onItemSelected: (HistoryItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    val items = buildList {
        add(NoneItem)
        add(AverageItem)
        addAll(list.map { YearItem(it) })
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        val shape = RoundedCornerShape(percent = 50)
        Row(
            Modifier
                .clip(shape)
                .clickable(onClick = { expanded = true })
                .border(2.dp, color = MaterialTheme.colorScheme.secondary, shape = shape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =
                when (val i = items[selectedIndex]) {
                    is NoneItem -> stringResource(id = R.string.none)
                    is AverageItem -> stringResource(id = R.string.average)
                    is YearItem -> "${i.year}"
                },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(start = 8.dp),
                color = contentColorFor(backgroundColor = backgroundColor)
            )

            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "",
                tint = contentColorFor(backgroundColor = backgroundColor),
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                //.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onItemSelected(item)
                }) {

                    Text(
                        text = when (item) {
                            is NoneItem -> stringResource(id = R.string.none)
                            is AverageItem -> stringResource(id = R.string.average)
                            is YearItem -> "${item.year}"
                        },
                        color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }
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

val yearsSample = (2010..2022).toList()
val periodWithHistorySample = Sample {
    periodSample.value.map {
        PeriodWithPhaseAndHistory(
            phase = it.phase, period = it.period, periodHistories = (1..5).map { index ->
                PeriodHistory(
                    periodHistoryUid = UUID.randomUUID().toString(),
                    startDate = LocalDate.now().withMonth(Random.nextInt(1, 12))
                        .withYear(yearsSample[index]),
                    endDate = LocalDate.now().withMonth(Random.nextInt(1, 12))
                        .withYear(yearsSample[index]),
                    periodUid = it.period.periodUid
                )
            }
        )
    }
}