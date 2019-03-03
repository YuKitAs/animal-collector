package yukitas.animal.collector.model

data class Photo(
        val id: String,
        val content: String,
        val description: String,
        val createdAt: String,
        val location: Location)