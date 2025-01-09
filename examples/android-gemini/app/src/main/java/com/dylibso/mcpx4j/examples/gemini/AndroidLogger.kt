package com.dylibso.mcpx4j.examples.gemini

import android.util.Log
import com.dylibso.chicory.log.Logger

class AndroidLogger(val tag: String) : Logger {
    override fun log(level: Logger.Level?, msg: String?, throwable: Throwable?) {
        Log.i(tag, msg, throwable)
    }
    override fun isLoggable(level: Logger.Level): Boolean = true
}
