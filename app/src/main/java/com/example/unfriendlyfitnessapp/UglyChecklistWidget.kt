package com.example.unfriendlyfitnessapp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

data class UglyChecklistItem(
    val title: String,
    val isDone: Boolean
)

class UglyChecklistWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val items = listOf(
            UglyChecklistItem("DO 100 BURPEES RIGHT NOW!!", false),
            UglyChecklistItem("DRINK 5 GALLONS OF WATER", true),
            UglyChecklistItem("RUN 50 MILES NO REST", false),
            UglyChecklistItem("EAT BROCCOLI ONLY", false),
            UglyChecklistItem("LIFT 500 LBS BENCH PRESS", true)
        )

        provideContent {
            GlanceTheme {
                UglyChecklistWidgetContent(items = items)
            }
        }
    }
}

class UglyChecklistWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UglyChecklistWidget()
}

@Composable
fun UglyChecklistWidgetContent(items: List<UglyChecklistItem>) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFFFFF00), Color(0xFFFFFF00)))
            .padding(4.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            Text(
                text = "⚠️ UGLY CHECKLIST ⚠️",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFFF0000), Color(0xFFFF0000)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(ColorProvider(Color(0xFF00FFFF), Color(0xFF00FFFF)))
                    .padding(8.dp)
            )

            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                items(items) { item ->
                    UglyRowItem(item)
                }
            }
        }
    }
}

@Composable
fun UglyRowItem(item: UglyChecklistItem) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(
                if (item.isDone) ColorProvider(Color(0xFFFF00FF), Color(0xFFFF00FF))
                else ColorProvider(Color(0xFF00FF00), Color(0xFF00FF00))
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (item.isDone) " [X] " else " [ ] ",
            style = TextStyle(
                color = ColorProvider(Color(0xFF000000), Color(0xFF000000)),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        Text(
            text = item.title,
            style = TextStyle(
                color = ColorProvider(Color(0xFF0000FF), Color(0xFF0000FF)),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
    }
}
