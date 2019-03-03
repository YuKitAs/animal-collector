package yukitas.animal.collector.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.repository.AlbumRepository

class AlbumViewModel(context: Application) : AndroidViewModel(context) {
    fun getAlbumsByCategory(categoryId: String): LiveData<List<Album>> {
        return AlbumRepository.fetchAlbumsByCategoryId(categoryId)
    }
}