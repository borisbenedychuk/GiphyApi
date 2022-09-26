package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.theme.ui.NatifeTheme
import com.example.natifetesttask.presentation.utils.compose.fillMaxSmallestWidth

@Composable
fun DeleteIcon(
    item: GifItem,
    onDeleteItem: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "delete gif",
        tint = MaterialTheme.colors.error,
        modifier = modifier
            .fillMaxSmallestWidth(NatifeTheme.dimensions.cardDeleteButtonWidthPercent)
            .aspectRatio(1f)
            .shadow(5.dp, CircleShape)
            .clickable(
                enabled = true,
                interactionSource = remember(::MutableInteractionSource),
                indication = rememberRipple(color = MaterialTheme.colors.error),
                onClick = { onDeleteItem(item.id) },
            )
            .background(color = MaterialTheme.colors.surface, shape = CircleShape)
            .clip(CircleShape)
            .padding(NatifeTheme.dimensions.cardDeleteButtonIconPadding),
    )
}