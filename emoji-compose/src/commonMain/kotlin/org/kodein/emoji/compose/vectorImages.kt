package org.kodein.emoji.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


internal expect class SVGImage {
    fun sizeRatio(): Float
    companion object {
        suspend fun create(bytes: ByteArray): SVGImage
    }
}

@Composable
internal expect fun SVGImage(image: SVGImage, contentDescription: String, modifier: Modifier)

internal expect class LottieAnimation {
    fun sizeRatio(): Float
    companion object {
        suspend fun create(bytes: ByteArray): LottieAnimation
    }
}

@Composable
internal expect fun LottieAnimation(
    animation: LottieAnimation,
    iterations: Int,
    stopAt: Float,
    speed: Float,
    contentDescription: String,
    modifier: Modifier
)
