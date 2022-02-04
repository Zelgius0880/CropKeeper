package com.zelgius.cropkeeper.ui.phase

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zelgius.common.getStringByName
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.Sample
import com.zelgius.cropkeeper.ui.generic.TagText
import com.zelgius.cropkeeper.ui.theme.phaseColorSet
import com.zelgius.database.model.Phase

@Composable
fun PhaseTagList(
    phases: List<Phase>,
    modifier: Modifier = Modifier,
    selectedPhase: Phase? = null,
    onPhaseClicked: (Phase) -> Unit = {}
) {
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(phases) { index, phase ->
            val text = phase.string(LocalContext.current)

            if (index > 0) Icon(
                painterResource(id = R.drawable.ic_baseline_arrow_right_24),
                contentDescription = "",
                Modifier.width(18.dp),
                tint = contentColorFor(
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Box(modifier = Modifier.padding(horizontal = 2.dp)) {
                TagText(
                    text = text,
                    isSelected = selectedPhase == phase
                ) {
                    onPhaseClicked(phase)
                }
            }
        }

    }

    LaunchedEffect(selectedPhase) {
        listState.animateScrollToItem(index = (phases.indexOfFirst { it == selectedPhase }).coerceAtLeast(
            0
        ))
    }
}

val phaseSample = Sample {
    (1..5).map {
        Phase(
            name = "${if (it == 5) "Long Phase" else "Phase"} $it",
            color = String.format(
                "#%08X",
                0xFFFFFFFF and MaterialTheme.colorScheme.phaseColorSet.random().value.toLong()
            )
        )
    }
}

fun Phase.string(context: Context): String = stringResource?.let {
    context.getStringByName(it)
} ?: name