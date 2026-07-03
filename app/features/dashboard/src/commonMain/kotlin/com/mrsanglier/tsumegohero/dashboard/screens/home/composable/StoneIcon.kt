package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.app.coreui.resources.stone_black
import com.mrsanglier.tsumegohero.app.coreui.resources.stone_white
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.shape.THShape
import com.mrsanglier.tsumegohero.game.model.Stone
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun StoneIcon(
    stone: Stone,
    modifier: Modifier = Modifier,
) {
    val drawable = remember(stone) {
        when (stone) {
            Stone.BLACK -> THDrawable.stone_black
            Stone.WHITE -> THDrawable.stone_white
        }
    }
    Stone(drawable = drawable, modifier = modifier)
}

@Composable
private fun Stone(
    drawable: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .background(
                    color = THTheme.colors.background,
                    shape = THShape.circle,
                )
        )
    }
}