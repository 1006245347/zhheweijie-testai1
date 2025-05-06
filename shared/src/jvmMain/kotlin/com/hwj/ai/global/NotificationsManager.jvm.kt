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

import com.hwj.ai.global.notification.linux.LinuxNotificationProvider
import com.hwj.ai.global.notification.macos.MacOsNotificationProvider
import com.hwj.ai.global.notification.windows.Toast4jNotificationProvider
import com.hwj.ai.utils.Sound

actual class NotificationsManager {
    actual fun showNotification(title: String, description: String) {
        Sound.playSound()
        val hostOs = System.getProperty("os.name")
        Toast4jNotificationProvider.init()
        when {
            hostOs.startsWith("Windows") -> {
                Toast4jNotificationProvider.sendNotification(
                    title = title,
                    description = description,
                )
            }

            hostOs.startsWith("Mac") -> {
                MacOsNotificationProvider.sendNotification(
                    title = title,
                    description = description,
                )
            }

            hostOs.startsWith("Linux") -> {
                LinuxNotificationProvider.sendNotification(
                    title = title,
                    description = description,
                )
            }

            else -> {
                println("Unsupported OS: $hostOs")
            }
        }
    }
}
