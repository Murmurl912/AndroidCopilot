package com.example.androidcopilot.command

interface FunctionCommand<Input, Output> {

    suspend fun execute(input: Input): Output

}
