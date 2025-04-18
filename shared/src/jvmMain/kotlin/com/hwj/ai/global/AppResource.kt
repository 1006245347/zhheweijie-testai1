package com.hwj.ai.global

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.VoiceChat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

class AppResource(val icon: VectorPainter)


val LocalAppResource = staticCompositionLocalOf<AppResource> {
    error("Local hwj res isn't provided")
}


@Composable
fun rememberAppResource(): AppResource {
    val icon = rememberVectorPainter(Icons.Default.VoiceChat, Color.Yellow)
    return remember { AppResource(icon) }
}

@Composable
fun rememberVectorPainter(image: ImageVector, tintColor: Color) =
    rememberVectorPainter(
        defaultWidth = image.defaultWidth,
        defaultHeight = image.defaultHeight,
        viewportWidth = image.viewportWidth,
        viewportHeight = image.viewportHeight,
        name = image.name,
        tintColor = tintColor,
        tintBlendMode = image.tintBlendMode,
        autoMirror = false,
        content = { _, _ -> RenderVectorGroup(group = image.root) }
    )