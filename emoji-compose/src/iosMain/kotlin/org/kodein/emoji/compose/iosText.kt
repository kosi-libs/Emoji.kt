package org.kodein.emoji.compose

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import org.kodein.emoji.Emoji


internal actual fun platformSizeRatio(emoji: Emoji, textMeasurer: TextMeasurer, density: Density): Size =
    fontSizeRatio(emoji, textMeasurer, density)
