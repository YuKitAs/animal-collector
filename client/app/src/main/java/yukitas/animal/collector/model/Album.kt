package yukitas.animal.collector.model

data class Album(
        val id: String,
        val category: Category,
        var name: String,
        val photos: List<Photo>,
        var thumbnail: Photo?
)