package com.mrsanglier.tsumegohero.dashboard.screens.collection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.coreui.componants.screen.LocalPiScreenPadding
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen

@Composable
fun CollectionRoute() {
    CollectionScreen()
}

@Composable
private fun CollectionScreen() {
    THScreen(
        modifier = Modifier.padding(
            bottom = LocalPiScreenPadding.current.calculateBottomPadding(),
        ),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}
