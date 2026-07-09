package com.mrsanglier.tsumegohero.coreui.theme.shape

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.mrsanglier.tsumegohero.coreui.theme.dimens.THCornerRadius

object THShape {
    val rect: Shape = RectangleShape
    val circle: Shape = CircleShape
    val roundMicro: Shape = RoundedCornerShape(THCornerRadius.micro)
    val roundSmall: Shape = RoundedCornerShape(THCornerRadius.small)
    val roundMedium: Shape = RoundedCornerShape(THCornerRadius.medium)
    val roundLarge: Shape = RoundedCornerShape(THCornerRadius.large)

    val textField: Shape = RoundedCornerShape(
        topStart = THCornerRadius.small,
        topEnd = THCornerRadius.small,
        bottomStart = THCornerRadius.none,
        bottomEnd = THCornerRadius.none,
    )

    val bottomSheet: Shape = RoundedCornerShape(
        topStart = THCornerRadius.huge,
        topEnd = THCornerRadius.huge,
        bottomStart = THCornerRadius.none,
        bottomEnd = THCornerRadius.none,
    )

    val topBanner: Shape = RoundedCornerShape(
        topStart = THCornerRadius.none,
        topEnd = THCornerRadius.none,
        bottomStart = THCornerRadius.huge,
        bottomEnd = THCornerRadius.huge,
    )
}