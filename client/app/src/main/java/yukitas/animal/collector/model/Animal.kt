package yukitas.animal.collector.model

import java.util.*

data class Animal(
        val id: UUID,
        val name: String,
        val tags: List<String>
)