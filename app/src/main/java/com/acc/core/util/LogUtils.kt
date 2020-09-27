package com.acc.core.util

import android.util.Log


object LogUtils {

    private val TAG = "HomeSpeeder"

    private var debug = true

    fun init(mode: Boolean) {
        debug = mode
    }

    fun d(tag: String, msg: String) {
        if (debug) {
            Log.d(tag, msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (debug) {
            Log.v(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (debug) {
            Log.i(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
    }

    fun printError(e: Throwable) {
        if (debug) {
            e.printStackTrace()
        }
    }
}
