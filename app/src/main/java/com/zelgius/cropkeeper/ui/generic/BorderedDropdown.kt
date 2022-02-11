package com.zelgius.cropkeeper.ui.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun <T> BorderedDropdown(
    items: List<T>,
    modifier: Modifier = Modifier,
    dropDownHeight: Dp? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentStart: @Composable (RowScope.(T) -> Unit)? = null,
    formatting: @Composable ((T) -> String)? = null,
    selectedIndex: Int = 0,
    minWidth: Dp = 0.dp,
    onItemSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (items.size > selectedIndex) {
                val item = items[selectedIndex]
                contentStart?.let {
                    this.contentStart(item)
                }
                Text(
                    text = formatting?.invoke(item) ?: item.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .defaultMinSize(minWidth = minWidth)
                        .padding(start = 8.dp),
                    color = contentColorFor(backgroundColor = backgroundColor)
                )
            }

            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "",
                tint = contentColorFor(backgroundColor = backgroundColor),
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        }

        if (expanded) {
            val popupWidth = 200.dp
            val popupHeight = dropDownHeight ?: 250.dp
            val cornerSize = 16.dp

            Popup(alignment = Alignment.TopStart, onDismissRequest = { expanded = false }) {
                // Draw a rectangle shape with rounded corners inside the popup
                Surface(
                    Modifier
                        .size(popupWidth, popupHeight)
                        .background(Color.White, RoundedCornerShape(cornerSize))
                ) {
                    LazyColumn {
                        items(items) { item ->
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    onItemSelected(item)
                                },
                                text = {
                                    Text(
                                        text = formatting?.invoke(item) ?: item.toString()
                                    )
                                })
                        }
                    }
                }
            }
        }
    }
}

