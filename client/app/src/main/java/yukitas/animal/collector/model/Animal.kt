package yukitas.animal.collector.model

data class Animal(
        override val id: String,
        override val category: Category,
        override var name: String,
        var tags: List<String>,
        override var thumbnail: Photo?
) : Collection