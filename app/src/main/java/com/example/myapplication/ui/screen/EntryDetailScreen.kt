package com.example.myapplication.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.myapplication.model.DetailEntrySource
import com.example.myapplication.model.DetailMode
import com.example.myapplication.model.DiaryEntry
import com.example.myapplication.ui.component.EditEntry
import com.example.myapplication.ui.component.MoodPill
import com.example.myapplication.ui.component.PhoneCanvas
import com.example.myapplication.ui.component.SongCoverImage
import com.example.myapplication.ui.component.accentColor
import com.example.myapplication.ui.component.accentSoftColor
import com.example.myapplication.ui.component.cardBorderColor
import com.example.myapplication.ui.theme.InnocenceInk
import com.example.myapplication.ui.theme.InnocenceSubtleText

private val DetailPageShape = RoundedCornerShape(24.dp)
@Composable
fun EntryDetailScreen(
    entry: DiaryEntry,
    cardAlpha: Float,
    customBackgroundUri: String?,
    customBackgroundOffsetX: Float,
    customBackgroundOffsetY: Float,
    customBackgroundBlur: Float,
    themeKey: String,
    customThemeColor: Int,
    startInEditMode: Boolean,
    entrySource: DetailEntrySource,
    strings: AppStrings,
    onBack: () -> Unit,
    onBackToNewEntry: () -> Unit,
    onSave: (DiaryEntry) -> Unit,
    onDelete: (Int) -> Unit
) {
    var mode by remember(entry.id) {
        mutableStateOf(if (startInEditMode) DetailMode.Edit else DetailMode.Read)
    }
    var draft by remember(entry.id) { mutableStateOf(entry) }
    var showDiscardDialog by remember(entry.id) { mutableStateOf(false) }
    var showDeleteDialog by remember(entry.id) { mutableStateOf(false) }
    val hasUnsavedChanges = draft != entry

    fun leaveEdit() {
        if (mode == DetailMode.Edit && hasUnsavedChanges) {
            showDiscardDialog = true
        } else if (mode == DetailMode.Edit) {
            mode = DetailMode.Read
        } else {
            onBack()
        }
    }

    BackHandler(enabled = !showDiscardDialog && !showDeleteDialog) {
        leaveEdit()
    }

    PhoneCanvas(
        backgroundStyle = draft.photo,
        customBackgroundUri = customBackgroundUri,
        customBackgroundOffsetX = customBackgroundOffsetX,
        customBackgroundOffsetY = customBackgroundOffsetY,
        customBackgroundBlur = customBackgroundBlur,
        cardAlpha = cardAlpha,
        themeKey = themeKey,
        customThemeColor = customThemeColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.10f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                DetailTopBar(
                    entry = draft,
                    strings = strings,
                    onBack = { leaveEdit() },
                    onDelete = { showDeleteDialog = true }
                )

                AnimatedContent(
                    targetState = mode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "detailMode"
                ) { current ->
                    if (current == DetailMode.Read) {
                        DetailReadContent(
                            entry = draft,
                            onEdit = { mode = DetailMode.Edit }
                        )
                    } else {
                        DetailEditContent(
                            entry = draft,
                            placeholder = strings.writePlaceholder,
                            onEntryChange = { draft = it },
                            onSave = {
                                onSave(draft)
                                mode = DetailMode.Read
                            }
                        )
                    }
                }
            }
        }

        if (showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { showDiscardDialog = false },
                title = { Text("放弃未保存的修改？") },
                text = { Text("退出后，本次编辑内容会丢失。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            draft = entry
                            mode = DetailMode.Read
                            showDiscardDialog = false
                            if (entrySource == DetailEntrySource.NewEntry) {
                                onBackToNewEntry()
                            }
                        }
                    ) {
                        Text("放弃")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDiscardDialog = false }) {
                        Text("继续编辑")
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("删除这篇日记？") },
                text = { Text("删除后，这条本地记录将无法恢复。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDelete(entry.id)
                        }
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailTopBar(
    entry: DiaryEntry,
    strings: AppStrings,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        SoftSquareIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回",
            onClick = onBack
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = entry.songName,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                color = InnocenceInk,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = InnocenceSubtleText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        SoftSquareIconButton(
            icon = Icons.Default.DeleteOutline,
            contentDescription = strings.delete,
            onClick = onDelete
        )
    }
}

@Composable
private fun DetailReadContent(
    entry: DiaryEntry,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 18.dp)
    ) {
        DiaryNoteCard(
            entry = entry,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer(Modifier.height(20.dp))
        PrimaryDetailButton(
            text = "编辑记录",
            icon = Icons.Default.Edit,
            onClick = onEdit
        )
    }
}

@Composable
private fun DetailEditContent(
    entry: DiaryEntry,
    placeholder: String,
    onEntryChange: (DiaryEntry) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 18.dp)
    ) {
        SectionCard(
            title = "我的感想",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            EditEntry(
                entry = entry,
                placeholder = placeholder,
                onEntryChange = onEntryChange
            )
        }
        Spacer(Modifier.height(20.dp))
        PrimaryDetailButton(
            text = "保存记录",
            icon = Icons.Default.Check,
            onClick = onSave
        )
    }
}

@Composable
private fun DiaryNoteCard(entry: DiaryEntry, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = DetailPageShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
        border = BorderStroke(1.dp, cardBorderColor(strength = 0.72f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            SongNoteHeader(entry = entry)
            DividerLine()
            ReflectionNoteArea(
                entry = entry,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun SongNoteHeader(entry: DiaryEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        SongCoverImage(
            coverUri = entry.coverUri,
            modifier = Modifier.size(104.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = InnocenceSubtleText,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${entry.date} · ${entry.weekday}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InnocenceSubtleText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            MoodPill(text = entry.mood, light = false)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "歌词片段",
                    style = MaterialTheme.typography.titleSmall,
                    color = accentColor(),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = entry.lyricSnippet.ifBlank { "还没有记录歌词片段" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (entry.lyricSnippet.isBlank()) InnocenceSubtleText else InnocenceInk,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReflectionNoteArea(entry: DiaryEntry, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "我的感想",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
            color = accentColor(),
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) {
            DiaryWritingLines()
            Text(
                text = entry.reflection.ifBlank { "还没有记录这首歌带来的感受。" },
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 30.sp),
                color = if (entry.reflection.isBlank()) InnocenceSubtleText else InnocenceInk,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DiaryWritingLines() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(29.dp)
    ) {
        repeat(12) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(accentSoftColor().copy(alpha = 0.72f))
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = DetailPageShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
        border = BorderStroke(1.dp, cardBorderColor(strength = 0.72f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor(),
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
private fun PrimaryDetailButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor().copy(alpha = 0.92f),
            contentColor = Color.White
        )
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SoftSquareIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .background(Color.White.copy(alpha = 0.72f), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = InnocenceInk,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DividerLine(dashed: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (dashed) 1.dp else 1.dp)
            .background(
                cardBorderColor(strength = if (dashed) 0.65f else 0.9f)
            )
    )
}
