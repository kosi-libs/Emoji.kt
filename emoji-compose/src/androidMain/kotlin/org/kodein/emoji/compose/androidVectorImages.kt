package org.kodein.emoji.compose

import android.graphics.RectF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.airbnb.lottie.compose.LottieAnimation as ALottieAnimation
import java.io.ByteArrayInputStream
import kotlin.math.roundToInt
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
    stopAt: Float,
    speed: Float,
    contentDescription: String,
    modifier: Modifier
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        repeat(iterations) {
            progress.snapTo(0f)
            val isLastIteration = it == (iterations - 1)
            val target = if (isLastIteration) stopAt else 1f
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
