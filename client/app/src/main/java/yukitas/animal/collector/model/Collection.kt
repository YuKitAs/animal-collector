package yukitas.animal.collector.model

interface Collection {
    val id: String
    val category: Category
    var name: String
    var thumbnail: Photo?
}