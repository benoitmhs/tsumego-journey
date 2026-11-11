package zip

import com.mrsanglier.tsumegohero.domain.collection.utils.SgfFile
import com.mrsanglier.tsumegohero.domain.collection.utils.SgfZipReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

class SgfZipReaderTest : FunSpec({

    test("reads the sgf files of every folder and ignores system and other files") {
        val fileSystem = FakeFileSystem()
        val reader = SgfZipReader(fileSystem = fileSystem, tempDirectory = "/tmp".toPath())
        val zip = storedZip(
            "b.sgf" to "(;SZ[9]AB[aa])",
            "folder/a.SGF" to "(;SZ[9]AW[bb])",
            "readme.txt" to "hello",
            "__MACOSX/folder/._a.SGF" to "metadata",
            ".hidden.sgf" to "hidden",
        )

        reader.read(zip) shouldBe listOf(
            SgfFile(name = "b", content = "(;SZ[9]AB[aa])"),
            SgfFile(name = "a", content = "(;SZ[9]AW[bb])"),
        )
        // The temporary archive is removed
        fileSystem.list("/tmp".toPath()) shouldBe emptyList()
    }
})

/** Minimal zip archive without compression (method 0), enough for the zip file system. */
private fun storedZip(vararg entries: Pair<String, String>): ByteArray {
    val out = Buffer()
    val central = Buffer()
    entries.forEach { (name, content) ->
        val data = content.encodeToByteArray()
        val nameBytes = name.encodeToByteArray()
        val crc = crc32(data)
        val offset = out.size.toInt()
        out.writeIntLe(0x04034b50).writeShortLe(20).writeShortLe(0).writeShortLe(0)
            .writeShortLe(0).writeShortLe(0x21)
            .writeIntLe(crc).writeIntLe(data.size).writeIntLe(data.size)
            .writeShortLe(nameBytes.size).writeShortLe(0)
            .write(nameBytes).write(data)
        central.writeIntLe(0x02014b50).writeShortLe(20).writeShortLe(20).writeShortLe(0).writeShortLe(0)
            .writeShortLe(0).writeShortLe(0x21)
            .writeIntLe(crc).writeIntLe(data.size).writeIntLe(data.size)
            .writeShortLe(nameBytes.size).writeShortLe(0).writeShortLe(0)
            .writeShortLe(0).writeShortLe(0).writeIntLe(0).writeIntLe(offset)
            .write(nameBytes)
    }
    val centralOffset = out.size.toInt()
    val centralSize = central.size.toInt()
    out.writeAll(central)
    out.writeIntLe(0x06054b50).writeShortLe(0).writeShortLe(0)
        .writeShortLe(entries.size).writeShortLe(entries.size)
        .writeIntLe(centralSize).writeIntLe(centralOffset).writeShortLe(0)
    return out.readByteArray()
}

private fun crc32(data: ByteArray): Int {
    var crc = 0xFFFFFFFF.toInt()
    data.forEach { byte ->
        crc = crc xor (byte.toInt() and 0xFF)
        repeat(8) { crc = if (crc and 1 != 0) (crc ushr 1) xor 0xEDB88320.toInt() else crc ushr 1 }
    }
    return crc.inv()
}
