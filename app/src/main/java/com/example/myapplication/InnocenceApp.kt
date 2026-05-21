package com.example.myapplication

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.data.local.InnocenceDatabase
import com.example.myapplication.model.AppSettings
import com.example.myapplication.model.DetailEntrySource
import com.example.myapplication.model.DiaryEntry
import com.example.myapplication.model.EntryDraft
import com.example.myapplication.model.FieldPhoto
import com.example.myapplication.model.HarborPhoto
import com.example.myapplication.model.MeadowPhoto
import com.example.myapplication.model.NightPhoto
import com.example.myapplication.model.Screen
import com.example.myapplication.model.appStrings
import com.example.myapplication.repository.DataStoreSettingsRepository
import com.example.myapplication.repository.DiaryRepository
import com.example.myapplication.repository.RoomDiaryRepository
import com.example.myapplication.repository.SettingsRepository
import com.example.myapplication.ui.screen.EntryDetailScreen
import com.example.myapplication.ui.screen.HomeScreen
import com.example.myapplication.ui.screen.NewEntryScreen
import com.example.myapplication.ui.screen.SettingsScreen
import com.example.myapplication.ui.theme.InnocenceTheme
import kotlinx.coroutines.launch

@Composable
fun InnocenceApp() {
    val context = LocalContext.current.applicationContext
    val database = remember(context) { InnocenceDatabase.get(context) }
    val diaryRepository = remember(database) { RoomDiaryRepository(database.diaryDao()) }
    val settingsRepository = remember(context) { DataStoreSettingsRepository(context) }

    InnocenceApp(
        diaryRepository = diaryRepository,
        settingsRepository = settingsRepository
    )
}

@Composable
private fun InnocenceApp(
    diaryRepository: DiaryRepository,
    settingsRepository: SettingsRepository
) {
    val scope = rememberCoroutineScope()
    val entries by diaryRepository.observeEntries().collectAsState(initial = emptyList())
    val settings by settingsRepository.settings.collectAsState(initial = AppSettings())
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }
    var selectedEntryId by rememberSaveable { mutableStateOf(0) }
    var openDetailInEditMode by rememberSaveable { mutableStateOf(false) }
    var detailEntrySource by rememberSaveable { mutableStateOf(DetailEntrySource.Home) }
    var draft by remember { mutableStateOf(EntryDraft()) }

    LaunchedEffect(settings.hasSeededDefaults) {
        diaryRepository.seedDefaultsIfNeeded(defaultEntries(), settings.hasSeededDefaults)
        if (!settings.hasSeededDefaults) {
            settingsRepository.setDefaultsSeeded()
        }
    }

    LaunchedEffect(entries) {
        if (selectedEntryId == 0 && entries.isNotEmpty()) {
            selectedEntryId = entries.first().id
        }
    }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        if (settings.animationsEnabled) {
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    when {
                        initialState == Screen.Home && targetState == Screen.EntryDetail -> {
                            (fadeIn(animationSpec = androidx.compose.animation.core.tween(320, easing = FastOutSlowInEasing)) +
                                scaleIn(
                                    initialScale = 0.82f,
                                    animationSpec = androidx.compose.animation.core.tween(
                                        durationMillis = 360,
                                        easing = FastOutSlowInEasing
                                    )
                                )).togetherWith(
                                fadeOut(animationSpec = androidx.compose.animation.core.tween(180))
                            ).using(SizeTransform(clip = false))
                        }

                        initialState == Screen.EntryDetail && targetState == Screen.Home -> {
                            fadeIn(animationSpec = androidx.compose.animation.core.tween(220)).togetherWith(
                                fadeOut(animationSpec = androidx.compose.animation.core.tween(220)) +
                                    scaleOut(
                                        targetScale = 0.92f,
                                        animationSpec = androidx.compose.animation.core.tween(
                                            durationMillis = 280,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                            ).using(SizeTransform(clip = false))
                        }

                        else -> {
                            ContentTransform(
                                targetContentEnter = fadeIn(
                                    animationSpec = androidx.compose.animation.core.tween(220)
                                ),
                                initialContentExit = fadeOut(
                                    animationSpec = androidx.compose.animation.core.tween(180)
                                ),
                                sizeTransform = SizeTransform(clip = false)
                            )
                        }
                    }
                },
                label = "screen"
            ) { current ->
                InnocenceRoute(
                    screen = current,
                    entries = entries,
                    selectedEntryId = selectedEntryId,
                    settings = settings,
                    draft = draft,
                    onDraftChange = { draft = it },
                    onSelectEntry = { entryId ->
                        selectedEntryId = entryId
                        openDetailInEditMode = false
                        detailEntrySource = DetailEntrySource.Home
                        screen = Screen.EntryDetail
                    },
                    onNewEntry = {
                        draft = EntryDraft()
                        screen = Screen.NewEntry
                    },
                    onCreateEntry = { entry ->
                        scope.launch {
                            val saved = diaryRepository.addEntry(entry)
                            selectedEntryId = saved.id
                            openDetailInEditMode = true
                            detailEntrySource = DetailEntrySource.NewEntry
                            screen = Screen.EntryDetail
                        }
                    },
                    onSaveEntry = { updated ->
                        scope.launch { diaryRepository.updateEntry(updated) }
                    },
                    onDeleteEntry = { id ->
                        scope.launch {
                            diaryRepository.deleteEntry(id)
                            selectedEntryId = 0
                            screen = Screen.Home
                        }
                    },
                    onCardAlphaChange = { value ->
                        scope.launch { settingsRepository.setCardAlpha(value) }
                    },
                    onAnimationsEnabledChange = { value ->
                        scope.launch { settingsRepository.setAnimationsEnabled(value) }
                    },
                    onLanguageChange = { value ->
                        scope.launch { settingsRepository.setLanguage(value) }
                    },
                    onBackgroundChange = { value ->
                        scope.launch { settingsRepository.setBackgroundKey(value) }
                    },
                    onCustomBackgroundChange = { value ->
                        scope.launch { settingsRepository.setCustomBackgroundUri(value) }
                    },
                    onCustomBackgroundOffsetChange = { x, y ->
                        scope.launch { settingsRepository.setCustomBackgroundOffset(x, y) }
                    },
                    onCustomBackgroundBlurChange = { value ->
                        scope.launch { settingsRepository.setCustomBackgroundBlur(value) }
                    },
                    onThemeChange = { value ->
                        scope.launch { settingsRepository.setThemeKey(value) }
                    },
                    onCustomThemeColorChange = { value ->
                        scope.launch { settingsRepository.setCustomThemeColor(value) }
                    },
                    onOpenSettings = { screen = Screen.Settings },
                    onHome = {
                        openDetailInEditMode = false
                        screen = Screen.Home
                    },
                    startDetailInEditMode = openDetailInEditMode,
                    detailEntrySource = detailEntrySource,
                    onBackToNewEntry = { screen = Screen.NewEntry }
                )
            }
        } else {
            InnocenceRoute(
                screen = screen,
                entries = entries,
                selectedEntryId = selectedEntryId,
                settings = settings,
                draft = draft,
                onDraftChange = { draft = it },
                onSelectEntry = { entryId ->
                    selectedEntryId = entryId
                    openDetailInEditMode = false
                    detailEntrySource = DetailEntrySource.Home
                    screen = Screen.EntryDetail
                },
                onNewEntry = {
                    draft = EntryDraft()
                    screen = Screen.NewEntry
                },
                onCreateEntry = { entry ->
                    scope.launch {
                        val saved = diaryRepository.addEntry(entry)
                        selectedEntryId = saved.id
                        openDetailInEditMode = true
                        detailEntrySource = DetailEntrySource.NewEntry
                        screen = Screen.EntryDetail
                    }
                },
                onSaveEntry = { updated ->
                    scope.launch { diaryRepository.updateEntry(updated) }
                },
                onDeleteEntry = { id ->
                    scope.launch {
                        diaryRepository.deleteEntry(id)
                        selectedEntryId = 0
                        screen = Screen.Home
                    }
                },
                onCardAlphaChange = { value ->
                    scope.launch { settingsRepository.setCardAlpha(value) }
                },
                onAnimationsEnabledChange = { value ->
                    scope.launch { settingsRepository.setAnimationsEnabled(value) }
                },
                onLanguageChange = { value ->
                    scope.launch { settingsRepository.setLanguage(value) }
                },
                onBackgroundChange = { value ->
                    scope.launch { settingsRepository.setBackgroundKey(value) }
                },
                onCustomBackgroundChange = { value ->
                    scope.launch { settingsRepository.setCustomBackgroundUri(value) }
                },
                onCustomBackgroundOffsetChange = { x, y ->
                    scope.launch { settingsRepository.setCustomBackgroundOffset(x, y) }
                },
                onCustomBackgroundBlurChange = { value ->
                    scope.launch { settingsRepository.setCustomBackgroundBlur(value) }
                },
                onThemeChange = { value ->
                    scope.launch { settingsRepository.setThemeKey(value) }
                },
                onCustomThemeColorChange = { value ->
                    scope.launch { settingsRepository.setCustomThemeColor(value) }
                },
                onOpenSettings = { screen = Screen.Settings },
                onHome = {
                    openDetailInEditMode = false
                    screen = Screen.Home
                },
                startDetailInEditMode = openDetailInEditMode,
                detailEntrySource = detailEntrySource,
                onBackToNewEntry = { screen = Screen.NewEntry }
            )
        }
    }
}

@Composable
private fun InnocenceRoute(
    screen: Screen,
    entries: List<DiaryEntry>,
    selectedEntryId: Int,
    settings: AppSettings,
    draft: EntryDraft,
    onDraftChange: (EntryDraft) -> Unit,
    onSelectEntry: (Int) -> Unit,
    onNewEntry: () -> Unit,
    onCreateEntry: (DiaryEntry) -> Unit,
    onSaveEntry: (DiaryEntry) -> Unit,
    onDeleteEntry: (Int) -> Unit,
    onCardAlphaChange: (Float) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onBackgroundChange: (String) -> Unit,
    onCustomBackgroundChange: (String?) -> Unit,
    onCustomBackgroundOffsetChange: (Float, Float) -> Unit,
    onCustomBackgroundBlurChange: (Float) -> Unit,
    onThemeChange: (String) -> Unit,
    onCustomThemeColorChange: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    onHome: () -> Unit,
    startDetailInEditMode: Boolean,
    detailEntrySource: DetailEntrySource,
    onBackToNewEntry: () -> Unit
) {
    val strings = appStrings(settings.language)

    when (screen) {
        Screen.Home -> HomeScreen(
            entries = entries,
            cardAlpha = settings.cardAlpha,
            animationsEnabled = settings.animationsEnabled,
            backgroundKey = settings.backgroundKey,
            customBackgroundUri = settings.customBackgroundUri,
            customBackgroundOffsetX = settings.customBackgroundOffsetX,
            customBackgroundOffsetY = settings.customBackgroundOffsetY,
            customBackgroundBlur = settings.customBackgroundBlur,
            themeKey = settings.themeKey,
            customThemeColor = settings.customThemeColor,
            strings = strings,
            onOpenEntry = onSelectEntry,
            onNewEntry = onNewEntry,
            onOpenSettings = onOpenSettings,
            onDeleteEntry = onDeleteEntry
        )

        Screen.NewEntry -> NewEntryScreen(
            draft = draft,
            cardAlpha = settings.cardAlpha,
            backgroundKey = settings.backgroundKey,
            customBackgroundUri = settings.customBackgroundUri,
            customBackgroundOffsetX = settings.customBackgroundOffsetX,
            customBackgroundOffsetY = settings.customBackgroundOffsetY,
            customBackgroundBlur = settings.customBackgroundBlur,
            themeKey = settings.themeKey,
            customThemeColor = settings.customThemeColor,
            strings = strings,
            onDraftChange = onDraftChange,
            onBack = onHome,
            onCreate = {
                onCreateEntry(
                    DiaryEntry(
                        id = 0,
                        date = draft.date.ifBlank { "2026-05-13" },
                        weekday = draft.weekday.ifBlank { "星期三" },
                        mood = draft.mood,
                        songName = draft.songName.ifBlank { "未命名歌曲" },
                        artist = draft.artist.ifBlank { "未知歌手" },
                        lyricSnippet = draft.lyricSnippet.ifBlank { "写下一句你想记住的歌词。" },
                        reflection = "",
                        photo = MeadowPhoto,
                        coverUri = draft.coverUri
                    )
                )
            }
        )

        Screen.EntryDetail -> {
            val selectedEntry = entries.firstOrNull { it.id == selectedEntryId }
            if (selectedEntry == null) {
                HomeScreen(
                    entries = entries,
                    cardAlpha = settings.cardAlpha,
                    animationsEnabled = settings.animationsEnabled,
                    backgroundKey = settings.backgroundKey,
                    customBackgroundUri = settings.customBackgroundUri,
                    customBackgroundOffsetX = settings.customBackgroundOffsetX,
                    customBackgroundOffsetY = settings.customBackgroundOffsetY,
                    customBackgroundBlur = settings.customBackgroundBlur,
                    themeKey = settings.themeKey,
                    customThemeColor = settings.customThemeColor,
                    strings = strings,
                    onOpenEntry = onSelectEntry,
                    onNewEntry = onNewEntry,
                    onOpenSettings = onOpenSettings,
                    onDeleteEntry = onDeleteEntry
                )
            } else {
                EntryDetailScreen(
                    entry = selectedEntry,
                    cardAlpha = settings.cardAlpha,
                    customBackgroundUri = settings.customBackgroundUri,
                    customBackgroundOffsetX = settings.customBackgroundOffsetX,
                    customBackgroundOffsetY = settings.customBackgroundOffsetY,
                    customBackgroundBlur = settings.customBackgroundBlur,
                    themeKey = settings.themeKey,
                    customThemeColor = settings.customThemeColor,
                    startInEditMode = startDetailInEditMode,
                    entrySource = detailEntrySource,
                    strings = strings,
                    onBack = onHome,
                    onBackToNewEntry = onBackToNewEntry,
                    onSave = onSaveEntry,
                    onDelete = onDeleteEntry
                )
            }
        }

        Screen.Settings -> SettingsScreen(
            cardAlpha = settings.cardAlpha,
            animationsEnabled = settings.animationsEnabled,
            language = settings.language,
            backgroundKey = settings.backgroundKey,
            customBackgroundUri = settings.customBackgroundUri,
            customBackgroundOffsetX = settings.customBackgroundOffsetX,
            customBackgroundOffsetY = settings.customBackgroundOffsetY,
            customBackgroundBlur = settings.customBackgroundBlur,
            themeKey = settings.themeKey,
            customThemeColor = settings.customThemeColor,
            strings = strings,
            onCardAlphaChange = onCardAlphaChange,
            onAnimationsEnabledChange = onAnimationsEnabledChange,
            onLanguageChange = onLanguageChange,
            onBackgroundChange = onBackgroundChange,
            onCustomBackgroundChange = onCustomBackgroundChange,
            onCustomBackgroundOffsetChange = onCustomBackgroundOffsetChange,
            onCustomBackgroundBlurChange = onCustomBackgroundBlurChange,
            onThemeChange = onThemeChange,
            onCustomThemeColorChange = onCustomThemeColorChange,
            onHome = onHome
        )
    }
}

private fun defaultEntries(): List<DiaryEntry> = listOf(
    DiaryEntry(
        id = 0,
        date = "2026-05-13",
        weekday = "星期三",
        mood = "平静",
        songName = "Breathe Again",
        artist = "Sara Bareilles",
        lyricSnippet = "This time I'll be stronger, I'll breathe again and face this life...",
        reflection = "有些歌会刚好落在你最需要它的那一天。今天听到这首歌时，整个人像慢慢松开了一点，呼吸也重新顺了起来。",
        photo = MeadowPhoto
    ),
    DiaryEntry(
        id = 0,
        date = "2026-05-11",
        weekday = "星期一",
        mood = "期待",
        songName = "The Night We Met",
        artist = "Lord Huron",
        lyricSnippet = "I had all and then most of you, some and now none of you.",
        reflection = "夜里回家的路有点空，但这首歌让灯光也变得柔和了。",
        photo = HarborPhoto
    ),
    DiaryEntry(
        id = 0,
        date = "2026-05-08",
        weekday = "星期五",
        mood = "难过",
        songName = "Say Something",
        artist = "A Great Big World",
        lyricSnippet = "Say something, I'm giving up on you.",
        reflection = "钢琴一响起来，情绪就被轻轻按住了，像是终于允许自己安静下来。",
        photo = NightPhoto
    ),
    DiaryEntry(
        id = 0,
        date = "2026-05-05",
        weekday = "星期二",
        mood = "开心",
        songName = "Bloom",
        artist = "The Paper Kites",
        lyricSnippet = "Can I be close to you?",
        reflection = "适合在有太阳的下午循环，空气都会跟着变轻一点。",
        photo = FieldPhoto
    )
)

@Preview(showBackground = true)
@Composable
private fun InnocencePreview() {
    InnocenceTheme {
        InnocenceApp()
    }
}
