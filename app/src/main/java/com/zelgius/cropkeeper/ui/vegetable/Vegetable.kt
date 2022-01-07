package com.zelgius.cropkeeper.ui.vegetable

import com.zelgius.cropkeeper.R
import com.zelgius.cropkeeper.ui.Sample
import com.zelgius.database.model.Vegetable

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
        else -> R.drawable.ic_launcher_foreground
    }

val vegetableSample = Sample {
    (1..5).map {
        Vegetable(name = "Vegetable $it")
    }
}