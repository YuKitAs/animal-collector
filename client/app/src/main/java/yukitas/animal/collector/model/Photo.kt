package yukitas.animal.collector.model

data class Photo(
        val id: String,
        val content: String,
        val description: String,
        val createdAt: String,
        val location: Location) {
    override fun toString(): String {
        return String.format("Photo (id=%s, description='%s', createdAt=%s, location=%s)", id, description, createdAt, location)
    }
}