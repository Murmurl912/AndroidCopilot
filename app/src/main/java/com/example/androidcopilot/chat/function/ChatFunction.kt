package com.example.androidcopilot.chat.function

import kotlinx.serialization.json.JsonElement

interface ChatFunction {

    val name: String
    val description: String
    val parameters: JsonElement

    suspend fun execute(args: JsonElement): JsonElement

}