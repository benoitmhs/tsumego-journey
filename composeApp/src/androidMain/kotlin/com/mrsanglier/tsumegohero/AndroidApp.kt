package com.mrsanglier.tsumegohero

import android.app.Application
import androidx.core.content.edit
import com.mrsanglier.tsumegohero.app.coreui.resources.Res
import com.mrsanglier.tsumegohero.localdatasources.utils.TsumegoDatabasePath
import com.mrsanglier.tsumegohero.utils.PreferenceKey
import com.mrsanglier.tsumegohero.utils.TsumegoAsset
import com.mrsanglier.tsumegohero.utils.TsumegoAssetVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class AndroidApp : Application() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sharedPref by lazy {
        getSharedPreferences("sharedPref", MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        coroutineScope.launch(Dispatchers.IO) {
            initTsumegoDatabase()
        }
    }

    private suspend fun initTsumegoDatabase() {
        val currentAssetVersion = sharedPref.getInt(PreferenceKey.CURRENT_TSUMEGO_DATABASE_VERSION, -1)
        if (TsumegoAssetVersion > currentAssetVersion) {
            try {
                val inputStream = Res.readBytes("files/$TsumegoAsset").inputStream()
                val dbFile = getDatabasePath(TsumegoDatabasePath)

                dbFile.parentFile?.mkdirs()

                inputStream.use { input ->
                    FileOutputStream(dbFile).use { output ->
                        input.copyTo(output)
                    }
                }

                sharedPref.edit {
                    putInt(PreferenceKey.CURRENT_TSUMEGO_DATABASE_VERSION, TsumegoAssetVersion)
                }
            } catch (e: Exception) {
                println("INIT: failed to copy dictionary asset: ${e.message}")
            }
        }
    }
}