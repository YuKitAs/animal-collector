package yukitas.animal.collector.model

data class Album(
        override val id: String,
        override val category: Category,
        override var name: String,
        override var thumbnail: Photo?
): Collection