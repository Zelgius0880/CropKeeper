package com.zelgius.mock

import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodHistory
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Seed
import com.zelgius.database.model.SeedWithVegetableAndPeriod
import com.zelgius.database.model.Vegetable
import java.time.LocalDate
import java.util.*
import kotlin.random.Random


val phaseColorSet
    get() = listOf(
        "#B71C1C",
        "#880E4F",
        "#4A148C",
        "#311B92",
        "#1A237E",
        "#0D47A1",
        "#01579B",
        "#006064",
        "#004D40",
        "#1B5E20",
        "#33691E",
        "#827717",
        "#F57F17",
        "#FF6F00",
        "#E65100",
        "#BF360C",
    )

val phaseSample = (1..5).map {
    Phase(
        name = "${if (it == 5) "Long Phase" else "Phase"} $it",
        color = phaseColorSet.random()
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
                periodUid = it.period.periodUid,
                seedUid = ""
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

