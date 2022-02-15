package com.zelgius.cropkeeper

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import com.zelgius.cropkeeper.di.AppModule
import com.zelgius.cropkeeper.ui.phase.string
import com.zelgius.cropkeeper.ui.vegetable.string
import com.zelgius.database.AppDatabase
import com.zelgius.database.SeedGroup
import com.zelgius.database.model.FullSeed
import com.zelgius.database.repository.PeriodRepository
import com.zelgius.database.repository.SeedRepository
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt


class AppWidget : AppWidgetProvider() {
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        update(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        intent.extras?.let {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids =
                appWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))

            when (it.getString(EXTRA_ACTION)) {
                ACTION_NEXT -> {
                    val seedUid = it.getString(EXTRA_SEED_UID)
                    val phaseUid = it.getString(EXTRA_PHASE_UID)
                    if (seedUid != null && phaseUid != null)
                        coroutineScope.launch {
                            WidgetHelper.repository(context).updatePhase(seedUid, phaseUid)
                            update(context, appWidgetManager, ids)
                        }
                }
                ACTION_CLOSE -> {
                    val seedUid = it.getString(EXTRA_SEED_UID)
                    if (seedUid != null)
                        coroutineScope.launch {
                            WidgetHelper.repository(context).closeSeed(seedUid)
                            update(context, appWidgetManager, ids)
                        }
                }
            }
        }

    }

    private fun update(context: Context, appWidgetManager: AppWidgetManager, ids: IntArray) {
        coroutineScope.launch {
            WidgetHelper.items(context).first().let {
                val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget_small)
                val builder = RemoteViews.RemoteCollectionItems.Builder()
                it.forEachIndexed { index, seed ->
                    builder.addItem(index.toLong(), constructRemoteViews(context, seed))
                }
                remoteViews.setRemoteAdapter(R.id.vegetable_list, builder.build())
                remoteViews.setPendingIntentTemplate(
                    R.id.vegetable_list, PendingIntent.getBroadcast(
                        context,
                        2,
                        Intent(context, AppWidget::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                )
                appWidgetManager.updateAppWidget(ids, remoteViews)

            }
        }
    }

    private fun constructRemoteViews(
        context: Context,
        item: FullSeed
    ): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.layout_widget_row)

        remoteView.setTextViewText(R.id.name, item.vegetable.string(context))
        remoteView.setTextViewText(R.id.current, item.actualPeriod.phase.string(context))
        remoteView.setTextColor(R.id.current, getColor(item.actualPeriod.phase.color, context))

        val startDays = with(item.actualPeriod.period.startingMonth + 1) {
            val month = LocalDate.now()
                .withMonth(toInt())
                .month

            LocalDate.now()
                .withMonth(month.value)
                .withDayOfMonth(((this - month.value) * month.maxLength()).roundToInt().coerceAtLeast(1).coerceAtMost(month.maxLength()))
                .dayOfYear
        }
        val days = startDays - LocalDate.now().dayOfYear

        remoteView.setViewVisibility(R.id.days, if (days > 0) View.VISIBLE else View.GONE)
        remoteView.setTextViewText(R.id.days, context.getString(R.string.nb_days, days))


        val index =
            item.periods.indexOfFirst { item.actualPeriod.period.periodUid == it.period.periodUid }

        val next = item.periods.getOrNull(index + 1)
        val intent = Intent(context, AppWidget::class.java).apply {
            putExtra(EXTRA_SEED_UID, item.seed.seedUid)
            putExtra(EXTRA_ACTION, if (next == null) ACTION_CLOSE else ACTION_NEXT)
            if (next != null) putExtra(EXTRA_PHASE_UID, next.phase.phaseUid)
        }

        remoteView.setCompoundButtonChecked(R.id.current, false)
        remoteView.setOnCheckedChangeResponse(
            R.id.current, RemoteViews.RemoteResponse.fromFillInIntent(
                intent,
            )
        )


        return remoteView
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        job.cancel()
    }

    companion object {

        private fun getColor(color: String, context: Context): Int = when (color) {
            "#D50000", "#B71C1C" -> {
                context.getColor(R.color.color1)
            }
            "#C51162", "#880E4F" -> {
                context.getColor(R.color.color2)
            }
            "#AA00FF", "#4A148C" -> {
                context.getColor(R.color.color3)
            }
            "#6200EA", "#311B92" -> {
                context.getColor(R.color.color4)
            }
            "#304FFE", "#1A237E" -> {
                context.getColor(R.color.color5)
            }
            "#2962FF", "#0D47A1" -> {
                context.getColor(R.color.color6)
            }
            "#0091EA", "#01579B" -> {
                context.getColor(R.color.color7)
            }
            "#00B8D4", "#006064" -> {
                context.getColor(R.color.color8)
            }
            "#00BFA5", "#004D40" -> {
                context.getColor(R.color.color9)
            }
            "#00C853", "#1B5E20" -> {
                context.getColor(R.color.color10)
            }
            "#64DD17", "#33691E" -> {
                context.getColor(R.color.color11)
            }
            "#AEEA00", "#827717" -> {
                context.getColor(R.color.color12)
            }
            "#FFD600", "#F57F17" -> {
                context.getColor(R.color.color13)
            }
            "#FFAB00", "#FF6F00" -> {
                context.getColor(R.color.color14)
            }
            "#FF6D00", "#E65100" -> {
                context.getColor(R.color.color15)
            }
            "#DD2600", "#BF360C" -> {
                context.getColor(R.color.color16)
            }
            else -> Color.parseColor(color)
        }


        const val ACTION_NEXT = "com.zelgius.cropkeeper.ACTION_NEXT"
        const val ACTION_UPDATE = "com.zelgius.cropkeeper.ACTION_UPDATE"
        const val ACTION_CLOSE = "com.zelgius.cropkeeper.ACTION_CLOSE"
        const val EXTRA_SEED_UID = "EXTRA_SEED_UID"
        const val EXTRA_PHASE_UID = "EXTRA_PHASE_UID"
        const val EXTRA_WIDGET_ID = "EXTRA_WIDGET"
        const val EXTRA_ACTION = "EXTRA_ACTION"
    }
}

object WidgetHelper {
    private var db: AppDatabase? = null
    private fun db(context: Context) =
        if (db == null) {
            AppModule.provideDatabase(context).also {
                db = it
            }
        } else db!!

    private var repository: SeedRepository? = null
    fun repository(context: Context) =
        if (repository == null) {
            val db = db(context)
            val periodRepository =
                PeriodRepository(db.periodDao(), db.periodHistoryDao(), db.phaseDao())

            SeedRepository(
                db.seedDao(),
                db.phaseDao(),
                db.vegetableDao(),
                periodRepository,
                db.fullSeedDao(),
                db.periodHistoryDao()
            ).also { repository = it }
        } else repository!!


    fun items(context: Context): Flow<List<FullSeed>> =
        repository(context).getGroupedFullSeeds()/*.map {
            it.filterKeys { k -> k != SeedGroup.Ended }
        }*/.map {
            it.flatMap { e -> e.value }
        }

    fun update(context: Context) {
        val intent = Intent(context, AppWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, AppWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }
}