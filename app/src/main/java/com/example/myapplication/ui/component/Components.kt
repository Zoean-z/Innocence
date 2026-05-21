package com.example.myapplication.ui.component

import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.DiaryEntry
import com.example.myapplication.model.PhotoStyle
import com.example.myapplication.ui.theme.InnocenceAccent
import com.example.myapplication.ui.theme.InnocenceBackgroundBottom
import com.example.myapplication.ui.theme.InnocenceBackgroundTop
import com.example.myapplication.ui.theme.InnocenceCard
import com.example.myapplication.ui.theme.InnocenceCardBorder
import com.example.myapplication.ui.theme.InnocenceHighlight
import com.example.myapplication.ui.theme.InnocenceInk
import com.example.myapplication.ui.theme.InnocencePrimary
import com.example.myapplication.ui.theme.InnocencePrimarySoft
import com.example.myapplication.ui.theme.InnocenceSubtleText
import com.example.myapplication.ui.theme.InnocenceWarm

private val PageShape = RoundedCornerShape(24.dp)
private val SectionShape = RoundedCornerShape(20.dp)
val LocalCardAlpha = staticCompositionLocalOf { 0.92f }
val LocalAccentColor = staticCompositionLocalOf { InnocencePrimary }
val LocalAccentSoftColor = staticCompositionLocalOf { InnocencePrimarySoft }

@Composable
fun surfaceAlpha(): Float = LocalCardAlpha.current.coerceIn(0.72f, 0.96f)

@Composable
fun cardSurfaceColor(base: Color = InnocenceCard): Color = base.copy(alpha = surfaceAlpha())

@Composable
fun cardLayerColor(base: Color = Color.Unspecified, strength: Float = 0.82f): Color {
    val source = if (base == Color.Unspecified) accentSoftColor() else base
    return source.copy(alpha = (surfaceAlpha() * strength).coerceIn(0f, 1f))
}

@Composable
fun cardBorderColor(base: Color = Color.Unspecified, strength: Float = 0.9f): Color {
    val source = if (base == Color.Unspecified) accentSoftColor() else base
    return source.copy(alpha = (surfaceAlpha() * strength).coerceIn(0f, 1f))
}

@Composable
fun accentColor(): Color = LocalAccentColor.current

@Composable
fun accentSoftColor(): Color = LocalAccentSoftColor.current

fun safeThemeAccentColor(rawColor: Color): Color {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(rawColor.toArgb(), hsv)
    hsv[1] = hsv[1].coerceIn(0.34f, 0.72f)
    hsv[2] = hsv[2].coerceIn(0.28f, 0.58f)

    var safeColor = Color(AndroidColor.HSVToColor(hsv))
    var attempts = 0
    while (contrastRatio(safeColor, Color.White) < 4.5f && attempts < 12) {
        hsv[2] = (hsv[2] * 0.88f).coerceAtLeast(0.18f)
        safeColor = Color(AndroidColor.HSVToColor(hsv))
        attempts++
    }
    return safeColor
}

private fun contrastRatio(foreground: Color, background: Color): Float {
    val lighter = maxOf(foreground.luminance(), background.luminance())
    val darker = minOf(foreground.luminance(), background.luminance())
    return ((lighter + 0.05f) / (darker + 0.05f))
}

private fun fineBlurAmount(value: Float): Float {
    val normalized = (value.coerceIn(0f, 28f) / 28f)
    return normalized * normalized * 28f
}

@Composable
fun PhoneCanvas(
    backgroundStyle: PhotoStyle? = null,
    customBackgroundUri: String? = null,
    customBackgroundOffsetX: Float = 0f,
    customBackgroundOffsetY: Float = 0f,
    customBackgroundBlur: Float = 0f,
    cardAlpha: Float = 0.92f,
    themeKey: String = "fresh",
    customThemeColor: Int = 0xFF5F8D63.toInt(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val extractedThemeColor = safeThemeAccentColor(Color(customThemeColor))
    val emphasisColor = when (themeKey) {
        "paper" -> Color(0xFF8E8A70)
        "warm" -> Color(0xFFC49367)
        "night" -> Color(0xFF7A8F80)
        "custom" -> extractedThemeColor
        else -> InnocencePrimary
    }
    val emphasisSoftColor = when (themeKey) {
        "paper" -> Color(0xFFF1EADB)
        "warm" -> Color(0xFFF7E5D1)
        "night" -> Color(0xFFDCE6DD)
        "custom" -> lerp(Color.White, extractedThemeColor, 0.20f)
        else -> InnocencePrimarySoft
    }
    val accent = when (themeKey) {
        "paper" -> Color(0xFFEFE9D8)
        "warm" -> Color(0xFFF4E4C8)
        "night" -> Color(0xFFDDE8DD)
        "custom" -> lerp(Color.White, extractedThemeColor, 0.18f)
        else -> emphasisSoftColor
    }
    val presetGlow = backgroundStyle?.glow ?: InnocenceAccent
    val presetBase = backgroundStyle?.base ?: emphasisSoftColor
    val presetShadow = backgroundStyle?.shadow ?: InnocenceWarm
    val customBitmap = remember(customBackgroundUri) {
        customBackgroundUri?.let { uriString ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (customBitmap != null) {
            Image(
                bitmap = customBitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(fineBlurAmount(customBackgroundBlur).dp),
                contentScale = ContentScale.Crop,
                alignment = BiasAlignment(
                    horizontalBias = customBackgroundOffsetX.coerceIn(-1f, 1f),
                    verticalBias = customBackgroundOffsetY.coerceIn(-1f, 1f)
                )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                InnocenceBackgroundTop,
                                presetGlow.copy(alpha = 0.28f),
                                presetBase.copy(alpha = 0.22f),
                                accent.copy(alpha = 0.32f),
                                InnocenceBackgroundBottom,
                                presetShadow.copy(alpha = 0.18f)
                            ),
                            start = Offset.Zero,
                            end = Offset(1200f, 1800f)
                        )
                    )
            )
        }
        if (customBitmap == null) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 8.dp)
                    .alpha(0.12f)
                    .background(
                        color = (backgroundStyle?.glow ?: InnocenceAccent).copy(alpha = 0.45f),
                        shape = CircleShape
                    )
                )
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 80.dp)
                    .alpha(0.08f)
                    .background(color = InnocenceWarm, shape = CircleShape)
            )
        }
        CompositionLocalProvider(
            LocalCardAlpha provides cardAlpha.coerceIn(0.72f, 0.96f),
            LocalAccentColor provides emphasisColor,
            LocalAccentSoftColor provides emphasisSoftColor
        ) {
            content()
        }
    }
}

@Composable
fun HeroDiaryCard(entry: DiaryEntry, cardAlpha: Float, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = PageShape,
        colors = CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodPill(text = "今日记录", light = false)
                Text(
                    text = entry.date,
                    color = InnocenceSubtleText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = entry.songName,
                    style = MaterialTheme.typography.titleLarge,
                    color = InnocenceInk,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = entry.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InnocenceSubtleText
                )
            }
            Text(
                text = entry.reflection.ifBlank { entry.lyricSnippet },
                color = InnocenceInk,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodPill(text = entry.mood, light = false)
                Text(
                    text = entry.weekday,
                    color = accentColor(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun PastDiaryRow(entry: DiaryEntry, onClick: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clickable(onClick = onClick),
        shape = SectionShape,
        colors = CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                PhotoBackdrop(style = entry.photo, decorative = false)
            }
            Spacer(Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entry.songName,
                    color = InnocenceInk,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.artist,
                    color = InnocenceSubtleText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.reflection.ifBlank { entry.lyricSnippet },
                    color = InnocenceSubtleText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MoodPill(text = entry.mood, light = false)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.date,
                        color = InnocenceSubtleText,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                Icons.Default.MoreHoriz,
                                contentDescription = "更多操作",
                                tint = InnocenceSubtleText
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("删除") },
                                onClick = {
                                    expanded = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoBackdrop(style: PhotoStyle, decorative: Boolean = true) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        style.glow.copy(alpha = 0.92f),
                        style.base.copy(alpha = 0.88f),
                        style.shadow.copy(alpha = 0.72f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(900f, 1200f)
                )
            )
    ) {
        if (decorative) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.TopEnd)
                    .alpha(0.12f)
                    .background(Color.White, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .align(Alignment.BottomStart)
                    .alpha(0.08f)
                    .background(style.glow, CircleShape)
            )
        }
    }
}

@Composable
fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    subtitle: String? = null,
    actionText: String? = null,
    actionIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            GlassIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = InnocenceInk, style = MaterialTheme.typography.headlineSmall)
                subtitle?.let {
                    Text(it, color = InnocenceSubtleText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        if (actionIcon != null && onAction != null) {
            if (actionText == null) {
                GlassIconButton(icon = actionIcon, onClick = onAction)
            } else {
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor(),
                        contentColor = Color.White
                    )
                ) {
                    Icon(actionIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(actionText)
                }
            }
        }
    }
}

@Composable
fun DateHeader(entry: DiaryEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.date,
            fontWeight = FontWeight.Medium,
            color = InnocenceInk,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = entry.weekday,
            color = InnocenceSubtleText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BottomNav(
    selected: String,
    onHome: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(cardSurfaceColor())
            .border(1.dp, cardBorderColor(), RoundedCornerShape(24.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavItem(
            label = "首页",
            icon = Icons.Default.Home,
            selected = selected == "Home",
            modifier = Modifier.weight(1f),
            onClick = onHome
        )
        NavItem(
            label = "个性化",
            icon = Icons.Default.Settings,
            selected = selected == "Setting",
            modifier = Modifier.weight(1f),
            onClick = onSettings
        )
    }
}

@Composable
fun FieldBlock(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = InnocenceInk
        )
        content()
    }
}

@Composable
fun SoftInputRow(
    icon: ImageVector,
    value: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = { Icon(icon, contentDescription = null, tint = accentColor()) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = outlinedFieldColors()
    )
}

@Composable
fun MoodGrid(selectedMood: String, onSelect: (String) -> Unit) {
    val moods = listOf(
        "平静",
        "开心",
        "难过",
        "期待",
        "烦闷",
        "心动",
        "疲惫",
        "复杂"
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        moods.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { mood ->
                    MoodChoice(
                        text = mood,
                        selected = normalizeMoodKey(selectedMood) == normalizeMoodKey(mood),
                        onClick = { onSelect(mood) }
                    )
                }
            }
        }
    }
}

@Composable
fun MoodPill(text: String, light: Boolean) {
    val background = if (light) Color.White.copy(alpha = 0.2f) else accentSoftColor()
    val border = if (light) Color.White.copy(alpha = 0.4f) else cardBorderColor()
    Text(
        text = displayMood(text),
        color = if (light) Color.White else accentColor(),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun GlassPanel(alpha: Float, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = PageShape,
        colors = CardDefaults.cardColors(cardSurfaceColor()),
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun GlassIconButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(cardSurfaceColor())
            .border(1.dp, cardBorderColor(), RoundedCornerShape(14.dp))
    ) {
        Icon(icon, contentDescription = null, tint = accentColor())
    }
}

@Composable
fun ReadEntry(entry: DiaryEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SongCoverImage(
            coverUri = entry.coverUri,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(132.dp)
        )
        PaperSection(title = "歌词片段") {
            Text(
                text = entry.lyricSnippet.ifBlank { "这首歌还没有写下歌词片段。" },
                style = MaterialTheme.typography.bodyLarge,
                color = InnocenceInk
            )
        }
        PaperSection(title = "我的感想") {
            Text(
                text = entry.reflection.ifBlank { "这一刻还没有留下感想。" },
                style = MaterialTheme.typography.bodyLarge,
                color = InnocenceInk
            )
        }
    }
}

@Composable
fun EditEntry(entry: DiaryEntry, placeholder: String, onEntryChange: (DiaryEntry) -> Unit) {
    TextField(
        value = entry.reflection,
        onValueChange = { onEntryChange(entry.copy(reflection = it)) },
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .defaultMinSize(minHeight = 380.dp),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = cardLayerColor(strength = 0.86f),
            unfocusedContainerColor = cardLayerColor(strength = 0.86f),
            disabledContainerColor = cardLayerColor(strength = 0.86f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = accentColor(),
            focusedTextColor = InnocenceInk,
            unfocusedTextColor = InnocenceInk,
            focusedPlaceholderColor = InnocenceSubtleText,
            unfocusedPlaceholderColor = InnocenceSubtleText
        )
    )
}

@Composable
fun SongCoverPicker(coverUri: String?, onPickCover: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PageShape)
            .background(cardSurfaceColor())
            .border(1.dp, cardBorderColor(), PageShape)
            .clickable(onClick = onPickCover)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SongCoverImage(
            coverUri = coverUri,
            modifier = Modifier.size(78.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (coverUri == null) "选择封面图片" else "已选择封面图片",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = InnocenceInk
            )
            Text(
                text = if (coverUri == null) "从设备中挑一张图，让这首歌更像日记。" else "点击可以更换当前封面。",
                color = InnocenceSubtleText
            )
        }
        Icon(Icons.Default.Image, contentDescription = null, tint = accentColor())
    }
}

@Composable
fun SongCoverImage(coverUri: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap = remember(coverUri) {
        coverUri?.let { uriString ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cardLayerColor(strength = 0.78f))
            .border(1.dp, cardBorderColor(), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "歌曲封面",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, contentDescription = null, tint = accentColor())
                Text("封面", color = accentColor(), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun BackgroundTile(style: PhotoStyle, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 112.dp, height = 84.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accentColor() else cardBorderColor(strength = 0.8f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        PhotoBackdrop(style = style)
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = accentColor(),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun AddTile() {
    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 84.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(cardSurfaceColor())
            .border(1.dp, cardBorderColor(), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = accentColor())
    }
}

@Composable
fun ThemeTile(
    selected: Boolean,
    base: Color,
    accent: Color,
    dark: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(base)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) accentColor() else cardBorderColor(),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (dark) Color.White else accent)
            )
        }
        Text(
            text = if (selected) "当前" else "可选",
            color = if (selected) accentColor() else InnocenceSubtleText,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun SettingSwitch(
    title: String,
    detail: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = InnocenceInk)
            Text(detail, color = InnocenceSubtleText)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun AboutCard() {
    GlassPanel(alpha = LocalCardAlpha.current, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("关于 innocence", style = MaterialTheme.typography.titleMedium, color = InnocenceInk)
            Text(
                "innocence 是一个轻量的本地日记本，把歌曲、心情和当下的感受记在一起。",
                color = InnocenceSubtleText
            )
            Text("版本 1.0.0", color = InnocenceSubtleText, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) accentSoftColor() else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (selected) accentColor() else InnocenceSubtleText,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            color = if (selected) accentColor() else InnocenceSubtleText,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun MoodChoice(text: String, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(targetValue = if (selected) 1.04f else 1f, label = "moodScale")
    Row(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) accentColor() else Color.White.copy(alpha = 0.76f))
            .border(
                1.dp,
                if (selected) accentColor() else cardBorderColor(strength = 0.9f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = if (selected) Color.White else InnocenceInk)
    }
}

@Composable
private fun PaperSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier,
        shape = SectionShape,
        colors = CardDefaults.cardColors(cardSurfaceColor(Color.White)),
        border = BorderStroke(1.dp, cardBorderColor(strength = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
fun outlinedFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedContainerColor = cardSurfaceColor(),
    unfocusedContainerColor = cardSurfaceColor(),
    disabledContainerColor = cardSurfaceColor(),
    focusedBorderColor = accentColor(),
    unfocusedBorderColor = cardBorderColor(),
    cursorColor = accentColor(),
    focusedTextColor = InnocenceInk,
    unfocusedTextColor = InnocenceInk,
    focusedPlaceholderColor = InnocenceSubtleText,
    unfocusedPlaceholderColor = InnocenceSubtleText
)

private fun normalizeMoodKey(value: String): String {
    return when (value.trim().lowercase()) {
        "calm", "平静" -> "calm"
        "happy", "开心" -> "happy"
        "sad", "难过", "melancholy" -> "sad"
        "hopeful", "期待" -> "hopeful"
        "angry", "烦闷" -> "angry"
        "loved", "心动" -> "loved"
        "tired", "疲惫" -> "tired"
        else -> "complex"
    }
}

private fun displayMood(value: String): String {
    return when (normalizeMoodKey(value)) {
        "calm" -> "平静"
        "happy" -> "开心"
        "sad" -> "难过"
        "hopeful" -> "期待"
        "angry" -> "烦闷"
        "loved" -> "心动"
        "tired" -> "疲惫"
        else -> "复杂"
    }
}
