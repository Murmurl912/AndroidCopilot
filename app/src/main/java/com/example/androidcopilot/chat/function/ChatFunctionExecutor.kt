package com.example.androidcopilot.chat.function

import kotlinx.serialization.json.JsonElement

interface ChatFunctionExecutor {

    fun functions(): List<ChatFunction>

    suspend fun execute(function: ChatFunction, args: JsonElement): JsonElement


}