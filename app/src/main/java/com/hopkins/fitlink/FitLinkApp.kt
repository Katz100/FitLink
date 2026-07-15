package com.hopkins.fitlink

import android.app.Application
import android.content.Context
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltAndroidApp
class FitLinkApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(RemoteTree(applicationContext))
    }
}

private class RemoteTree(
    context: Context
): Timber.Tree() {
    companion object {
        const val FILE_NAME = "logs3"
    }

    private val file = File(context.filesDir, FILE_NAME)

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        file.appendText("$current $tag: $message\n")
        Log.i(tag, message)
    }

}