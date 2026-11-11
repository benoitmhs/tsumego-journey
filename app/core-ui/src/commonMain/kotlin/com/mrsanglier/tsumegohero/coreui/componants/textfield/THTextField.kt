package com.mrsanglier.tsumegohero.coreui.componants.textfield

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.componants.iconbutton.FOIconButton
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.color.Transparent
import com.mrsanglier.tsumegohero.coreui.utils.AnimatedNullableVisibility

@Composable
fun THTextField(
    value: String,
    onValueChange: (String) -> Unit,
    description: TextSpec,
    modifier: Modifier = Modifier,
    title: TextSpec? = null,
    label: TextSpec? = null,
    placeholder: TextSpec? = null,
    leadingIcon: IconSpec? = null,
    trailingAction: TrailingTextFieldOption.ActionIcon? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
    errorText: TextSpec? = null,
    isError: Boolean = errorText != null,
    enabled: Boolean = true,
    colors: TextFieldColors = thOutlinedTextFieldColors(),
    textStyle: TextStyle = THTheme.typography.content200,
) {
    val contentDescription = description.string

    Column(
        modifier = modifier.animateContentSize(),
    ) {
        title?.let {
            THText(
                text = it,
                style = THTheme.typography.title100,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .padding(bottom = THTheme.spacing.small),
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    this.contentDescription = contentDescription
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            placeholder = placeholder?.let {
                {
                    THText(
                        text = it,
                        color = Color.Unspecified,
                        style = THTheme.typography.content200,
                    )
                }
            },
            label = label?.let {
                {
                    THText(
                        text = it,
                        color = Color.Unspecified,
                        style = LocalTextStyle.current.merge(fontFamily = THTheme.typography.content200.fontFamily),
                    )
                }
            },
            leadingIcon = leadingIcon?.let {
                {
                    THIcon(
                        icon = it,
                        iconSize = IconSize.Regular,
                        tint = Color.Unspecified,
                    )
                }
            },
            trailingIcon = {
                AnimatedNullableVisibility(
                    value = trailingAction,
                ) { action ->
                    FOIconButton(
                        icon = action.icon,
                        onClick = action.onClick,
                    )
                }
            },
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            minLines = minLines,
            singleLine = maxLines == 1,
            shape = THTheme.shape.roundMedium,
            colors = colors,
        )
        errorText?.let {
            THText(
                text = it,
                style = THTheme.typography.content100,
                color = THTheme.colors.contentCritical,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = THTheme.spacing.tiny,
                        start = THTheme.spacing.medium,
                    ),
            )
        }
    }
}

@Composable
internal fun thOutlinedTextFieldColors(): TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
    unfocusedBorderColor = Transparent,
    focusedBorderColor = THTheme.colors.strokeSecondary,
    cursorColor = THTheme.colors.contentTint,
    textColor = THTheme.colors.content,
    unfocusedLabelColor = THTheme.colors.contentSecondary,
    focusedLabelColor = THTheme.colors.contentTint,
    trailingIconColor = THTheme.colors.content,
    placeholderColor = THTheme.colors.contentSecondary,
    disabledTextColor = THTheme.colors.contentDisable,
    disabledLabelColor = THTheme.colors.contentDisable,
    disabledBorderColor = Transparent,
    disabledPlaceholderColor = THTheme.colors.contentDisable,
    errorTrailingIconColor = THTheme.colors.contentCritical,
    errorBorderColor = Transparent,
    errorCursorColor = THTheme.colors.contentCritical,
    errorLabelColor = THTheme.colors.contentCritical,
    errorLeadingIconColor = THTheme.colors.contentCritical,
    backgroundColor = THTheme.colors.surface2,
    leadingIconColor = THTheme.colors.content,
    disabledLeadingIconColor = THTheme.colors.contentDisable,
    disabledTrailingIconColor = THTheme.colors.contentDisable,
)
