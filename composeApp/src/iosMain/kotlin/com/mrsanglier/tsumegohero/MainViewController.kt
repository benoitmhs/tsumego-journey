package com.mrsanglier.tsumegohero

import androidx.compose.ui.window.ComposeUIViewController
import com.mrsanglier.tsumegohero.app.coreui.resources.Res
import com.mrsanglier.tsumegohero.di.THKoinApplication
import com.mrsanglier.tsumegohero.localdatasources.databasePath
import com.mrsanglier.tsumegohero.utils.PreferenceKeyIOS
import com.mrsanglier.tsumegohero.utils.TsumegoAsset
import com.mrsanglier.tsumegohero.utils.TsumegoAssetVersion
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import platform.Foundation.NSData
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.UIViewController

@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController = ComposeUIViewController {
    runBlocking {
        initTsugemoDatabase()
    }
    THKoinApplication {
        App()
    }
}

@OptIn(ExperimentalForeignApi::class)
private suspend fun initTsugemoDatabase() {
    val preferences = NSUserDefaults.standardUserDefaults
    val currentTsumegoDBVersion = preferences.integerForKey(PreferenceKeyIOS.CURRENT_TSUMEGO_DATABASE_VERSION)

    if (currentTsumegoDBVersion >= TsumegoAssetVersion) return

    try {
        val destinationPath = databasePath()

        val copyFileSuccess = Res.readBytes("files/$TsumegoAsset")
            .usePinned { pinned ->
                NSData.create(
                    bytes = pinned.addressOf(0),
                    length = pinned.get().size.toULong()
                )
            }.writeToFile(
                path = destinationPath,
                atomically = true
            )

        if (!copyFileSuccess) throw IOException("failed to copy file")

        preferences.setInteger(
            value = TsumegoAssetVersion.toLong(),
            forKey = PreferenceKeyIOS.CURRENT_TSUMEGO_DATABASE_VERSION,
        )
    } catch (e: Exception) {
        println("INIT: failed to copy dictionary asset: ${e.message}")
    }
}