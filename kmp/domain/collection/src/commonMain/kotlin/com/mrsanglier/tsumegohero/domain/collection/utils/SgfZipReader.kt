package com.mrsanglier.tsumegohero.domain.collection.utils

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.openZip
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** An .sgf file found in an archive. @property name file name without extension */
data class SgfFile(
    val name: String,
    val content: String,
)

/** Extracts the .sgf files of a zip archive, in every folder of the archive. */
class SgfZipReader(
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
    private val tempDirectory: Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY,
) {
    /**
     * @return the .sgf files sorted by path in natural order ("p8" before "p12");
     * system files (`__MACOSX`, hidden files) are ignored
     */
    @OptIn(ExperimentalUuidApi::class)
    fun read(zipBytes: ByteArray): List<SgfFile> {
        // The zip file system reads from a file: the archive is written to a temporary one
        val zipPath = tempDirectory / "collection-import-${Uuid.random()}.zip"
        fileSystem.createDirectories(tempDirectory)
        try {
            fileSystem.write(zipPath) { write(zipBytes) }
            val zip = fileSystem.openZip(zipPath)
            val root = "/".toPath()
            return zip.listRecursively(root)
                .filter { path -> path.isSgf() && zip.metadataOrNull(path)?.isRegularFile == true }
                .sortedWith(compareBy(NaturalOrderComparator) { it.toString() })
                .map { path ->
                    SgfFile(
                        name = path.name.substringBeforeLast('.'),
                        content = zip.read(path) { readUtf8() },
                    )
                }
                .toList()
        } finally {
            fileSystem.delete(zipPath, mustExist = false)
        }
    }

    private fun Path.isSgf(): Boolean =
        name.endsWith(SGF_EXTENSION, ignoreCase = true) &&
            !name.startsWith(".") &&
            segments.none { it == MAC_OS_METADATA_FOLDER }

    private companion object {
        const val SGF_EXTENSION = ".sgf"
        const val MAC_OS_METADATA_FOLDER = "__MACOSX"
    }
}
