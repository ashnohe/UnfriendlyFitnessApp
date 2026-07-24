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

data class UglyNewsArticle(
    val id: String,
    val headline: String,
    val summary: String,
    val timeAgo: String
)

class MyWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val articles = listOf(
            UglyNewsArticle(
                id = "1",
                headline = "🚨 BURPEES OFFICIALLY BANNED WORLDWIDE!!",
                summary = "Scientists declare exercise causes extreme soreness & regret.",
                timeAgo = "2 MINS AGO"
            ),
            UglyNewsArticle(
                id = "2",
                headline = "🥑 NEW DIET: ICE CUBES & DUST ONLY",
                summary = "Guaranteed 50lb weight loss in under 30 minutes.",
                timeAgo = "15 MINS AGO"
            ),
            UglyNewsArticle(
                id = "3",
                headline = "🚌 LOCAL MAN BENCHES ENTIRE CITY BUS",
                summary = "Passersby reportedly stunned and completely confused.",
                timeAgo = "1 HOUR AGO"
            ),
            UglyNewsArticle(
                id = "4",
                headline = "😴 GYM CLOSED PERMANENTLY FOR NAP TIME",
                summary = "Management states all members are simply too tired to lift.",
                timeAgo = "3 HOURS AGO"
            )
        )

        provideContent {
            GlanceTheme {
                UglyNewsWidgetContent(articles = articles)
            }
        }
    }
}

class MyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyWidget()
}

@Composable
fun UglyNewsWidgetContent(articles: List<UglyNewsArticle>) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF8A2BE2), Color(0xFF8A2BE2))) // Electric Purple
            .padding(4.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            Text(
                text = "💥 FITNESS NEWS FLASH 💥",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFFFFF00), Color(0xFFFFFF00)), // Bright Yellow
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(ColorProvider(Color(0xFFFF4500), Color(0xFFFF4500))) // Bright Orange
                    .padding(8.dp)
            )

            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                items(articles) { article ->
                    UglyNewsArticleRow(article)
                }
            }
        }
    }
}

@Composable
fun UglyNewsArticleRow(article: UglyNewsArticle) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(Color(0xFF00FF00), Color(0xFF00FF00))) // Bright Lime Green
            .padding(8.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = article.headline,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFFF0000), Color(0xFFFF0000)), // Bright Red
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = article.timeAgo,
                style = TextStyle(
                    color = ColorProvider(Color(0xFF000000), Color(0xFF000000)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }

        Text(
            text = article.summary,
            style = TextStyle(
                color = ColorProvider(Color(0xFF0000FF), Color(0xFF0000FF)), // Bright Blue
                fontSize = 12.sp
            ),
            modifier = GlanceModifier.padding(top = 4.dp)
        )
    }
}
