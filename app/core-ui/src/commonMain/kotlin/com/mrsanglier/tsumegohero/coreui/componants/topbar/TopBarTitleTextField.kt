package com.mrsanglier.tsumegohero.coreui.componants.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.drawLineBottom
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

/** Editable title of a top bar: same style as the title, underlined while editing. */
@Composable
fun TopBarTitleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: TextSpec? = null,
    maxLines: Int = 3,
) {
    val focusManager = LocalFocusManager.current
    val textStyle = THTheme.typography.title200.copy(color = THTheme.colors.content)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(THTheme.colors.contentTint),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = THTheme.spacing.medium)
            .drawLineBottom(stroke = 1.dp, color = THTheme.colors.strokeTint),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.padding(vertical = THTheme.spacing.xsmall)) {
                if (value.isEmpty() && placeholder != null) {
                    THText(
                        text = placeholder,
                        style = THTheme.typography.title200,
                        color = THTheme.colors.contentSecondary,
                        maxLines = maxLines,
                    )
                }
                innerTextField()
            }
        },
    )
}
