package org.kodein.emoji.compose

import android.graphics.RectF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import kotlin.math.roundToInt
import com.airbnb.lottie.compose.LottieAnimation as ALottieAnimation
import com.caverock.androidsvg.SVG as ASVG


internal actual class SVGImage(val svg: ASVG) {
    actual fun sizeRatio(): Float = svg.documentAspectRatio
    actual companion object {
        actual suspend fun create(bytes: ByteArray): SVGImage =
            withContext(Dispatchers.Default) {
                SVGImage(ASVG.getFromInputStream(ByteArrayInputStream(bytes)))
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
        drawIntoCanvas { canvas ->
            image.svg.renderToCanvas(canvas.nativeCanvas, RectF(0f, 0f, size.width, size.height))
        }
    }
}

internal actual class LottieAnimation(val composition: LottieComposition) {
    actual fun sizeRatio(): Float = composition.bounds.let { it.width().toFloat() / it.height().toFloat() }
    actual companion object {
        actual suspend fun create(bytes: ByteArray): LottieAnimation =
            withContext(Dispatchers.Default) {
                val result = LottieCompositionFactory.fromJsonInputStreamSync(ByteArrayInputStream(bytes), null)
                LottieAnimation(result.value ?: throw result.exception!!)
            }
    }
}

@Composable
internal actual fun LottieAnimation(
    animation: LottieAnimation,
    iterations: Int,
    skipLastFrame: Boolean,
    stopAt: Float,
    speed: Float,
    contentDescription: String,
    modifier: Modifier
) {
    require(iterations > 0) { "Invalid iterations" }
    require(stopAt in 0f..1f) { "Invalid stopAt" }
    require(speed > 0f) { "Invalid speed" }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        val end =
            if (skipLastFrame) {
                1f - (1f / animation.composition.durationFrames)
            }
            else 1f
        repeat(iterations) {
            progress.snapTo(0f)
            val isLastIteration = it == (iterations - 1)
            val target = if (isLastIteration) end * stopAt else end
            progress.animateTo(
                targetValue = target,
                animationSpec = tween((animation.composition.duration * speed * target).roundToInt(), easing = LinearEasing)
            )
        }
    }

    ALottieAnimation(
        composition = animation.composition,
        progress = { progress.value },
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Image
            }
    )
}
