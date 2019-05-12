package yukitas.animal.collector.model

data class Album(
        val id: String,
        var name: String,
        val photos: List<Photo>,
        var thumbnail: Photo?
)