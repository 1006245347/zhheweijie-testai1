package com.hwj.ai.global

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object EventHelper {

    private val _events = MutableStateFlow<Event?>(null)

    val events = _events.asStateFlow()
    fun post(event: Event?) {
        println("post>$event")
        _events.value = event
    }
}

sealed class Event {

    data class GlobalEvent(val code:Int) : Event()

    data object RefreshEvent : Event()

    data class HotKeyEvent(val code :Int,val time:Long):Event()
}