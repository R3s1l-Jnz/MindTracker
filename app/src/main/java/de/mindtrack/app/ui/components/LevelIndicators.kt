package de.mindtrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BatteryIndicator(
    level: Int?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(24.dp)
                .border(2.dp, color, RoundedCornerShape(5.dp))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((level ?: 0).coerceIn(0, 5) / 5f)
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(10.dp)
                .background(color, RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
        )
    }
}

@Composable
fun TensionBars(
    level: Int?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(30.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        val heights = listOf(8, 13, 18, 23, 28)
        heights.forEachIndexed { index, height ->
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(height.dp)
                    .background(
                        color.copy(alpha = if (index < (level ?: 0)) 1f else 0.18f),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}
