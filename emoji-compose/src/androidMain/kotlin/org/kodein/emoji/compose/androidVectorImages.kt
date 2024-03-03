package org.kodein.emoji.compose

import android.graphics.RectF
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
import com.airbnb.lottie.compose.LottieConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.airbnb.lottie.compose.LottieAnimation as ALottieAnimation
import java.io.ByteArrayInputStream
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
internal actual fun LottieAnimation(animation: LottieAnimation, contentDescription: String, modifier: Modifier) {
    ALottieAnimation(
        composition = animation.composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Image
            }
    )
}
