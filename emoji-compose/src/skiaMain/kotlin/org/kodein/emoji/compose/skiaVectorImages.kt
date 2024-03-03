package org.kodein.emoji.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.*
import org.jetbrains.skia.Data
import org.jetbrains.skia.Rect
import org.jetbrains.skia.skottie.Animation
import org.jetbrains.skia.sksg.InvalidationController
import org.jetbrains.skia.svg.SVGDOM
import org.jetbrains.skia.svg.SVGLengthContext
import kotlin.math.roundToInt


internal actual class SVGImage(val dom: SVGDOM) {
    actual fun sizeRatio(): Float {
        return dom.root?.viewBox?.let { it.width / it.height } ?: 1f
    }
    actual companion object {
        actual suspend fun create(bytes: ByteArray): SVGImage =
            withContext(Dispatchers.Default) {
                SVGImage(SVGDOM(data = Data.makeFromBytes(bytes)))
            }
    }
}

@Composable
internal actual fun SVGImage(image: SVGImage, contentDescription: String, modifier: Modifier) {
    Canvas(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Image
            }
    ) {
        image.dom.setContainerSize(size.width, size.height)
        drawIntoCanvas { canvas ->
            image.dom.render(canvas.nativeCanvas)
        }
    }
}

internal actual class LottieAnimation(val animation: Animation) {
    actual fun sizeRatio(): Float = animation.width / animation.height

    actual companion object {
        actual suspend fun create(bytes: ByteArray): LottieAnimation =
            withContext(Dispatchers.Default) {
                val result = Animation.makeFromString(bytes.decodeToString())
                LottieAnimation(result)
            }
    }
}

@Composable
internal actual fun LottieAnimation(animation: LottieAnimation, contentDescription: String, modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = animation.animation.duration,
        animationSpec = infiniteRepeatable(
            animation = tween((animation.animation.duration * 1_000).roundToInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val invalidationController = remember { InvalidationController() }
    animation.animation.seekFrameTime(time, invalidationController)
    Canvas(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Image
            }
    ) {
        drawIntoCanvas {
            animation.animation.render(
                canvas = it.nativeCanvas,
                dst = Rect.makeWH(size.width, size.height)
            )
        }
    }
}
