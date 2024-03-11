package com.example.androidcopilot.app.log

import android.util.Log
import com.example.androidcopilot.app.log.AppLogger

object LogcatLogger: AppLogger {

    override fun info(tag: String, error: Throwable?, message: () -> String?) {
        Log.i(tag, message(), error)
    }

    override fun debug(tag: String, error: Throwable?, message: () -> String?) {
        Log.e(tag, message(), error)
    }

    override fun warn(tag: String, error: Throwable?, message: () -> String?) {
        Log.w(tag, message(), error)
    }

    override fun error(tag: String, error: Throwable?, message: () -> String?) {
        Log.e(tag, message(), error)
    }

}