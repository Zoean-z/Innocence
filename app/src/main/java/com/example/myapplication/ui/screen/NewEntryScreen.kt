package com.example.myapplication.ui.screen

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.AppStrings
import com.example.myapplication.model.EntryDraft
import com.example.myapplication.model.photoStyleForKey
import com.example.myapplication.ui.component.FieldBlock
import com.example.myapplication.ui.component.GlassIconButton
import com.example.myapplication.ui.component.MoodGrid
import com.example.myapplication.ui.component.PhoneCanvas
import com.example.myapplication.ui.component.SoftInputRow
import com.example.myapplication.ui.component.SongCoverPicker
import com.example.myapplication.ui.component.accentColor
import com.example.myapplication.ui.component.cardBorderColor
import com.example.myapplication.ui.component.cardSurfaceColor
import com.example.myapplication.ui.component.outlinedFieldColors
import com.example.myapplication.ui.theme.InnocenceInk
import com.example.myapplication.ui.theme.InnocenceSubtleText

@Composable
fun NewEntryScreen(
    draft: EntryDraft,
    cardAlpha: Float,
    backgroundKey: String,
    customBackgroundUri: String?,
    customBackgroundOffsetX: Float,
    customBackgroundOffsetY: Float,
    customBackgroundBlur: Float,
    themeKey: String,
    customThemeColor: Int,
    strings: AppStrings,
    onDraftChange: (EntryDraft) -> Unit,
    onBack: () -> Unit,
    onCreate: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            onDraftChange(draft.copy(coverUri = uri.toString()))
        }
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
            bottomBar = {
                Button(
                    onClick = onCreate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor())
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(strings.createButton)
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    NewEntryHeader(strings = strings, onBack = onBack)
                }

                item {
                    FormCard {
                        FieldBlock(label = strings.songCover) {
                            SongCoverPicker(
                                coverUri = draft.coverUri,
                                onPickCover = { coverPicker.launch(arrayOf("image/*")) }
                            )
                        }
                    }
                }

                item {
                    FormCard {
                        FieldBlock(label = strings.song) {
                            SoftInputRow(
                                icon = Icons.Default.MusicNote,
                                value = draft.songName,
                                placeholder = "给这首歌写个名字",
                                onValueChange = { onDraftChange(draft.copy(songName = it)) }
                            )
                        }
                    }
                }

                item {
                    FormCard {
                        FieldBlock(label = strings.artist) {
                            SoftInputRow(
                                icon = Icons.Default.Person,
                                value = draft.artist,
                                placeholder = "歌手或乐队",
                                onValueChange = { onDraftChange(draft.copy(artist = it)) }
                            )
                        }
                    }
                }

                item {
                    FormCard {
                        FieldBlock(label = strings.date) {
                            SoftInputRow(
                                icon = Icons.Default.CalendarToday,
                                value = draft.date,
                                placeholder = "例如 2026-05-13",
                                onValueChange = { onDraftChange(draft.copy(date = it)) }
                            )
                        }
                    }
                }

                item {
                    FormCard {
                        FieldBlock(label = strings.moodQuestion) {
                            MoodGrid(selectedMood = draft.mood) { onDraftChange(draft.copy(mood = it)) }
                        }
                    }
                }

                item {
                    FormCard {
                        FieldBlock(label = strings.lyricSnippet) {
                            OutlinedTextField(
                                value = draft.lyricSnippet,
                                onValueChange = { onDraftChange(draft.copy(lyricSnippet = it)) },
                                placeholder = { Text("摘下你最想记住的一句歌词") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(20.dp),
                                colors = outlinedFieldColors()
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun NewEntryHeader(strings: AppStrings, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack
            )
            Spacer(Modifier.size(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "新建记录",
                    style = MaterialTheme.typography.headlineSmall,
                    color = accentColor(),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "把今天和这首歌一起记下来。",
                    color = InnocenceSubtleText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FormCard(content: @Composable () -> Unit) {
    androidx.compose.material3.Card(
        shape = RoundedCornerShape(24.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor())
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}
