package yukitas.animal.collector.model

data class Animal (
        val id: String,
        var name: String,
        var tags: List<String>,
        var thumbnail: Photo?
)