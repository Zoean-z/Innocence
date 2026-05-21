package com.example.myapplication.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.AppStrings
import com.example.myapplication.model.DiaryEntry
import com.example.myapplication.model.photoStyleForKey
import com.example.myapplication.ui.component.BottomNav
import com.example.myapplication.ui.component.MoodPill
import com.example.myapplication.ui.component.PhoneCanvas
import com.example.myapplication.ui.component.SongCoverImage
import com.example.myapplication.ui.component.accentColor
import com.example.myapplication.ui.component.cardBorderColor
import com.example.myapplication.ui.component.cardSurfaceColor
import com.example.myapplication.ui.theme.InnocenceCardBorder
import com.example.myapplication.ui.theme.InnocenceInk
import com.example.myapplication.ui.theme.InnocenceSubtleText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HomeCardShape = RoundedCornerShape(22.dp)

@Composable
fun HomeScreen(
    entries: List<DiaryEntry>,
    cardAlpha: Float,
    animationsEnabled: Boolean,
    backgroundKey: String,
    customBackgroundUri: String?,
    customBackgroundOffsetX: Float,
    customBackgroundOffsetY: Float,
    customBackgroundBlur: Float,
    themeKey: String,
    customThemeColor: Int,
    strings: AppStrings,
    onOpenEntry: (Int) -> Unit,
    onNewEntry: () -> Unit,
    onOpenSettings: () -> Unit,
    onDeleteEntry: (Int) -> Unit
) {
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    val todayEntry = remember(entries, today) {
        entries.firstOrNull { it.date == today }
    }

    PhoneCanvas(
        backgroundStyle = photoStyleForKey(backgroundKey),
        customBackgroundUri = customBackgroundUri,
        customBackgroundOffsetX = customBackgroundOffsetX,
        customBackgroundOffsetY = customBackgroundOffsetY,
        customBackgroundBlur = customBackgroundBlur,
        cardAlpha = cardAlpha,
        themeKey = themeKey,
        customThemeColor = customThemeColor
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNewEntry,
                    shape = CircleShape,
                    containerColor = accentColor(),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "新增记录")
                }
            },
            bottomBar = {
                BottomNav(
                    selected = "Home",
                    onHome = {},
                    onSettings = onOpenSettings
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    HeaderBlock(onOpenSettings = onOpenSettings)
                }

                if (todayEntry == null) {
                    item {
                        EmptyTodayCard(onNewEntry = onNewEntry)
                    }
                } else {
                    item {
                        TodayEntryCard(
                            entry = todayEntry,
                            cardAlpha = cardAlpha,
                            onClick = { onOpenEntry(todayEntry.id) }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "最近记录",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = InnocenceInk
                        )
                        Text(
                            text = "${entries.size} 条",
                            color = accentColor(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                itemsIndexed(entries, key = { _, entry -> entry.id }) { index, entry ->
                    val rowContent = @Composable {
                        CompactHistoryRow(
                            entry = entry,
                            onClick = { onOpenEntry(entry.id) },
                            onDelete = { onDeleteEntry(entry.id) }
                        )
                    }
                    if (animationsEnabled) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { 24 + index * 6 })
                        ) {
                            rowContent()
                        }
                    } else {
                        rowContent()
                    }
                }

                item {
                    Spacer(Modifier.height(168.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderBlock(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "innocence",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp),
                color = accentColor(),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "记录每一首歌陪你的时刻",
                color = InnocenceSubtleText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Card(
            modifier = Modifier.clickable(onClick = onOpenSettings),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(cardSurfaceColor()),
            border = BorderStroke(1.dp, cardBorderColor()),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = accentColor(),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "个性化",
                    color = accentColor(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TodayEntryCard(
    entry: DiaryEntry,
    cardAlpha: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = HomeCardShape,
        colors = CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "今日记录",
                    color = accentColor(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.songName,
                        modifier = Modifier.padding(end = 118.dp),
                        color = InnocenceInk,
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.artist,
                        modifier = Modifier.padding(end = 118.dp),
                        color = InnocenceSubtleText,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                MoodPill(text = entry.mood, light = false)

                Text(
                    text = entry.lyricSnippet.ifBlank { "还没有记录歌词片段" },
                    color = InnocenceInk,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.reflection.ifBlank { "还没有写下这一刻的感想" },
                    color = InnocenceSubtleText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(16.dp))
            }
            SongCoverImage(
                coverUri = entry.coverUri,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(104.dp)
            )
            Text(
                text = "${entry.date} · ${entry.weekday}",
                color = InnocenceSubtleText,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(top = 120.dp)
            )
        }
    }
}

@Composable
private fun EmptyTodayCard(onNewEntry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = HomeCardShape
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "今天还没有记录",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                fontWeight = FontWeight.SemiBold,
                color = InnocenceInk
            )
            Text(
                text = "写下今天听到的一首歌，它会出现在这里。",
                color = InnocenceSubtleText,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "新建记录",
                color = accentColor(),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable(onClick = onNewEntry)
            )
        }
    }
}

@Composable
private fun CompactHistoryRow(
    entry: DiaryEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = HomeCardShape,
        colors = CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            SongCoverImage(
                coverUri = entry.coverUri,
                modifier = Modifier.size(56.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = entry.songName,
                    color = InnocenceInk,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.artist,
                    color = InnocenceSubtleText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.reflection.ifBlank { entry.lyricSnippet.ifBlank { "还没有摘要" } },
                    color = InnocenceSubtleText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.date,
                    color = InnocenceSubtleText,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoodPill(text = entry.mood, light = false)
                Box {
                    IconButton(onClick = { expanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "更多操作",
                            tint = InnocenceSubtleText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    androidx.compose.material3.DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                expanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard(strings: AppStrings, onNewEntry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor()),
        shape = HomeCardShape
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = strings.noDiaries,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = InnocenceInk
            )
            Text(
                text = strings.noDiariesHint,
                color = InnocenceSubtleText
            )
            Text(
                text = strings.createEntry,
                color = accentColor(),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable(onClick = onNewEntry)
            )
        }
    }
}
