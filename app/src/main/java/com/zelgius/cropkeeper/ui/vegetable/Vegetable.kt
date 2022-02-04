package com.zelgius.cropkeeper.ui.vegetable

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.zelgius.common.getStringByName
import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.Action
import com.zelgius.cropkeeper.ui.generic.BorderedDropdown
import com.zelgius.cropkeeper.ui.theme.AppTheme
import com.zelgius.cropkeeper.ui.theme.toPx
import com.zelgius.database.model.Vegetable
import com.zelgius.mock.dao.FakeProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun VegetableDropdown(
    items: List<Vegetable>,
    vegetable: Vegetable,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onActionSelected: (Action) -> Unit = {},
    onVegetableSelected: (Vegetable) -> Unit,
) {
    BorderedDropdown(
        items = items, onItemSelected = onVegetableSelected, modifier = modifier,
        formatting = {
            it.string(LocalContext.current)
        },
        dropDownHeight = 250.dp,
        selectedIndex = items.indexOf(vegetable).let { if (it < 0) 0 else it },
        minWidth = 100.dp,
        contentStart = {
            var expanded by remember { mutableStateOf(false) }

            Icon(
                Icons.TwoTone.MoreVert,
                contentDescription = "",
                tint = contentColorFor(backgroundColor = backgroundColor),
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Action.values().forEach { item ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        onActionSelected(item)
                    }) {

                        Text(
                            text = when (item) {
                                Action.Add -> stringResource(id = R.string.new_vegetable)
                                Action.Edit -> stringResource(id = R.string.edit_vegetable)
                                Action.Delete -> stringResource(id = R.string.delete_vegetable)
                            },
                            color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            }

        }
    )
}

@Composable
fun VegetableImage(
    vegetable: Vegetable,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    padding: Dp = 8.dp
) {
    Box(
        modifier
            .height(size)
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        val drawableId = vegetable.drawable
        if (drawableId != null)
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "",
                Modifier.padding(padding)
            )
        else
            Text(
                text = vegetable.string(LocalContext.current).substring(0,2)
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineLarge
                    .copy(
                        fontSize = with(LocalDensity.current) { (size/2).toSp() },
                        color = MaterialTheme.colorScheme.onSecondary
                    ),
                modifier = Modifier.align(Alignment.Center)
            )
    }
}

@Composable
@Preview
fun VegetableDropdownPreview() {
    AppTheme() {
        val vegetables = runBlocking {
            FakeProvider.vegetableRepository.getAll().first()
        }
        VegetableDropdown(items = vegetables, vegetables[1]) {}
    }
}

@Composable
@Preview
fun VegetableImagePreview() {
    AppTheme() {
        val vegetables = runBlocking {
            FakeProvider.vegetableRepository.getAll().first()
        }

        Row {
            VegetableImage(vegetable = vegetables.first(), size = 64.dp)
            VegetableImage(vegetable = Vegetable(name = "Vegetable"), size = 64.dp)
        }
    }
}

val Vegetable.drawable
    get() = when (drawableResource) {
        "artichoke" -> R.drawable.artichoke
        "asparagus" -> R.drawable.asparagus
        "aubergine" -> R.drawable.aubergine
        "beans" -> R.drawable.beans
        "beetroot" -> R.drawable.beetroot
        "broccoli" -> R.drawable.broccoli
        "brussels_sprouts" -> R.drawable.brussels_sprouts
        "butternut_squash" -> R.drawable.butternut_squash
        "carrot" -> R.drawable.carrot
        "cauliflower" -> R.drawable.cauliflower
        "celery" -> R.drawable.celery
        "chard" -> R.drawable.chard
        "chilly" -> R.drawable.chilly
        "courgette" -> R.drawable.courgette
        "cucumber" -> R.drawable.cucumber
        "garlic" -> R.drawable.garlic
        "leek" -> R.drawable.leek
        "lettuce" -> R.drawable.lettuce
        "onion" -> R.drawable.onion
        "parsnip" -> R.drawable.parsnip
        "pea" -> R.drawable.pea
        "pepper" -> R.drawable.pepper
        "potato" -> R.drawable.potato
        "pumpkin" -> R.drawable.pumpkin
        "radish" -> R.drawable.radish
        "shallot" -> R.drawable.shallot
        "spinach_summer" -> R.drawable.spinach
        "spinach_winter" -> R.drawable.spinach
        "tomato_outdoors" -> R.drawable.tomato
        "tomato_green_house" -> R.drawable.tomato
        else -> null
    }

fun Vegetable.string(context: Context?): String = stringResource?.let {
    context?.getStringByName(it)
} ?: name