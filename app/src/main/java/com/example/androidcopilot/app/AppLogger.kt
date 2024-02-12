package com.example.androidcopilot.app

interface AppLogger {

    fun info(tag: String, error: Throwable? = null, message: () -> String? = {null})

    fun debug(tag: String, error: Throwable? = null, message: () -> String? = {null})

    fun warn(tag: String, error: Throwable? = null, message: () -> String? = {null})

    fun error(tag: String, error: Throwable? = null, message: () -> String? = {null})

}