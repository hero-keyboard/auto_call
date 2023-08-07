package com.keyboardhero.call.features.home

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

object CallStateSubscribe {
    @OptIn(ObsoleteCoroutinesApi::class)
    private val callChannel = BroadcastChannel<String>(Channel.BUFFERED)

    @OptIn(ObsoleteCoroutinesApi::class)
    fun postCallEvent(event: String) {
        callChannel.trySend(event).isSuccess
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    fun getCallEvents(): Flow<String> = callChannel.asFlow()
}
