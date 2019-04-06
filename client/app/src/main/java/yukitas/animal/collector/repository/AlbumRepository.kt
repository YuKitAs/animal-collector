package yukitas.animal.collector.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import yukitas.animal.collector.model.Album
import yukitas.animal.collector.model.Photo
import yukitas.animal.collector.networking.ApiService

object AlbumRepository {
    private val TAG = AlbumRepository::class.java.simpleName
    private val apiService by lazy { ApiService.create() }

    fun fetchAlbumsByCategoryId(categoryId: String): LiveData<List<Album>> {
        val albums: MutableLiveData<List<Album>> = MutableLiveData()
        apiService.getAlbumsByCategory(categoryId).enqueue(object : Callback<List<Album>> {
            override fun onResponse(call: Call<List<Album>>?, response: Response<List<Album>>?) {
                if (response!!.isSuccessful) {
                    albums.value = response.body()!!
                    Log.d(TAG, "Fetched albums by category $categoryId: ${albums.value}")
                } else {
                    Log.e(TAG, "Fetching albums by category $categoryId responded with\n$response")
                }
            }

            override fun onFailure(call: Call<List<Album>>?, t: Throwable?) {
                Log.e(TAG, "Fetching albums by category $categoryId failed", t)
            }
        })

        return albums
    }

    fun fetchAlbumThumbnail(albumId: String): LiveData<Photo?> {
        val photo: MutableLiveData<Photo> = MutableLiveData()
        AlbumRepository.apiService.getAlbumThumbnail(albumId).enqueue(object : Callback<Photo> {
            override fun onResponse(call: Call<Photo>, response: Response<Photo>?) {
                if (response!!.isSuccessful) {
                    photo.value = response.body()
                    Log.d(AlbumRepository.TAG, "Fetched latest photo by album $albumId")
                } else {
                    Log.w(AlbumRepository.TAG, "Fetching latest photo by album $albumId responded with\n$response")
                }
            }

            override fun onFailure(call: Call<Photo>, t: Throwable) {
                Log.e(AlbumRepository.TAG, "Fetching latest photo by album $albumId failed", t)
            }
        })

        return photo
    }
}