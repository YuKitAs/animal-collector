package yukitas.animal.collector.model

data class Photo(
        val id: String,
        val content: ByteArray,
        val description: String,
        val createdAt: String,
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