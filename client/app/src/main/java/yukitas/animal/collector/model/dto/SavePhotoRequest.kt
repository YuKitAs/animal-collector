package yukitas.animal.collector.model.dto

data class SavePhotoRequest(val albumIds: List<String>,
                            val animalIds: List<String>,
                            val description: String)