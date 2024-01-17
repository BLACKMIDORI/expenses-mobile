package com.blackmidori.familyexpenses.android.shared.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SimpleScaffold(topBar: @Composable ()->Unit,content: @Composable BoxScope.() -> Unit) {
    Scaffold(
        topBar = {
            topBar()
        }
    )
    { innerPadding ->
        Box(
            Modifier.padding(innerPadding)
        ) {
            content()
        }
    }
}