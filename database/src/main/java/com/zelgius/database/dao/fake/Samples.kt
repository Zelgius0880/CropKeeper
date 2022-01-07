package com.zelgius.database.dao.fake

import android.graphics.Color
import com.zelgius.database.model.*
import java.time.LocalDate
import java.util.*
import kotlin.random.Random


val phaseColorSet
    get() = listOf(
        0xB71C1C,
        0x880E4F,
        0x4A148C,
        0x311B92,
        0x1A237E,
        0x0D47A1,
        0x01579B,
        0x006064,
        0x004D40,
        0x1B5E20,
        0x33691E,
        0x827717,
        0xF57F17,
        0xFF6F00,
        0xE65100,
        0xBF360C,
    )

val phaseSample = (1..5).map {
    Phase(
        name = "${if (it == 5) "Long Phase" else "Phase"} $it",
        color = Color.valueOf(phaseColorSet.random()).toArgb()
    )
}
val vegetableSample = (1..5).map {
    Vegetable(name = "Vegetable $it", drawableResource = "cauliflower", stringResource = "cauliflower")
}

val periodSample = (1..5).map {
    val start = Random.nextInt(0, 12)
    val end = Random.nextInt(0, 12)


    val phase = phaseSample[it - 1]
    PeriodWithPhase(
        period = Period(
            startingMonth = start.toFloat(),
            endingMonth = end.toFloat(),
            order = it,
            vegetableUid = vegetableSample.first().vegetableUid,
            phaseUid = phase.phaseUid,
        ),

        phase = phase
    )
}

val yearsSample = (2010..2022).toList()
val periodSampleWithPhaseAndHistorySample = periodSample.map {
    PeriodWithPhaseAndHistory(
        phase = it.phase, period = it.period, periodHistories = (1..5).map { index ->
            PeriodHistory(
                startDate = LocalDate.now().withMonth(Random.nextInt(1, 12))
                    .withYear(yearsSample[index]),
                endDate = LocalDate.now().withMonth(Random.nextInt(1, 12))
                    .withYear(yearsSample[index]),
                periodUid = it.period.periodUid
            )
        }
    )
}

val seedSample = (0..4).map {
    val vegetable = vegetableSample[it]

    val period =
        PeriodWithPhase(
            phase = phaseSample.first(),
            period = periodSample.first().period
        )
    SeedWithVegetableAndPeriod(
        Seed(
            seedUid = UUID.randomUUID().toString(),
            startDate = LocalDate.now(),
            vegetableUid = vegetable.vegetableUid,
            actualPeriodUid = period.period.periodUid
        ),
        vegetable = vegetable,
        actualPeriod = period.period
    )
}

