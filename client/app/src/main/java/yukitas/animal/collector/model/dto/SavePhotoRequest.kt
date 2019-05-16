package yukitas.animal.collector.model.dto

data class SavePhotoRequest(val animalIds: List<String>, val albumIds: List<String>,
                            val description: String)