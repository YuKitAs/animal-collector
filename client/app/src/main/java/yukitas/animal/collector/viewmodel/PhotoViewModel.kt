package yukitas.animal.collector.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.repository.PhotoRepository

class PhotoViewModel(context: Application) : AndroidViewModel(context) {
    fun getPhotosByAlbum(albumId: String): LiveData<List<Photo>> {
        return PhotoRepository.fetchPhotosByAlbumId(albumId)
    }

    fun getPhotosByAnimal(animalId: String): LiveData<List<Photo>> {
        return PhotoRepository.fetchPhotosByAnimalId(animalId)
    }

    fun getPhotoById(id: String): LiveData<Photo> {
        return PhotoRepository.fetchPhotoById(id)
    }
}