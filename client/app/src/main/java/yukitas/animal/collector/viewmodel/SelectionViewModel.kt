package yukitas.animal.collector.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Animal
import java.util.stream.Collectors

class SelectionViewModel : ViewModel() {
    val selectedAlbums = MutableLiveData<List<Album>>()
    val selectedAnimals = MutableLiveData<List<Animal>>()

    var selectedAlbumIds: List<String> = emptyList()
        private set
    var selectedAnimalIds: List<String> = emptyList()
        private set

    fun selectAlbums(albums: List<Album>) {
        selectedAlbums.value = albums
        selectedAlbumIds = albums.stream().map { album -> album.id }.collect(
                Collectors.toList())
    }

    fun selectAnimals(animals: List<Animal>) {
        selectedAnimals.value = animals
        selectedAnimalIds = animals.stream().map { animal -> animal.id }.collect(
                Collectors.toList())
    }
}