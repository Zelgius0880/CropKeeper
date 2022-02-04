package com.zelgius.cropkeeper.ui.generic

import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zelgius.cropkeeper.ui.legacy.Card3
import com.zelgius.cropkeeper.ui.theme.AppTheme

@Composable
fun TagText(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    borderColor: Color = MaterialTheme.colorScheme.tertiary,
    surfaceColor: Color = MaterialTheme.colorScheme.tertiary,
    onClickListener: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(percent = 50)

    val backgroundColor = if (!isSelected) MaterialTheme.colorScheme.surface else surfaceColor
    val background = remember {
        Animatable(backgroundColor)
    }
    LaunchedEffect(key1 = isSelected) {
        background.animateTo(backgroundColor)
    }

    Text(
        text,
        color = contentColorFor(backgroundColor = backgroundColor),
        modifier = modifier
            .clip(shape)
            .let {
                if (!isSelected) it.border(2.dp, color = borderColor, shape = shape)
                else it.background(background.value)
            }
            .let {
                if (onClickListener != null) {
                    it.clickable { onClickListener() }
                } else it
            }
            .padding(horizontal = 8.dp, vertical = 8.dp),
    )
}

@Preview
@Composable
fun TagTextPreview() {
    Row() {
        var isSelected by remember {
            mutableStateOf(false)
        }

        AppTheme(useDarkTheme = false) {

            Card3() {
                TagText(
                    "Test",
                    Modifier.padding(16.dp),
                    isSelected = isSelected
                ) {
                    isSelected = !isSelected
                }
            }
        }


        AppTheme(useDarkTheme = true) {
            Card3() {
                TagText(
                    "Test",
                    Modifier.padding(16.dp),
                    isSelected
                ) {
                    isSelected = !isSelected
                }
            }
        }
    }
}
