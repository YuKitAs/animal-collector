package yukitas.animal.collector.model

import java.util.*

data class Album(
        val id: UUID,
        val name: String,
        val photos: List<Photo>
)