package com.hwj.ai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hwj.ai.global.Greeting

@Composable
fun TestPage() {
    Column {
        Text(text = Greeting().greet())
    }
}