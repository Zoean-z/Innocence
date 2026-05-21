package com.example.myapplication.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.myapplication.model.AppStrings
import com.example.myapplication.model.FieldPhoto
import com.example.myapplication.model.HarborPhoto
import com.example.myapplication.model.MeadowPhoto
import com.example.myapplication.model.PhotoStyle
import com.example.myapplication.model.SkyPhoto
import com.example.myapplication.model.NightPhoto
import com.example.myapplication.model.photoKeyOf
import com.example.myapplication.model.photoStyleForKey
import com.example.myapplication.ui.component.BottomNav
import com.example.myapplication.ui.component.PhoneCanvas
import com.example.myapplication.ui.component.accentColor
import com.example.myapplication.ui.component.accentSoftColor
import com.example.myapplication.ui.component.cardBorderColor
import com.example.myapplication.ui.component.cardLayerColor
import com.example.myapplication.ui.component.cardSurfaceColor
import com.example.myapplication.ui.component.safeThemeAccentColor
import com.example.myapplication.ui.theme.InnocenceCardBorder
import com.example.myapplication.ui.theme.InnocenceHighlight
import com.example.myapplication.ui.theme.InnocenceInk
import com.example.myapplication.ui.theme.InnocenceSubtleText
import java.io.File

private val SettingsBlockShape = RoundedCornerShape(22.dp)

@Composable
fun SettingsScreen(
    cardAlpha: Float,
    animationsEnabled: Boolean,
    language: String,
    backgroundKey: String,
    customBackgroundUri: String?,
    customBackgroundOffsetX: Float,
    customBackgroundOffsetY: Float,
    customBackgroundBlur: Float,
    themeKey: String,
    customThemeColor: Int,
    strings: AppStrings,
    onCardAlphaChange: (Float) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onBackgroundChange: (String) -> Unit,
    onCustomBackgroundChange: (String?) -> Unit,
    onCustomBackgroundOffsetChange: (Float, Float) -> Unit,
    onCustomBackgroundBlurChange: (Float) -> Unit,
    onThemeChange: (String) -> Unit,
    onCustomThemeColorChange: (Int) -> Unit,
    onHome: () -> Unit
) {
    BackHandler {
        onHome()
    }

    val context = LocalContext.current
    val effectiveAlpha = cardAlpha.coerceIn(0.72f, 0.96f)
    val previewAlpha by animateFloatAsState(targetValue = effectiveAlpha, label = "previewAlpha")
    val backgroundCropper = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { croppedUri ->
                persistCroppedBackground(context, croppedUri)?.let(onCustomBackgroundChange)
            }
        }
    }
    val backgroundPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            backgroundCropper.launch(
                CropImageContractOptions(
                    uri = it,
                    cropImageOptions = CropImageOptions(
                        guidelines = CropImageView.Guidelines.ON,
                        fixAspectRatio = false,
                        cropMenuCropButtonTitle = "确认"
                    )
                )
            )
        }
    }
    val launchBackgroundCropper = {
        backgroundPicker.launch(arrayOf("image/*"))
    }
    val launchBackgroundRecrop = {
        backgroundCropper.launch(
            CropImageContractOptions(
                uri = customBackgroundUri?.let(Uri::parse),
                cropImageOptions = CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON,
                    fixAspectRatio = false,
                    cropMenuCropButtonTitle = "确认"
                )
            )
        )
    }

    PhoneCanvas(
        backgroundStyle = photoStyleForKey(backgroundKey),
        customBackgroundUri = customBackgroundUri,
        customBackgroundOffsetX = customBackgroundOffsetX,
        customBackgroundOffsetY = customBackgroundOffsetY,
        customBackgroundBlur = customBackgroundBlur,
        cardAlpha = effectiveAlpha,
        themeKey = themeKey,
        customThemeColor = customThemeColor
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNav(
                    selected = "Setting",
                    onHome = onHome,
                    onSettings = {}
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    HeaderSection()
                }

                item {
                    SettingsLabel(
                        title = "预览效果",
                        subtitle = "看看现在这套背景、强调色和卡片透明度的感觉。"
                    )
                    Spacer(Modifier.height(10.dp))
                    PreviewPanel(alpha = previewAlpha, themeKey = themeKey)
                }

                item {
                    SettingsLabel(
                        title = "背景图片",
                        subtitle = "选择相册图片后，可以调整裁切位置和背景模糊度。"
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                    AlbumThumb(
                        backgroundUri = customBackgroundUri,
                        selected = customBackgroundUri != null,
                        onClick = launchBackgroundCropper
                    )
                        listOf(MeadowPhoto, SkyPhoto, FieldPhoto, HarborPhoto, NightPhoto).forEach { style ->
                            val key = photoKeyOf(style)
                            BackgroundThumb(
                                style = style,
                                selected = customBackgroundUri == null && backgroundKey == key,
                                onClick = {
                                    onCustomBackgroundChange(null)
                                    onBackgroundChange(key)
                                }
                            )
                        }
                    }
                }

                if (customBackgroundUri != null) {
                    item {
                        BackgroundImageControls(
                            blur = customBackgroundBlur,
                            onBlurChange = onCustomBackgroundBlurChange,
                            onRecrop = launchBackgroundRecrop
                        )
                    }
                }

                item {
                    SettingsLabel(
                        title = "更改界面强调色",
                        subtitle = "调整按钮、高亮和重点信息使用的强调色。"
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        ThemeDot(
                            label = "绿色",
                            color = Color(0xFF5F8D63),
                            selected = themeKey == "fresh",
                            onClick = { onThemeChange("fresh") }
                        )
                        ThemeDot(
                            label = "米色",
                            color = Color(0xFF8E8A70),
                            selected = themeKey == "paper",
                            onClick = { onThemeChange("paper") }
                        )
                        ThemeDot(
                            label = "杏色",
                            color = Color(0xFFC49367),
                            selected = themeKey == "warm",
                            onClick = { onThemeChange("warm") }
                        )
                        ThemeDot(
                            label = "灰绿",
                            color = Color(0xFF7A8F80),
                            selected = themeKey == "night",
                            onClick = { onThemeChange("night") }
                        )
                        ExtractedThemeDot(
                            label = "提取",
                            color = safeThemeAccentColor(Color(customThemeColor)),
                            enabled = customBackgroundUri != null,
                            selected = themeKey == "custom",
                            onClick = {
                                customBackgroundUri?.let { uri ->
                                    extractThemeColor(context, uri)?.let { color ->
                                        onCustomThemeColorChange(color)
                                        onThemeChange("custom")
                                    }
                                }
                            }
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
                            text = "卡片透明度",
                            style = MaterialTheme.typography.titleMedium,
                            color = InnocenceInk,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(effectiveAlpha * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = accentColor(),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Slider(
                        value = effectiveAlpha,
                        onValueChange = onCardAlphaChange,
                        valueRange = 0.72f..0.96f,
                        colors = SliderDefaults.colors(
                            thumbColor = accentColor(),
                            activeTrackColor = accentColor(),
                            inactiveTrackColor = accentSoftColor()
                        )
                    )
                }

                item {
                    SettingsLabel(title = "动效")
                    Spacer(Modifier.height(10.dp))
                    CompactSettingRow(
                        title = "开启细微动画",
                        detail = "保留轻微的进入和切换动画",
                        trailing = {
                            Switch(
                                checked = animationsEnabled,
                                onCheckedChange = onAnimationsEnabledChange
                            )
                        }
                    )
                }

                item {
                    AboutPanel()
                }
            }
        }
    }
}

@Composable
private fun BackgroundImageControls(
    blur: Float,
    onBlurChange: (Float) -> Unit,
    onRecrop: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsLabel(
            title = "背景图片调整",
            subtitle = "重新裁切图片，或调节裁切后背景的模糊度。"
        )
        Card(
            shape = SettingsBlockShape,
            colors = CardDefaults.cardColors(cardSurfaceColor()),
            border = BorderStroke(1.dp, cardBorderColor()),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SliderRow(
                    title = "背景模糊度",
                    valueText = String.format("%.1f", blur.coerceIn(0f, 28f)),
                    value = blur.coerceIn(0f, 28f),
                    range = 0f..28f,
                    onValueChange = onBlurChange
                )
                Text(
                    text = "重新选择并裁切图片",
                    color = accentColor(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onRecrop)
                )
            }
        }
    }
}

private fun persistCroppedBackground(context: Context, sourceUri: Uri): String? {
    return runCatching {
        val directory = File(context.filesDir, "backgrounds").apply { mkdirs() }
        val target = File(directory, "custom_background.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return@runCatching null
        Uri.fromFile(target).toString()
    }.getOrNull()
}

private fun extractThemeColor(context: Context, uriString: String): Int? {
    return runCatching {
        val bitmap = context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: return@runCatching null
        sampleAverageColor(bitmap).also { bitmap.recycle() }
    }.getOrNull()
}

private fun sampleAverageColor(bitmap: Bitmap): Int {
    val stepX = (bitmap.width / 24).coerceAtLeast(1)
    val stepY = (bitmap.height / 24).coerceAtLeast(1)
    var red = 0L
    var green = 0L
    var blue = 0L
    var count = 0L

    var y = stepY / 2
    while (y < bitmap.height) {
        var x = stepX / 2
        while (x < bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            red += AndroidColor.red(pixel)
            green += AndroidColor.green(pixel)
            blue += AndroidColor.blue(pixel)
            count++
            x += stepX
        }
        y += stepY
    }

    if (count == 0L) return 0xFF5F8D63.toInt()
    return AndroidColor.rgb(
        ((red / count) * 0.75f).toInt().coerceIn(0, 255),
        ((green / count) * 0.75f).toInt().coerceIn(0, 255),
        ((blue / count) * 0.75f).toInt().coerceIn(0, 255)
    )
}

@Composable
private fun SliderRow(
    title: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = InnocenceInk,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelLarge,
                color = accentColor(),
                fontWeight = FontWeight.SemiBold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accentColor(),
                activeTrackColor = accentColor(),
                inactiveTrackColor = accentSoftColor()
            )
        )
    }
}

@Composable
private fun HeaderSection() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "个性化",
            style = MaterialTheme.typography.headlineMedium,
            color = accentColor(),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "把页面调成更接近你现在的心情。",
            color = InnocenceSubtleText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SettingsLabel(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = InnocenceInk,
            fontWeight = FontWeight.SemiBold
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = InnocenceSubtleText
            )
        }
    }
}

@Composable
private fun PreviewPanel(alpha: Float, themeKey: String) {
    val accent = when (themeKey) {
        "paper" -> Color(0xFFEFE7D3)
        "warm" -> Color(0xFFF5E2C8)
        "night" -> Color(0xFFD9E4DA)
        else -> accentSoftColor()
    }

    Card(
        shape = SettingsBlockShape,
        colors = CardDefaults.cardColors(containerColor = cardSurfaceColor(Color.White)),
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardLayerColor(accent, strength = 0.28f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "晚风会记得这首歌",
                style = MaterialTheme.typography.titleMedium,
                color = InnocenceInk,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "陈粒",
                style = MaterialTheme.typography.bodyMedium,
                color = InnocenceSubtleText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodPreviewChip()
                Text(
                    text = "今天听到副歌的时候，突然觉得忙碌也能慢下来一点。",
                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = InnocenceInk,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MoodPreviewChip() {
    Text(
        text = "平静",
        color = accentColor(),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(accentSoftColor())
            .border(1.dp, cardBorderColor(), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun BackgroundThumb(
    style: PhotoStyle,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 106.dp, height = 80.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(style.glow, style.base, style.shadow)
                )
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accentColor() else cardBorderColor(strength = 0.8f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        if (selected) {
            CheckBadge(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
        }
    }
}

@Composable
private fun AlbumThumb(
    backgroundUri: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(backgroundUri) {
        backgroundUri?.let { uriString ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    Box(
        modifier = Modifier
            .size(width = 106.dp, height = 80.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(cardSurfaceColor())
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accentColor() else cardBorderColor(strength = 0.8f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "背景图片",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardLayerColor(Color.White, strength = 0.30f))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardLayerColor(strength = 0.88f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = accentColor())
                    Text("从相册选择", color = accentColor(), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        if (selected) {
            CheckBadge(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
        }
    }
}

@Composable
private fun ThemeDot(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) accentColor() else cardBorderColor(),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) accentColor() else InnocenceSubtleText
        )
    }
}

@Composable
private fun ExtractedThemeDot(
    label: String,
    color: Color,
    enabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    val ringColors = listOf(
        Color(0xFF8BBF8F),
        Color(0xFFF0C477),
        Color(0xFF8DB7C8),
        Color(0xFFD99B9B),
        Color(0xFFA99AC7),
        Color(0xFF8BBF8F)
    )
    Column(
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Brush.sweepGradient(ringColors))
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) accentColor() else cardBorderColor(),
                    shape = CircleShape
                )
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (enabled) color else InnocenceHighlight)
                    .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = when {
                selected -> accentColor()
                enabled -> InnocenceSubtleText
                else -> InnocenceSubtleText.copy(alpha = 0.55f)
            }
        )
    }
}

@Composable
private fun CompactSettingRow(
    title: String,
    detail: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SettingsBlockShape)
            .background(cardSurfaceColor())
            .border(1.dp, cardBorderColor(), SettingsBlockShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = InnocenceInk,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = InnocenceSubtleText
            )
        }
        trailing()
    }
}

@Composable
private fun AboutPanel() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "关于 innocence",
            style = MaterialTheme.typography.titleSmall,
            color = InnocenceSubtleText,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "innocence 是一个轻量的本地日记本，把歌曲、心情和当下的感受记在一起。",
            style = MaterialTheme.typography.bodyMedium,
            color = InnocenceSubtleText
        )
        Text(
            text = "版本 1.0.0",
            style = MaterialTheme.typography.labelLarge,
            color = InnocenceSubtleText
        )
    }
}

@Composable
private fun CheckBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = accentColor(),
            modifier = Modifier.size(14.dp)
        )
    }
}
