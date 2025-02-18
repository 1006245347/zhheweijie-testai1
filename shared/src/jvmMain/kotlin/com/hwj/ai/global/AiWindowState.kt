package com.hwj.ai.global

import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState


class AiWindowState(private val application:AiApplicationState,
    private val exit:(AiWindowState)->Unit) {

    val window = WindowState()
    private suspend fun askToSave(): Boolean {
        // do sth
        return true
    }

    fun toggleFullScreen(){
        window.placement=if (window.placement==WindowPlacement.Fullscreen){
            WindowPlacement.Floating
        }else{
            WindowPlacement.Fullscreen
        }
    }

    fun newWindow(){
        application.newWindow()
    }

    fun sendNotification(notification: Notification){
        application.sendNotification(notification)
    }

    suspend fun exit():Boolean{
        askToSave()
        exit(this)
        return true
    }
}