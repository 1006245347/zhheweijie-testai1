/*
 * Copyright 2023 Joel Kanyi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hwj.ai.global

import androidx.compose.ui.graphics.Color

sealed class UiEvents {
    data class ShowToast(val message: String) : UiEvents()
    data object Navigation : UiEvents()
    data object NavigateBack : UiEvents()
}


//AsyncImage(
//    model = Res.getUri("drawable/sample.jpg"),
//    contentDescription = null,
//)

//Android15无法响应颜色切换
fun isDarkTxt(): Color { //onTertiary =
    return BackTxtColor2
}

fun isLightTxt(): Color {
    return BackTxtColor1
}

fun isDarkBg(): Color { //onSecondary
    return BackHumanColor2
}

fun isLightBg(): Color {
    return BackHumanColor1
}

fun isDarkPanel():Color{ //onPrimary
    return BackInnerColor2
}

fun isLightPanel():Color{
    return BackInnerColor1
}




