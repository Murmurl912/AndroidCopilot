package com.example.androidcopilot.chat

import android.util.Log

object LogcatLogger: AppLogger {

    override fun info(tag: String, error: Throwable?, message: () -> String?) {
        Log.e(tag, message(), error)
    }

    override fun debug(tag: String, error: Throwable?, message: () -> String?) {
        Log.e(tag, message(), error)
    }

    override fun warn(tag: String, error: Throwable?, message: () -> String?) {
        Log.e(tag, message(), error)
    }

    override fun error(tag: String, error: Throwable?, message: () -> String?) {
        Log.e(tag, message(), error)
    }

}