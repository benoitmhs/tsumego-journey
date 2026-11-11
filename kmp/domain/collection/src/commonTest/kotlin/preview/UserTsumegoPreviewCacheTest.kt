package preview

import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.domain.collection.utils.PreviewGeometry
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoPreviewCache
import com.mrsanglier.tsumegohero.game.model.BoardSize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

class UserTsumegoPreviewCacheTest : FunSpec({

    fun tsumego(id: String, sgf: String, title: String? = null) = UserTsumego(
        id = id,
        collectionId = "c",
        title = title,
        rank = null,
        notes = null,
        sgf = sgf,
        createdAt = Instant.fromEpochSeconds(0),
    )

    fun cacheCountingReads(read: MutableList<String>, unreadable: Set<String> = emptySet()) =
        UserTsumegoPreviewCache { tsumego ->
            read += tsumego.id
            if (tsumego.sgf in unreadable) null else PreviewGeometry(BoardSize.X9, emptySet(), emptySet(), null)
        }

    test("a problem is read again only when its sgf changed") {
        val read = mutableListOf<String>()
        val cache = cacheCountingReads(read)

        cache.previews(listOf(tsumego("a", "sgf-a"), tsumego("b", "sgf-b")))
        cache.previews(listOf(tsumego("a", "sgf-a", title = "renamed"), tsumego("b", "sgf-b2")))

        read shouldBe listOf("a", "b", "b")
    }

    test("title changes are applied without reading the sgf") {
        val cache = cacheCountingReads(mutableListOf())

        cache.previews(listOf(tsumego("a", "sgf-a", title = "old")))
        cache.previews(listOf(tsumego("a", "sgf-a", title = "new"))).single().title shouldBe "new"
    }

    test("unreadable problems are skipped and not read again") {
        val read = mutableListOf<String>()
        val cache = cacheCountingReads(read, unreadable = setOf("broken"))

        cache.previews(listOf(tsumego("a", "broken"), tsumego("b", "sgf-b"))).map { it.id } shouldBe listOf("b")
        cache.previews(listOf(tsumego("a", "broken"), tsumego("b", "sgf-b")))

        read shouldBe listOf("a", "b")
    }

    test("a deleted problem leaves the cache") {
        val read = mutableListOf<String>()
        val cache = cacheCountingReads(read)

        cache.previews(listOf(tsumego("a", "sgf-a")))
        cache.previews(emptyList())
        cache.previews(listOf(tsumego("a", "sgf-a")))

        read shouldBe listOf("a", "a")
    }
})
