package yukitas.animal.collector.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.repository.AnimalRepository

class AnimalViewModel(context: Application) : AndroidViewModel(context) {
    fun getAnimalsByCategory(categoryId: String): LiveData<List<Animal>> {
        return AnimalRepository.fetchAnimalsByCategory(categoryId)
    }

    fun getAnimalsByPhoto(photoId: String): LiveData<List<Animal>> {
        return AnimalRepository.fetchAnimalsByPhoto(photoId)
    }

    fun getAnimalThumbnail(animalId: String): LiveData<Photo?> {
        return AnimalRepository.fetchAnimalThumbnail(animalId)
    }
}