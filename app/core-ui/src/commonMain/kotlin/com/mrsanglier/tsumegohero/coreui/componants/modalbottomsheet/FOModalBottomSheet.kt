package com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import kotlinx.coroutines.launch

interface THModalBottomSheetState {
    val onDismiss: () -> Unit

    @Composable
    fun Content(hideBottomSheet: () -> Unit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun THModalBottomSheetState.Composable() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        shape = THTheme.shape.bottomSheet,
        containerColor = THTheme.colors.surface1,
    ) {
        Content(
            hideBottomSheet = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismiss()
                    }
                }
            }
        )
    }
}
