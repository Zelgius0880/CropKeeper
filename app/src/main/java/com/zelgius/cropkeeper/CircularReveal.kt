package com.zelgius.cropkeeper

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.debugInspectorInfo
import kotlin.math.sqrt

/**A modifier that clips the composable content using an animated circle. The circle will
 *  expand/shrink with an animation whenever [visible] changes.
 *
 *  For more fine-grained control over the transition, see this method's overload, which allows passing
 *  a [State] object to control the progress of the reveal animation.
 *
 *  By default, the circle is centered in the content, but custom positions may be specified using
 *  [revealFrom]. Specified offsets should be between 0 (left/top) and 1 (right/bottom).*/
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.circularReveal(
    visible: Boolean,
    revealFrom: Offset = Offset(0.5f, 0.5f),
): Modifier = composed(
    factory = {
        val factor = updateTransition(visible, label = "Visibility")
            .animateFloat(
                label = "revealFactor") { if (it) 1f else 0f }
        circularReveal(factor, revealFrom)
    },
    inspectorInfo = debugInspectorInfo {
        name = "circularReveal"
        properties["visible"] = visible
        properties["revealFrom"] = revealFrom
    }
)
/**A modifier that clips the composable content using a circular shape. The radius of the circle
 * will be determined by the [transitionProgress].
 *
 * The values of the progress should be between 0 and 1.
 *
 * By default, the circle is centered in the content, but custom positions may be specified using
 *  [revealFrom]. Specified offsets should be between 0 (left/top) and 1 (right/bottom).
 *  */
fun Modifier.circularReveal(
    transitionProgress: State<Float>,
    revealFrom: Offset = Offset(0.5f, 0.5f)
): Modifier {
    return drawWithCache {
        val path = Path()


        val center = revealFrom.mapTo(size)
        val radius = calculateRadius(revealFrom, size)

        path.addOval(Rect(center, radius * transitionProgress.value))

        onDrawWithContent {
            clipPath(path) { this@onDrawWithContent.drawContent() }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CircularRevealEffect(
    visible: Boolean,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    offset: Offset = Offset(0.5f, 0.5f),
    content: @Composable () -> Unit
) {

    Surface(
        Modifier
            .circularReveal(visible, offset)
            .fillMaxSize(),
        color = color
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            content()
        }
    }
}

private fun Offset.mapTo(size: Size): Offset {
    return Offset(x * size.width, y * size.height)
}

private fun calculateRadius(normalizedOrigin: Offset, size: Size) = with(normalizedOrigin) {
    val x = (if (x > 0.5f) x else 1 - x) * size.width
    val y = (if (y > 0.5f) y else 1 - y) * size.height

    sqrt(x * x + y * y)
}
