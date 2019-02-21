package yukitas.animal.collector.model

import java.time.Instant
import java.util.*

data class Photo(
        val id: UUID,
        val content: ByteArray,
        val description: String,
        val createdAt: Instant,
        val location: Location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}