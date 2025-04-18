/*
 * Copyright 2023 onseok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hwj.ai.camera.icon

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath

internal val IconCustomArrowBack =
    materialIcon("Filled.CustomArrowBack") {
        val startY = 12f
        val startX = 1f
        val arrowWidth = 8f
        val arrowHeight = 14f
        val lineWidth = 14f
        val lineHeight = 2f
        materialPath {
            moveTo(startX, startY)
            lineToRelative(arrowWidth, arrowHeight / 2)
            verticalLineToRelative(-arrowHeight)
            close()
            moveTo(startX + arrowWidth, startY + lineHeight / 2)
            verticalLineToRelative(-lineHeight)
            horizontalLineToRelative(lineWidth)
            verticalLineToRelative(lineHeight)
            close()
        }
    }
