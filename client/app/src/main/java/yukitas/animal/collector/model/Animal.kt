package yukitas.animal.collector.model

data class Animal(
        val id: String,
        val category: Category,
        var name: String,
        var tags: List<String>,
        var thumbnail: Photo?
)