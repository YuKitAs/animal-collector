package yukitas.animal.collector.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import yukitas.animal.collector.model.Animal
import yukitas.animal.collector.repository.AnimalRepository

class AnimalViewModel(context: Application) : AndroidViewModel(context) {
    fun getAnimalsByCategory(categoryId: String): LiveData<List<Animal>> {
        return AnimalRepository.fetchAnimalsByCategory(categoryId)
    }
}