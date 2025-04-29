package com.hwj.ai.ui.global

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hwj.ai.global.cBlue629DE8
import com.hwj.ai.global.cDeepLine
import com.hwj.ai.global.cHalfGrey80717171
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import kotlinx.coroutines.Dispatchers

/**
 * @author by jason-何伟杰，2025/4/29
 * des:图片预览
 */
@Composable
fun PreviewImageDialog(filePath: String, isDark: Boolean, onBack: () -> Unit) {
    Box(Modifier.padding(30.dp).fillMaxSize().clip(RoundedCornerShape(10.dp))) {

        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, if (isDark) cBlue629DE8() else cDeepLine()),
            modifier =Modifier.align(Alignment.Center).background(cHalfGrey80717171())
        ) {
            AsyncImage(
                filePath, contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.align(Alignment.Center).wrapContentSize()
            )
        }

        IconButton(
            onClick = onBack,
            modifier =
            Modifier
                .align(Alignment.TopStart)
                .padding(top = 35.dp, start = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close Preview Dialog",
                tint = Color.White, modifier = Modifier.background(cHalfGrey80717171())
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {

        }
    }
}

@Composable
fun PreviewImageDialog(file: PlatformFile, isDark: Boolean, onBack: () -> Unit) {
    PreviewImageDialog(file.absolutePath(), isDark, onBack)
}