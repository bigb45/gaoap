package natour.developer.poc

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random


class ScaleNode() : DrawModifierNode, Modifier.Node() {
    var isPressed = false

    var currentPosition: Offset = Offset.Zero
    val animatedScalePercent = Animatable(1f)

    val scope = CoroutineScope(Dispatchers.Main + Job())
    fun animateToPressed(){
        coroutineScope.launch {
            animatedScalePercent.animateTo(0.9f)
        }
    }

    fun animateToReleased(){

        coroutineScope.launch{ animatedScalePercent.animateTo(1f) }
    }

    override fun ContentDrawScope.draw() {
        scale(
            scale = animatedScalePercent.value, pivot = currentPosition
        ) {
            this@draw.drawContent()
        }
    }




}

private data class ScaleElement(
    val isPressed: Boolean, val interactionSource: InteractionSource
) : ModifierNodeElement<ScaleNode>() {
    override fun create(): ScaleNode {
        return ScaleNode()
    }

    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        return Random.nextInt()
    }

    override fun update(node: ScaleNode) {
        Timber.d("pressed value updated: $isPressed")
        node.isPressed = isPressed

        if(isPressed){
            node.animateToPressed()
        }else{
            node.animateToReleased()
        }
    }

}


fun Modifier.customIndication(isPressed: Boolean, interactionSource: InteractionSource) =
    this then ScaleElement(isPressed = isPressed, interactionSource = interactionSource)
