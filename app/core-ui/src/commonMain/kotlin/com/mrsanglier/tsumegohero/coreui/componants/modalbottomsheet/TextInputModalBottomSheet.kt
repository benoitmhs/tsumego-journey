package com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStatus
import com.mrsanglier.tsumegohero.coreui.componants.button.THButton
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.componants.textfield.InputType
import com.mrsanglier.tsumegohero.coreui.componants.textfield.THTextField
import com.mrsanglier.tsumegohero.coreui.componants.textfield.TrailingTextFieldOption
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
fun TextInputModalBottomSheet(
    headerIcon: IconSpec,
    headerText: TextSpec,
    buttonText: TextSpec,
    onValidate: (String) -> Unit,
    textFieldPlaceholder: TextSpec,
    initialValue: String = "",
    state: MutableState<String> = rememberSaveable { mutableStateOf(initialValue) },
    errorToShown: (String) -> TextSpec? = { null },
    textFieldTrailingAction: TrailingTextFieldOption.ActionIcon? = null,
    /** Focuses the text field (and opens the keyboard) when the sheet appears */
    requestFocus: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }
    if (requestFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }

    val error by remember(state.value) {
        derivedStateOf { errorToShown(state.value) }
    }
    val buttonState by remember(state.value, error) {
        derivedStateOf {
            if (state.value.isBlank() || error != null) ButtonStatus.Disabled else ButtonStatus.Enabled
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(THTheme.spacing.large),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            THIcon(
                icon = headerIcon,
                iconSize = IconSize.Regular,
            )
            THText(
                text = headerText,
                style = THTheme.typography.title100,
            )
        }
        THTextField(
            value = state.value,
            onValueChange = { state.value = it },
            description = "".toTextSpec(),
            placeholder = textFieldPlaceholder,
            keyboardOptions = InputType.Default.getKeyboardOption(imeAction = ImeAction.Done),
            trailingAction = textFieldTrailingAction,
            errorText = error,
            modifier = Modifier
                .padding(vertical = THTheme.spacing.large)
                .focusRequester(focusRequester),
        )
        THButton(
            text = buttonText,
            onClick = {
                onValidate(state.value)
            },
            status = buttonState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = THTheme.spacing.large),
        )
    }
}
