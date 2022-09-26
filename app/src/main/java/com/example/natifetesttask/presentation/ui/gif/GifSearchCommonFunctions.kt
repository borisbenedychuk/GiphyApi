package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.utils.compose.fillMaxSmallestWidth
import com.example.natifetesttask.presentation.utils.compose.isInLandScape

@Composable
fun DeleteIcon(
    item: GifItem,
    onDeleteItem: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "delete gif",
        tint = Color(0xFFF80000),
        modifier = modifier
            .fillMaxSmallestWidth(if (isInLandScape()) 0.1f else 0.15f)
            .aspectRatio(1f)
            .shadow(5.dp, CircleShape)
            .clickable(
                enabled = true,
                interactionSource = remember(::MutableInteractionSource),
                indication = rememberRipple(color = Color(0xFFFF0303)),
                onClick = { onDeleteItem(item.id) },
            )
            .background(color = Color.White, shape = CircleShape)
            .clip(CircleShape)
            .padding(if (isInLandScape()) 8.dp else 12.dp),
    )
}