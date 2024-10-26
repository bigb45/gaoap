package natour.developer.poc

import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.PocTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        enableEdgeToEdge()
        setContent {
            PocTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MyButton(
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(15.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MyButton(
    modifier: Modifier = Modifier, onClick: () -> Unit = {},
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {/*TODO: add mask if button is disabled */
    var isPressed by remember { mutableStateOf(false) }
    val translation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = tween(60),
        label = "translationAnimation",
    )
    Box(modifier = modifier
        .customIndication(isPressed, interactionSource)

        .hardShadow(
            borderRadius = 16.dp,
            isEnabled = enabled,
        )
        .offset { IntOffset(0, translation.roundToPx()) }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
//                    call onClick method
                    onClick()

//                    handle button state
                    isPressed = true && enabled
                    awaitRelease()
                    isPressed = false
                },
            )
        }
//        .indication(
//            interactionSource = interactionSource,
//            indication = ScaleIndication
//        )
        .clip(
            shape = RoundedCornerShape(size = 16.dp)
        )
        .background(colorScheme.primary)
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 8.dp)

    ) {

        Text(
            color = colorScheme.onPrimary, text = "Click me", style = TextStyle(
                fontWeight = FontWeight.Light, fontSize = 28.sp
            ), modifier = Modifier.align(alignment = Alignment.Center)
        )

    }
}


@Composable
fun Modifier.hardShadow(
    borderRadius: Dp, elevation: Dp = 4.dp, isEnabled: Boolean

): Modifier {
    val color = LocalContentColor.current
    val bottomPixel = remember { Animatable(0f) }
    val elevationPx = with(LocalDensity.current) { elevation.toPx() }
    LaunchedEffect(isEnabled) {
        bottomPixel.animateTo(
            targetValue = if (isEnabled) elevationPx else 0f,
            animationSpec = tween(durationMillis = 50)
        )
    }
    return this.then(this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()

            frameworkPaint.color = color.toArgb()

            val noShadow = 0.toDp().toPx()

            val rightPixel = size.width

            canvas.drawRoundRect(
                left = noShadow,
                top = noShadow + 10,
                right = rightPixel,
                bottom = size.height + bottomPixel.value,
                paint = paint,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx()
            )
        }
    })
}


fun Color.getShadow(): Color {
    return this.copy(alpha = 0.5f).compositeOver(Color.Black)
}


@Preview
@Composable
private fun MyButtonPreview() {
    PocTheme {
        MyButton()
    }
}


