package com.mrsanglier.tsumegohero.coreui.componants.topbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.THTextResponsive
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.foHazeChild
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeSource

@Composable
fun THTopBar(
    title: TextSpec?,
    hazeState: HazeState,
    elevation: Dp,
    modifier: Modifier = Modifier,
    navAction: TopBarAction? = null,
    trailingAction: TopBarAction? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    maxLines: Int = 1,
    /** Replaces the [title] text, e.g. by a [TopBarTitleTextField] to edit it */
    titleContent: @Composable (() -> Unit)? = null,
) {
    val backgroundColor by animateColorAsState(if (elevation == 0.dp) THTheme.colors.background else THTheme.colors.surface1)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .foHazeChild(
                state = hazeState,
                backgroundColor = backgroundColor,
            )
            .statusBarsPadding()
            .zIndex(1f),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(TopBarMinHeight)
                .padding(THTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Use box to ensure padding if there is no navAction
            Box {
                navAction?.Content()
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (titleContent != null) {
                    titleContent()
                } else if (title != null) {
                    THTextResponsive(
                        text = title,
                        style = THTheme.typography.title200,
                        modifier = Modifier.padding(horizontal = THTheme.spacing.medium),
                        maxLines = maxLines,
                    )
                }
            }
            trailingAction?.Content()
        }
        bottomContent?.invoke()
    }
}

private val TopBarMinHeight: Dp = 56.dp

@Preview
@Composable
fun PreviewTopBar() {
    THTheme {
        val hazeState = remember { HazeState() }
        val lazyListState = rememberLazyListState()
        val topBarElevation by rememberTopBarElevation(lazyListState)

        Box(Modifier.fillMaxSize().background(THTheme.colors.contentTint))
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = THTheme.colors.background,
            topBar = {
                THTopBar(
                    title = "TopBar".toTextSpec(),
                    hazeState = hazeState,
                    navAction = TopBarAction.back {},
                    elevation = topBarElevation,
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.hazeSource(hazeState),
                state = lazyListState,
                contentPadding = it,
            ) {
                for (i in 1..50) {
                    item {
                        THText(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Item $i".toTextSpec(),
                            style = THTheme.typography.title100,
                        )
                    }
                }
            }
        }
    }
}