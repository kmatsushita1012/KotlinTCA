package com.example.kotlin_tca.core.tca

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*

@Composable
fun <Item> FullScreenNavigation(
    item: Item?,
    screenContent: @Composable (Item) -> Unit,
    content: @Composable () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        if (item != null) {
            screenContent(item)
        }else{
            content()
        }
    }
}
